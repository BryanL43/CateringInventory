import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.fakeskymeal.dao.impl.FlightDaoImpl;
import util.jdbc.ConnectionPool;
import org.fakeskymeal.dao.AirlineDao;
import org.fakeskymeal.dao.exception.DaoException;
import org.fakeskymeal.dao.impl.AirlineDaoImpl;
import org.fakeskymeal.dto.AirlineDto;
import org.fakeskymeal.dao.FlightDao;
import org.fakeskymeal.dto.FlightDto;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class AirlineTest {
    private static final Logger LOGGER = Logger.getLogger(AirlineTest.class.getName());
    private static AirlineDao airlineDao;
    private static ConnectionPool pool;

    @BeforeAll
    static void setUp() {
        pool = new ConnectionPool(3);
        airlineDao = new AirlineDaoImpl(pool);
    }

    @AfterAll
    static void tearDown() {
        pool.shutdown();
    }

    @Test
    void testGetById() throws DaoException {
        int ADMIN_TEST_ID = 1;
        AirlineDto airlineDto = airlineDao.get(ADMIN_TEST_ID);

        assertNotNull(airlineDto);
        assertEquals(ADMIN_TEST_ID, airlineDto.getAirlineId());
        System.out.println("Get by ID " + ADMIN_TEST_ID + ": " + airlineDto.toJson());
    }

    @Test
    void testInvalidGetById() {
        int missingId = -999;
        DaoException exception = assertThrows(
            DaoException.class,
            () -> airlineDao.get(missingId),
            "Expected DaoException for missing ID"
        );

        assertTrue(
            exception.getMessage().contains("No entry found"),
            "Expected a 'No record found' error message"
        );
    }

    @Test
    void testGetOneByParam() throws DaoException {
        String field = "contact_info";
        String value = "Admin@example.com";
        AirlineDto airlineDto = airlineDao.getRow(field, value);
        assertNotNull(airlineDto);
        System.out.println("Get by field " + field + ": " + airlineDto.toJson());
    }

    @Test
    void testInvalidGetOneByParam() {
        DaoException exception = assertThrows(
            DaoException.class,
            () -> airlineDao.getRow("name", "This should not exist")
        );

        assertTrue(
            exception.getMessage().contains("No entry found"),
            "Expected a 'No record found' error message"
        );
    }

    @Test
    void testCRUD() throws DaoException {
        // Insert a new test entry [Create]
        AirlineDto testAirline = new AirlineDto();
        testAirline.setAirlineName("Test Airline");
        testAirline.setContactInfo("Test@example.com");

        airlineDao.save(testAirline);
        System.out.println("Inserted new airline with ID: " + testAirline.getAirlineId());

        assertTrue(testAirline.getAirlineId() > 0, "Airline ID should be set after insert");

        // Read the test entry [Read]
        AirlineDto retrievedAirline = airlineDao.get(testAirline.getAirlineId());

        System.out.println("\nBefore update:");
        System.out.println("Returned Airline(" + retrievedAirline.getAirlineId() + "):" + retrievedAirline.toJson());

        // Update the test entry [Update]
        String[] updatedParams = {"Test Updated Airline", "Updated@example.com"};
        airlineDao.update(retrievedAirline, updatedParams);

        assertNotNull(retrievedAirline);
        assertEquals(updatedParams[0], retrievedAirline.getAirlineName());
        assertEquals(updatedParams[1], retrievedAirline.getContactInfo());

        System.out.println("\nAfter update:");
        System.out.println("Returned Airline(" + retrievedAirline.getAirlineId() + "):" + retrievedAirline.toJson());

        // Delete the test entry [Delete]
        airlineDao.delete(retrievedAirline);
        System.out.println("Deleted airline with ID: " + retrievedAirline.getAirlineId());

        // Confirm deletion
        assertThrows(DaoException.class, () -> airlineDao.get(retrievedAirline.getAirlineId()));
    }

    @Test
    void testConcurrentUpdateContention() {
        Properties SQLQueries = new Properties();
        try {
            SQLQueries.load(
                this.getClass().getClassLoader().getResourceAsStream("sql.properties")
            );
        } catch (IOException io) {
            LOGGER.log(Level.WARNING, "Exception during sql.properties load:", io);
        }

        ExecutorService executor = Executors.newFixedThreadPool(2);
        try (executor) {
            Runnable updateTask = () -> {
                Connection conn = null;
                try {
                    conn = pool.getConnection();
                    conn.setAutoCommit(false);

                    PreparedStatement stmt = conn.prepareStatement(SQLQueries.getProperty("AIRLINE_UPDATE_ID"));
                    stmt.setString(1, "Admin Test Airline");
                    stmt.setString(2, "Admin@example.com");
                    stmt.setInt(3, 16);

                    System.out.println(Thread.currentThread().getName() + " executing update...");
                    int rowsAffected = stmt.executeUpdate();
                    System.out.println(Thread.currentThread().getName() + " updated rows: " + rowsAffected);

                    Thread.sleep(300);

                    conn.commit();
                    System.out.println(Thread.currentThread().getName() + " committed.");
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, Thread.currentThread().getName() + " failed: ", e.getMessage());
                } finally {
                    if (conn != null) {
                        pool.releaseConnection(conn);
                    }
                }
            };

            // Submit two concurrent update tasks
            executor.submit(updateTask);
            executor.submit(updateTask);
        } finally {
            assertTrue(executor.isShutdown());
        }
    }

    @Test
    void testAcquireAllFlightsFromAirlineByName() throws DaoException {
        String companyName = "Delta";
        FlightDao flightDao = new FlightDaoImpl(pool);

        List<FlightDto> flightDtos = flightDao.getFlightsByAirlineName(companyName);

        assertNotNull(flightDtos, "The flight list should not be null");
        assertFalse(flightDtos.isEmpty(), "There should be at least one flight for airline " + companyName);

        for (FlightDto flightDto : flightDtos) {
            System.out.println("Returned Flight(" + flightDto.getFlightId() + "):" + flightDto.toJson());
        }
    }

    @Test
    void testDumpTable() throws DaoException {
        List<AirlineDto> airlineDtos = airlineDao.getAll();
        assertNotNull(airlineDtos, "The airline list should not be null");
        assertFalse(airlineDtos.isEmpty(), "Expected at least one airline record");

        System.out.println("Dumping airline records:");
        for (AirlineDto airlineDto : airlineDtos) {
            System.out.println("Returned Airline(" + airlineDto.getAirlineId() + "):" + airlineDto.toJson());
        }
    }
}
