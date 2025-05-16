import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * MainTestSuite
 *
 * Test suite that sequentially execute the connection pool test
 * before proceeding to the entities to test.
 */
@Suite
@SelectClasses({
    ConnectionPoolTest.class,
    DaoTestSuite.class
})
public class MainTestSuite {}
