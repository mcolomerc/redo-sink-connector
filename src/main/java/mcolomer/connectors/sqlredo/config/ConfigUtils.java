package mcolomer.connectors.sqlredo.config;

import java.util.Map;

public class ConfigUtils {

        /**
         * Get the connector's name from the configuration.
         *
         * @param connectorProps the connector properties
         * @return the concatenated string with delimiters
         */
        public static String connectorName(Map<?, ?> connectorProps) {
            Object nameValue = connectorProps.get("name");
            return nameValue != null ? nameValue.toString() : null;
        }
    }

