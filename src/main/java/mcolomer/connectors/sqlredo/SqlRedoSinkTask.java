package mcolomer.connectors.sqlredo;

import mcolomer.connectors.sqlredo.audit.SinkAuditHandler;
import mcolomer.connectors.sqlredo.config.SqlRedoSinkConnectorConfig;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.sink.SinkTask;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;

import org.apache.kafka.connect.sink.ErrantRecordReporter;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SqlRedoSinkTask extends SinkTask {
    private static final Logger log = LoggerFactory.getLogger(SqlRedoSinkTask.class);

    ErrantRecordReporter reporter;

    SqlRedoSinkConnectorConfig config;

    OracleJDBCWriter dbSink;

    SinkAuditHandler audit;

    @Override
    public void start(final Map<String, String> props) {
        log.info("Starting SqlRedoSinkTask  ");
        config = new SqlRedoSinkConnectorConfig(props);

        try {
            initWriter();
            audit = new SinkAuditHandler(dbSink);
        } catch (NoSuchMethodError | NoClassDefFoundError e) {
            // Will occur in Connect runtimes earlier than 2.6
            reporter = null;
        }
    }

    void initWriter() {
        log.info("Initializing JDBC writer");
        log.info(config.toString());
        String connectionUrl = config.getString(SqlRedoSinkConnectorConfig.CONNECTION_URL);
        String connectionUsername = config.getString(SqlRedoSinkConnectorConfig.CONNECTION_USER);
        String connectionPassword = config.getPasswordValue(SqlRedoSinkConnectorConfig.CONNECTION_PASSWORD);
        reporter = context.errantRecordReporter();
        dbSink = new OracleJDBCWriter(connectionUrl, connectionUsername, connectionPassword);
        log.info("JDBC writer initialized");
    }

    @Override
    public void put(Collection<SinkRecord> records) {
        if (records.isEmpty()) {
            return;
        }
        final SinkRecord first = records.iterator().next();
        final int recordsCount = records.size();
        log.info(
                "SqlRedoSinkTask task - Received {} records. First record kafka coordinates:({}-{}-{}). Writing them to the "
                        + "database...",
                recordsCount, first.topic(), first.kafkaPartition(), first.kafkaOffset()
        );
        try {
            for (SinkRecord record : records) {
                log.info(":: " + record.value());
                Struct val = (Struct) record.value();
                String statement = (String) val.get("SQL_REDO");
                log.info(":: " + statement);
                dbSink.executeSQL(statement);
                audit.auditSinkRecord(record, new Timestamp(System.currentTimeMillis()));
            }

        } catch (SQLException ex) {
                log.error (ex.toString());
        } catch (Exception tace) {
            if (reporter != null) {
                unrollAndRetry(records);
            } else {
                log.error(tace.toString());
                throw tace;
            }
        }
    }

    private void unrollAndRetry(Collection<SinkRecord> records) {
        initWriter();
        for (SinkRecord record : records) {
            try {
                log.debug ("writer.write(Collections.singletonList(record))");
            } catch (Exception tace) {
                log.debug(tace.toString());
                reporter.report(record, tace);

            }
        }
    }

    @Override
    public void flush(Map<TopicPartition, OffsetAndMetadata> map) {
        log.info ("Flush ");
        audit.flushAudit();
    }

    public void stop() {
        log.info("Stopping task");
    }

    @Override
    public String version() {
        return "0.0.0";
    }

}
