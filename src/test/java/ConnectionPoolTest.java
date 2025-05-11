import util.jdbc.ConnectionPool;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class ConnectionPoolTest {
    private static final Logger LOGGER = Logger.getLogger(ConnectionPoolTest.class.getName());
    private ConnectionPool pool;

    @BeforeEach
    void setUp() {
        int poolSize = 3;
        pool = new ConnectionPool(poolSize);
    }

    @AfterEach
    void tearDown() {
        if (pool != null) {
            pool.shutdown();
        }
    }

    @Test
    void testGetConnection() throws SQLException {
        Connection conn = pool.getConnection();
        assertNotNull(conn, "Connection should not be null");
        assertFalse(conn.isClosed(), "Connection should be open");

        pool.releaseConnection(conn);
    }

    @Test
    // Tests cyclic connection pool queue
    void testCyclicAcquisition() throws SQLException {
        pool.dumpPool("Initial pool state:"); // DEBUG

        // Acquire and release one connection
        Connection conn1 = pool.getConnection();
        pool.releaseConnection(conn1);

        // Fill the pool with other connections (except one spot to avoid blocking)
        List<Connection> otherConnections = new ArrayList<>();
        for (int i = 0; i < pool.getSize() - 1; i++) {
            otherConnections.add(pool.getConnection());
        }

        pool.dumpPool("Acquire all the connections beside one:"); // DEBUG

        // Reacquire from pool — should get conn1 back
        Connection conn2 = pool.getConnection();
        assertSame(conn1, conn2, "Connection released should be reused");

        // Release resources
        pool.releaseConnection(conn2);
        for (Connection conn : otherConnections) {
            pool.releaseConnection(conn);
        }

        pool.dumpPool("End pool state:"); // DEBUG
    }

    @Test
    void testMaxPoolSize() throws SQLException {
        // Exhaust the connection pool
        Connection[] connections = new Connection[pool.getSize()];
        for (int i = 0; i < pool.getSize(); i++) {
            connections[i] = pool.getConnection();
        }

        // Simulate that another thread is blocked by checking if `getConnection()` would block
        Thread t = new Thread(() -> assertThrows(SQLException.class, () -> {
            // Interrupt this thread while waiting
            Thread.currentThread().interrupt();
            pool.getConnection();
        }));
        t.start();
        try {
            t.join();
        } catch (InterruptedException ignored) {
        }

        for (Connection conn : connections) {
            pool.releaseConnection(conn);
        }
    }

    @Test
    void testOverCapacity() throws Exception {
        // Exhaust the connection pool
        Connection[] connections = new Connection[pool.getSize()];
        for (int i = 0; i < pool.getSize(); i++) {
            connections[i] = pool.getConnection();
        }

        // Launch a new thread and verify that connection acquisition is blocked on empty pool
        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
        try (executor) {
            try {
                Future<Connection> future = executor.submit(() -> pool.getConnection());

                try {
                    future.get(500, TimeUnit.MILLISECONDS);
                    fail("Expected ConnectionPoolTest.getConnection to block or fail, but it returned a connection");
                } catch (TimeoutException e) {
                    System.out.println("ConnectionPoolTest.getConnection is blocked as expected due to empty pool.");
                    future.cancel(true);
                } catch (InterruptedException | ExecutionException e) {
                    LOGGER.log(Level.SEVERE, e.getMessage(), e);
                }
            } finally {
                executor.shutdownNow();
            }
        } finally {
            assertTrue(executor.isShutdown());

            // Release resources
            for (Connection conn : connections) {
                pool.releaseConnection(conn);
            }
        }
    }

    @Test
    void testConnectionBlocksUntilAvailable() throws Exception {
        // Exhaust the connection pool
        Connection[] connections = new Connection[pool.getSize()];
        for (int i = 0; i < pool.getSize(); i++) {
            connections[i] = pool.getConnection();
        }

        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
        Future<Connection> future;

        try (executor) {
            // Submit task that will block on getConnection
            future = executor.submit(() -> {
                System.out.println("Waiting for available connection...");
                return pool.getConnection();
            });

            // Wait a bit to ensure the task is likely blocking
            Thread.sleep(500);

            // Release a connection so the blocked thread can proceed
            pool.releaseConnection(connections[0]);

            // The thread should now get the released connection
            Connection acquired = future.get(500, TimeUnit.MILLISECONDS);
            assertNotNull(acquired, "Blocked thread should have acquired a connection");
            System.out.println("Acquired connection: " + acquired);

            pool.releaseConnection(acquired);
        } finally {
            assertTrue(executor.isShutdown());

            for (int i = 1; i < pool.getSize(); i++) {
                pool.releaseConnection(connections[i]);
            }
        }
    }

    @Test
    void testConnectionTimeoutWhenPoolIsEmpty() throws Exception {
        // Exhaust the connection pool
        Connection[] connections = new Connection[pool.getSize()];
        for (int i = 0; i < pool.getSize(); i++) {
            connections[i] = pool.getConnection();
        }

        // Attempt another connection with a 1-second timeout
        long startTime = System.currentTimeMillis();
        SQLException thrown = assertThrows(SQLException.class, () -> pool.getConnection(1000));
        long duration = System.currentTimeMillis() - startTime;

        // Verify timeout behavior
        assertTrue(duration >= 1000, "getConnection should have waited at least 1 second before timing out");
        assertTrue(thrown.getMessage().contains("Timeout"), "Expected timeout-related SQLException message");

        // Release resources
        for (Connection conn : connections) {
            pool.releaseConnection(conn);
        }
    }

    @Test
    void testShutdownClosesConnections() throws SQLException {
        Connection conn = pool.getConnection();
        pool.releaseConnection(conn);
        pool.shutdown();
        assertTrue(conn.isClosed(), "Connections should be closed after shutdown");
        pool = null; // Blocks @AfterEach from double shutdown
    }

    @Test
    // Ensure no leaks or deadlocks after multiple cycles
    void testMultipleAcquireReleaseCycles() throws SQLException {
        for (int i = 0; i < pool.getSize() * 2 + 1; i++) {
            Connection conn = pool.getConnection();
            assertNotNull(conn);
            pool.releaseConnection(conn);
        }
    }
}
