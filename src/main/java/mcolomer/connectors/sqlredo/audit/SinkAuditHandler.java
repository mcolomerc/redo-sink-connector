package mcolomer.connectors.sqlredo.audit;

import mcolomer.connectors.sqlredo.OracleJDBCWriter;
import mcolomer.connectors.sqlredo.SqlRedoSinkTask;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.sink.SinkRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Date;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.Queue;

public class SinkAuditHandler {

    private static final Logger log = LoggerFactory.getLogger(SinkAuditHandler.class);

    private Queue<SinkAudit> buffer;
    OracleJDBCWriter writer;
    public static final String TABLE = "ReplAudit";

    private static final String CREATE_TABLE = "CREATE TABLE " + TABLE
            + " (operation VARCHAR(100), tableName VARCHAR(100), sourceTs TIMESTAMP, destTs TIMESTAMP, latency NUMBER(10))";


    public SinkAuditHandler(OracleJDBCWriter writer) {
        buffer = new LinkedList<>();
        this.writer = writer;
    }

    public synchronized void auditSinkRecord (SinkRecord record, Timestamp ts) {
        SinkAudit audit = this.build(record,ts);
        this.addSinkAudit(audit);
    }

    public void flushAudit() {

        while (!buffer.isEmpty()) {
            try {
                log.info ("Write Audit");
                writeSinkAudit(pollSinkAudit());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // Add an SQL sentence to the buffer
    private synchronized void addSinkAudit(SinkAudit sinkaudit) {
        buffer.add(sinkaudit);
        notify(); // Notify any waiting threads that there's a new SinkAudit in the buffer
    }

    // Retrieve and remove the next SQL sentence from the buffer
    private synchronized SinkAudit pollSinkAudit() throws InterruptedException {
        while (buffer.isEmpty()) {
            wait(); // Wait if the buffer is empty
        }
        return buffer.poll(); // Retrieves and removes the head of this queue
    }

    private void writeSinkAudit (SinkAudit adtRecord) {
            log.info ("Audit Record " + adtRecord);
            log.info ("Audit Record " + adtRecord.toSQLInsert());
            // Producer topic / Database
        try {
            writer.executeSQL(adtRecord.toSQLInsert());
        } catch (SQLException e) {
            log.error ("flushAudit >> Check Table exists >> " + CREATE_TABLE);
            e.printStackTrace();
        }
    }

    public SinkAudit build(SinkRecord record, Timestamp destinationTs) {
      Struct val = (Struct) record.value();
      String operation = (String) val.get("OPERATION");
      String tableName = (String) val.get("TABLE_NAME");
      Date ts = (Date) val.get("TIMESTAMP");
      Timestamp sourceTs = new Timestamp(ts.getTime());
      long millisecondsDiff = destinationTs.getTime() - sourceTs.getTime();
      return new SinkAudit(operation,tableName, sourceTs, destinationTs,millisecondsDiff);
  }


}
