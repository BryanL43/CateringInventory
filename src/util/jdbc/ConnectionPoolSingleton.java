package util.jdbc;

/**
 * ConnectionPoolSingleton
 *
 * Instantiate a connection pool singleton to avoid spawning multiple pools.
 */
public class ConnectionPoolSingleton {
    private static final ConnectionPool instance = new ConnectionPool(3);

    private ConnectionPoolSingleton() {
        // STUB
    }

    public static ConnectionPool getInstance() {
        return instance;
    }

    public static void shutdown() {
        instance.shutdown();
    }
}
