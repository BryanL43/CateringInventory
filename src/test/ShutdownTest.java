import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import util.jdbc.ConnectionPoolSingleton;

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
