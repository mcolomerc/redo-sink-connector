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

## Build

Maven build:
 
`mvn clean install` 

Connector JAR: 
`./target/oracle-sql-redo-sink-1.0-SNAPSHOT-jar-with-dependencies.jar`

## Deploy Connect

`CONNECT_PLUGIN_PATH: "/usr/share/java,/usr/share/confluent-hub-components"`
 
