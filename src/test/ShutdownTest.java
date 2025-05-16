import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import util.jdbc.ConnectionPoolSingleton;

/**
 * ShutdownTest
 *
 * Proxy object to tear down the connection pool on shutdown.
 */
public class ShutdownTest {
    @AfterAll
    static void shutdownGlobalPool() {
        ConnectionPoolSingleton.shutdown();
    }

    @Test
    void dummyTest() {
        // STUD for throwback
    }
}
