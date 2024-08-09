package mcolomer.connectors.sqlredo;

import java.util.Optional;

import mcolomer.connectors.sqlredo.config.SqlRedoSinkConnectorConfig;
import org.apache.kafka.common.config.Config;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigValue;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.sink.SinkConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class SqlRedoSinkConnector extends SinkConnector {

    private static final Logger log = LoggerFactory.getLogger(SqlRedoSinkConnector.class);

    private Map<String, String> configProps;

    public Class<? extends Task> taskClass() {
        return SqlRedoSinkTask.class;
    }

    @Override
    public List<Map<String, String>> taskConfigs(int maxTasks) {
        log.info("Setting task configurations for {} workers.", maxTasks);
        final List<Map<String, String>> configs = new ArrayList<>(maxTasks);
        for (int i = 0; i < maxTasks; ++i) {
            configs.add(configProps);
        }
        return configs;
    }

    @Override
    public void start(Map<String, String> props) {
        configProps = props;
    }

    @Override
    public void stop() {
    }

    @Override
    public ConfigDef config() {
        return SqlRedoSinkConnectorConfig.baseConfigDef();
    }

    @Override
    public Config validate(Map<String, String> connectorConfigs) {
        /** get configuration parsed and validated individually */
       return super.validate(connectorConfigs);
    }

    /** only if individual validation passed. */
    private Optional<ConfigValue> configValue(Config config, String name) {
        return config.configValues()
                .stream()
                .filter(cfg -> name.equals(cfg.name())
                        && cfg.errorMessages().isEmpty())
                .findFirst();
    }

    @Override
    public String version() {
        return "0.0.1";
    }
}