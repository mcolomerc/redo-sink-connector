package mcolomer.connectors.sqlredo.audit;

import mcolomer.connectors.sqlredo.SqlRedoSinkTask;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.sink.SinkRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.Queue;

public class SinkAuditHandler {

    private static final Logger log = LoggerFactory.getLogger(SinkAuditHandler.class);

    private Queue<SinkAudit> buffer;


    public SinkAuditHandler() {
        buffer = new LinkedList<>();
    }

    public synchronized void auditSinkRecord (SinkRecord record, Timestamp ts) {
        SinkAudit audit = this.build(record,ts);
        this.addSinkAudit(audit);
    }

    public void flushAudit() {
        while (buffer.isEmpty()) {
            try {
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

    private void writeSinkAudit (SinkAudit audit) {
        try {
            SinkAudit adtRecord = pollSinkAudit();
            log.info ("Audit Record " + adtRecord);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public SinkAudit build(SinkRecord record, Timestamp destinationTs) {
      Struct val = (Struct) record.value();
      String statement = (String) val.get("SQL_REDO");
      Date ts = (Date) val.get("TIMESTAMP");
      Timestamp sourceTs = new Timestamp(ts.getTime());
      long millisecondsDiff = sourceTs.getTime() - destinationTs.getTime();
      return new SinkAudit(statement,sourceTs, destinationTs,millisecondsDiff);
  }


}
