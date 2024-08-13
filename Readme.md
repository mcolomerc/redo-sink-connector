# Oracle Replication - Connector

 SQL Redo Log Topic Sink - WIP

Reads SQL_REDO field from the `redo-log-topic` messages by the [Oracle CDC Source](https://docs.confluent.io/kafka-connectors/oracle-cdc/current/overview.html) Kafka connector and executes the statement in the Oracle destination. 

**Parse statement**
  
- Remove `;` at the end
- Add `TO_TIMESTAMP` format.
- Remove `and ROWID='xxx'` (UPDATE/DELETE)

**Statements**

- INSERT 
- UPDATE 
- DELETE
- ALTER TABLE 
  - ADD COLUMN
  - MODIFY COLUMN

* Connector configuration:

```json
{
"name": "sqlredo-sink-connector",
"config": {
  "connector.class": "mcolomer.connectors.sqlredo.SqlRedoSinkConnector",
  "connection.url": "jdbc:oracle:thin:@oracleTarget:1521:ORCLCDB",
  "connection.user": "MYUSER",
  "connection.password": "mypassword",
  "topics": "redo-log-topic",
  "value.converter": "io.confluent.connect.avro.AvroConverter",
  "value.converter.schema.registry.url": "http://schemaregistry:8081",
  "key.converter": "io.confluent.connect.avro.AvroConverter",
  "key.converter.schema.registry.url": "http://schemaregistry:8081"
  }
}
```

## Audit Table

Create the table on the destination database: 

`CREATE TABLE ReplAudit (operation VARCHAR(100), tableName VARCHAR(100), sourceTs TIMESTAMP, destTs TIMESTAMP, latency NUMBER(10));`

The sink connector will insert one Audit record per operation:

Example:

Operation Table             Source TS               Destination TS          Latency
INSERT	CUSTOMERS_ORDERS	2024-08-13 17:00:44.000	2024-08-13 17:00:46.262	2262
INSERT	CUSTOMERS_ORDERS	2024-08-13 17:00:44.000	2024-08-13 17:00:46.265	2265
INSERT	CUSTOMERS_ORDERS	2024-08-13 17:00:44.000	2024-08-13 17:00:46.268	2268
INSERT	CUSTOMERS_ORDERS	2024-08-13 17:00:44.000	2024-08-13 17:00:46.270	2270


## Build

Maven build:
 
`mvn clean install` 

Connector JAR: 
`./target/oracle-sql-redo-sink-1.0-SNAPSHOT-jar-with-dependencies.jar`

## Deploy Connect

`CONNECT_PLUGIN_PATH: "/usr/share/java,/usr/share/confluent-hub-components"`
 
