package mcolomer.connectors.sqlredo;


import mcolomer.connectors.sqlredo.config.SqlRedoSinkConnectorConfig;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.sink.SinkRecord;

@FunctionalInterface
public interface RecordValidator {

    RecordValidator NO_OP = (record) -> { };

    void validate(SinkRecord record);

    default RecordValidator and(RecordValidator other) {
        if (other == null || other == NO_OP || other == this) {
            return this;
        }
        if (this == NO_OP) {
            return other;
        }
        RecordValidator thisValidator = this;
        return (record) -> {
            thisValidator.validate(record);
            other.validate(record);
        };
    }

    static RecordValidator create(SqlRedoSinkConnectorConfig config) {
        RecordValidator requiresKey = requiresKey(config);
        RecordValidator requiresValue = requiresValue(config);

        RecordValidator keyValidator = NO_OP;
        RecordValidator valueValidator = NO_OP;


        // Compose the validator that may or may be NO_OP
        return keyValidator.and(valueValidator);
    }

    static RecordValidator requiresValue(SqlRedoSinkConnectorConfig config) {
        return record -> {
            Schema valueSchema = record.valueSchema();
            if (record.value() != null
                    && valueSchema != null
                    && valueSchema.type() == Schema.Type.STRUCT) {
                return;
            }
            throw new ConnectException(
                    String.format(
                            "Sink connector '%s' is configured with '%s=%s' and '%s=%s' and therefore requires "

                    )
            );
        };
    }

    static RecordValidator requiresKey(SqlRedoSinkConnectorConfig config) {
        return record -> {
            Schema keySchema = record.keySchema();
            if (record.key() != null
                    && keySchema != null
                    && (keySchema.type() == Schema.Type.STRUCT || keySchema.type().isPrimitive())) {
                return;
            }
            throw new ConnectException(
                    String.format(
                            "Sink connector  "
                    )
            );
        };
    }
}
