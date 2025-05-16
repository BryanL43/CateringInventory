package util.jdbc;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * ConnectionPool
 *
 * The connection pool object for managing JDBC connections
 * to the database.
 */
public class ConnectionPool {
    private static final Logger LOGGER = Logger.getLogger(ConnectionPool.class.getName());
    private final BlockingQueue<Connection> pool;
    private final int poolSize;
    private boolean isShutdown = false;

    public ConnectionPool(int size) {
        pool = new ArrayBlockingQueue<>(size);
        poolSize = size;
        try {
            for (int i = 0; i < size; i++) {
                pool.put(JdbcConnection.createConnection());
            }
            System.out.println("Connection pool initialized with " + size + " connections.");
        } catch (SQLException | InterruptedException e) {
            LOGGER.log(Level.SEVERE, "Failed to instantiate connection pool", e);
            throw new RuntimeException("Unable to initialize connection pool", e);
        }
    }

    /**
     * getConnection
     *
     * Method overload getConnection method with a default timeout.
     *
     * @return Connection - The JDBC connection.
     * @throws SQLException Any exceptions that occur with JDBC connection.
     */
    public Connection getConnection() throws SQLException {
        return getConnection(3000); // Default timeout of 3 seconds
    }

    /**
     * getConnection
     *
     * Acquire a JDBC connection to the database with a defined timeout duration.
     *
     * @param long timeoutMillis - The set timeout to wait for an available connection.
     * @return Connection - The JDBC connection.
     * @throws SQLException Any exceptions that occur with JDBC connection.
     */
    public Connection getConnection(long timeoutMillis) throws SQLException {
        if (isShutdown) {
            throw new IllegalStateException("Connection pool is already shut down");
        }

        try {
            Connection conn = pool.poll(timeoutMillis, TimeUnit.MILLISECONDS);
            if (conn == null) {
                throw new SQLException("Timeout while waiting for a database connection");
            }
            return conn;
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            LOGGER.warning("Interrupted while waiting for a connection");
            throw new SQLException("Interrupted while waiting for a connection", ex);
        }
    }

    /**
     * releaseConnection
     *
     * Returns the acquired JDBC connection back to the pool and does not close it.
     *
     * @param Connection conn - The JDBC connection to be released back to the pool.
     */
    public void releaseConnection(Connection conn) {
        try {
            if (conn != null && !conn.isClosed()) {
                pool.put(conn);
            }
        } catch (SQLException | InterruptedException e) {
            LOGGER.warning("Failed to release connection, closing it: " + e.getMessage());
            try {
                conn.close(); // fail-safe close
            } catch (SQLException se) {
                LOGGER.warning("Also failed to close connection: " + se.getMessage());
            }
        } // finally will unintentionally close it from pool, so it's not necessary
    }

    /**
     * shutdown
     *
     * Shutdown the connection pool.
     * It closes all the connection within the pool.
     */
    public void shutdown() {
        Connection conn;
        while ((conn = pool.poll()) != null) {
            try {
                conn.close();
            } catch (SQLException ex) {
                LOGGER.warning("Failed to close DB connection: " + ex.getMessage());
            }
        }
        isShutdown = true;
        System.out.println("Connection pool shut down successfully.");
    }

    public int getSize() {
        return poolSize;
    }

    public void dumpPool(String message) {
        System.out.println("\n====================");
        System.out.println(message);
        for (Connection connection : pool) {
            System.out.println(connection);
        }
        System.out.println("====================\n");
    }
}
