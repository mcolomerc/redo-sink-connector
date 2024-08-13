package mcolomer.connectors.sqlredo.audit;

import java.sql.Timestamp;

public class SinkAudit {
    String operation;
    String tableName;
    Timestamp sourceTimestamp;
    Timestamp destinationTimestamp;
    long latency; //ms

    public SinkAudit(String operation, String tableName, Timestamp sourceTimestamp, Timestamp destinationTimestamp,  long latency) {
        this.operation = operation;
        this.tableName = tableName;
        this.sourceTimestamp = sourceTimestamp;
        this.destinationTimestamp = destinationTimestamp;
        this.latency = latency;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Timestamp getDestinationTimestamp() {
        return destinationTimestamp;
    }

    public void setDestinationTimestamp(Timestamp destinationTimestamp) {
        this.destinationTimestamp = destinationTimestamp;
    }

    @Override
    public String toString() {
        return "SinkAudit{" +
                "operation='" + operation + '\'' +
                ", tableName='" + tableName + '\'' +
                ", sourceTimestamp=" + sourceTimestamp +
                ", destinationTimestamp=" + destinationTimestamp +
                ", latency=" + latency +
                '}';
    }

    public String toSQLInsert() {


        // Format the SQL string with properly escaped values
        return String.format(
                "INSERT INTO " + SinkAuditHandler.TABLE
                        + " (operation, tableName, sourceTs, destTs, latency) "
                        + "VALUES ('%s', '%s', TO_TIMESTAMP('%s', 'YYYY-MM-DD HH24:MI:SS.FF3'), TO_TIMESTAMP('%s', 'YYYY-MM-DD HH24:MI:SS.FF3'), '%s')",
                operation, tableName, sourceTimestamp, destinationTimestamp, latency
        );
    }

    public long getLatency() {
        return latency;
    }

    public void setLatency(long latency) {
        this.latency = latency;
    }


    public Timestamp getSourceTimestamp() {
        return sourceTimestamp;
    }

    public void setSourceTimestamp(Timestamp sourceTimestamp) {
        this.sourceTimestamp = sourceTimestamp;
    }
}
