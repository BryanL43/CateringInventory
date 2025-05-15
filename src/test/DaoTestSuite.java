import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
    AirlineTest.class,
    FlightTest.class,
    BeverageTest.class,
    ShutdownTest.class
})
public class DaoTestSuite {}
