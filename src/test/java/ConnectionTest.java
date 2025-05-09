import org.junit.jupiter.api.Test;
import util.jdbc.JdbcConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ConnectionTest {
    private static final Logger LOGGER = Logger.getLogger(ConnectionTest.class.getName());

    @Test
    public void testStatementExecution() {
        try {
            // Get the JDBC connection
            Connection conn = JdbcConnection.getConnection();
            assertNotNull(conn);

            // Create and execute a simple query (you can modify this)
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM airline_companies;");

            // Check result
            assertNotNull(rs);
            while (rs.next()) {
                System.out.println("Airline: " + rs.getString("name"));
            }

            rs.close();
            stmt.close();
            JdbcConnection.resetConnection();
//            JdbcConnection.checkStatus();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new AssertionError("Test failed due to exception: " + e.getMessage());
        }
    }
}
