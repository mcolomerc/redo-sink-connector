package mcolomer.connectors.sqlredo.config;

import java.util.*;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigDef.Importance;
import org.apache.kafka.common.config.ConfigDef.Type;
import org.apache.kafka.common.config.ConfigDef.Width;

import org.apache.kafka.common.config.types.Password;

public class SqlRedoSinkConnectorConfig extends AbstractConfig {

    public static final ConfigDef CONFIG_DEF = new ConfigDef();

    public static final String DATABASE_GROUP = "Database";
    public static final String CONNECTION_PREFIX = "connection.";
    public static final String CONNECTION_URL_CONFIG = CONNECTION_PREFIX + "url";
    public static final String CONNECTION_URL = CONNECTION_URL_CONFIG;
    private static final String CONNECTION_URL_DOC =
            "JDBC connection URL.\n"
                    + "For example: ``jdbc:oracle:thin:@localhost:1521:orclpdb1``, "
                    + "databaseName=db_name``";
    private static final String CONNECTION_URL_DISPLAY = "JDBC URL";
    private static final String CONNECTION_URL_DEFAULT = "";

    public static final String CONNECTION_USER_CONFIG = CONNECTION_PREFIX + "user";

    public static final String CONNECTION_USER = CONNECTION_USER_CONFIG;
    private static final String CONNECTION_USER_DOC = "JDBC connection user.";
    private static final String CONNECTION_USER_DISPLAY = "JDBC User";

    public static final String CONNECTION_PASSWORD_CONFIG = CONNECTION_PREFIX + "password";
    public static final String CONNECTION_PASSWORD = CONNECTION_PASSWORD_CONFIG ;
    private static final String CONNECTION_PASSWORD_DOC = "JDBC connection password.";
    private static final String CONNECTION_PASSWORD_DISPLAY = "JDBC Password";
    public static final String CONNECTION_ATTEMPTS_CONFIG = CONNECTION_PREFIX + "attempts";
    public static final String CONNECTION_ATTEMPTS = CONNECTION_ATTEMPTS_CONFIG;
    public static final String CONNECTION_ATTEMPTS_DOC
            = "Maximum number of attempts to retrieve a valid JDBC connection. "
            + "Must be a positive integer.";
    public static final String CONNECTION_ATTEMPTS_DISPLAY = "JDBC connection attempts";
    public static final int CONNECTION_ATTEMPTS_DEFAULT = 3;

    public static final String CONNECTION_BACKOFF_CONFIG = CONNECTION_PREFIX + "backoff.ms";

    public static final String CONNECTION_BACKOFF = CONNECTION_BACKOFF_CONFIG;
    public static final String CONNECTION_BACKOFF_DOC
            = "Backoff time in milliseconds between connection attempts.";
    public static final String CONNECTION_BACKOFF_DISPLAY
            = "JDBC connection backoff in milliseconds";
    public static final long CONNECTION_BACKOFF_DEFAULT = 10000L;


    public static ConfigDef baseConfigDef() {
        ConfigDef config = new ConfigDef();
        addDatabaseOptions(config);
        return config;
    }

    private static final void addDatabaseOptions(ConfigDef config) {
        int orderInGroup = 0;
        config.define(
                CONNECTION_URL_CONFIG,
                Type.STRING,
                CONNECTION_URL_DEFAULT,
                Importance.HIGH,
                CONNECTION_URL_DOC,
                DATABASE_GROUP,
                ++orderInGroup,
                Width.LONG,
                CONNECTION_URL_DISPLAY
        ).define(
                CONNECTION_USER_CONFIG,
                Type.STRING,
                null,
                Importance.HIGH,
                CONNECTION_USER_DOC,
                DATABASE_GROUP,
                ++orderInGroup,
                Width.LONG,
                CONNECTION_USER_DISPLAY
        ).define(
                CONNECTION_PASSWORD_CONFIG,
                Type.PASSWORD,
                null,
                Importance.HIGH,
                CONNECTION_PASSWORD_DOC,
                DATABASE_GROUP,
                ++orderInGroup,
                Width.SHORT,
                CONNECTION_PASSWORD_DISPLAY
        ).define(
                CONNECTION_ATTEMPTS_CONFIG,
                Type.INT,
                CONNECTION_ATTEMPTS_DEFAULT,
                ConfigDef.Range.atLeast(1),
                Importance.LOW,
                CONNECTION_ATTEMPTS_DOC,
                DATABASE_GROUP,
                ++orderInGroup,
                Width.SHORT,
                CONNECTION_ATTEMPTS_DISPLAY
        ).define(
                CONNECTION_BACKOFF_CONFIG,
                Type.LONG,
                CONNECTION_BACKOFF_DEFAULT,
                Importance.LOW,
                CONNECTION_BACKOFF_DOC,
                DATABASE_GROUP,
                ++orderInGroup,
                Width.SHORT,
                CONNECTION_BACKOFF_DISPLAY
        );
    }

    public static void main(String... args) {
        System.out.println(CONFIG_DEF.toEnrichedRst());
    }

    public SqlRedoSinkConnectorConfig(Map<?, ?> props) {
        super(baseConfigDef(), props);
    }

    public String getPasswordValue(String key) {
        Password password = getPassword(key);
        if (password != null) {
            return password.value();
        }
        return null;
    }
}
