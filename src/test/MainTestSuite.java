import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
    ConnectionPoolTest.class,
    DaoTestSuite.class
})
public class MainTestSuite {}
