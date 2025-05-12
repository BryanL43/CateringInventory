import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
    AirlineTest.class,
    FacilityTest.class,
    InventoryTest.class,
    FlightTest.class,
    ShutdownTest.class
})
public class DaoTestSuite {}
