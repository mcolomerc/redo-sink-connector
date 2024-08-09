package mcolomer.connectors.sqlredo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class OracleJDBCWriter {

    private static final Logger log = LoggerFactory.getLogger(OracleJDBCWriter.class);

    private String url;
    private String username;
    private String password;
    private Connection connection;

    // Constructor to initialize connection parameters
    public OracleJDBCWriter(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.connection = null;
    }

    // Method to establish a connection to the Oracle database
    private void connect() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                // Load the Oracle JDBC driver
                Class.forName("oracle.jdbc.driver.OracleDriver");
                // Establish the connection
                connection = DriverManager.getConnection(url, username, password);
                log.info("Connected to the Oracle database successfully!");
            }  catch (SQLException e) {
                log.error("Failed to connect to the Oracle database.");
                e.printStackTrace();
                throw e;
            }  catch (Exception e) {
                log.error("Failed to connect to the Oracle database.");
                e.printStackTrace();
            }
        }
    }

    private static String addTimestampFormat(String sql) {
        // Define the format string you want to add to TO_TIMESTAMP
        String format = "'YYYY-MM-DD HH24:MI:SS.FF3'";

        // Regular expression to match TO_TIMESTAMP without a format model
        String regex = "TO_TIMESTAMP\\(([^,]*?)\\)";

        // Replace the TO_TIMESTAMP call with the one that includes the format
        String formattedSql = sql.replaceAll(regex, "TO_TIMESTAMP($1, " + format + ")");

        return formattedSql;
    }

    // Method to remove the semicolon at the end of an SQL string if it exists
    private static String removeTrailingSemicolon(String sql) {
        if (sql != null) {
            sql = sql.trim(); // Trim any trailing spaces or newline characters
            if (sql.endsWith(";")) {
                sql = sql.substring(0, sql.length() - 1);
            }
        }
        return sql;
    }

    private static String removeRowID(String sql) {
        // Regular expression to match the ROWID condition in the WHERE clause
        String regex = "and\\s+ROWID\\s*=\\s*'[^']*'";

        // Replace the ROWID clause with an empty string
        String modifiedSql = sql.replaceAll(regex, "").trim();

        // Remove any extra spaces left after removing ROWID
        modifiedSql = modifiedSql.replaceAll("\\s{2,}", " ");

        return modifiedSql;
    }


    // Method to execute the received SQL sentence
    public void executeSQL(String sql) throws SQLException {
        Statement stmt = null;
        try {
            // Establish connection if not already connected
            connect();
            // Create a statement object
            stmt = connection.createStatement();

            String sqlParsed = addTimestampFormat (sql);
            sqlParsed = removeRowID(sqlParsed);
            // Execute the SQL statement
            stmt.execute(removeTrailingSemicolon(sqlParsed));
            log.info("SQL statement executed successfully.");
        } catch (SQLSyntaxErrorException e) {
            log.error("Error executing SQL statement.");
            e.printStackTrace();
        } catch (SQLException e) {
            log.error("Error executing SQL statement.");
            e.printStackTrace();
            throw e;
        } finally {
            // Close the statement object
            if (stmt != null) {
                stmt.close();
            }
        }
    }

    // Method to close the database connection
    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
            log.info("Oracle Connection closed successfully.");
        }
    }

    public static void main(String[] args) {
        // Example usage
        OracleJDBCWriter executor = new OracleJDBCWriter(
                "jdbc:oracle:thin:@localhost:1521:ORCLCDB",
                "C##MYUSER",
                "mypassword"
        );

        try {
            // Example SQL statement to execute
            String insert = "insert into CUSTOMERS (first_name, last_name, email, gender, club_status, comments) values ('Java', 'Connector', 'connect@rambler.ru', 'Female', 'bronxxze', 'xx optimal hierarchy')";
            // Execute the SQL statement
            executor.executeSQL(insert);

            // Example SQL statement to execute
            String sql = "insert into \"C##MYUSER\".\"CUSTOMERS\"(\"ID\",\"FIRST_NAME\",\"LAST_NAME\",\"EMAIL\",\"GENDER\",\"CLUB_STATUS\",\"COMMENTS\",\"CREATE_TS\",\"UPDATE_TS\") values ('166','Java','Redo Connector','connect@rambler.ru','Female','bronxxze','xx optimal hierarchy',TO_TIMESTAMP('2024-08-09 15:19:54.320'),NULL);";
            // Execute the SQL statement
            executor.executeSQL(sql);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                // Close the connection
                executor.closeConnection();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}