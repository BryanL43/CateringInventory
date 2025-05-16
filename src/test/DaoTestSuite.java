import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * DaoTestSuite
 *
 * Test suite that sequentially execute the entities to test
 * and clean resources at the end.
 */
@Suite
@SelectClasses({
    AirlineTest.class,
    FlightTest.class,
    BeverageTest.class,
    ShutdownTest.class
})
public class DaoTestSuite {}
