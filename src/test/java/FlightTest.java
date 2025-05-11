import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.fakeskymeal.dto.AirlineDto;
import util.jdbc.ConnectionPool;
import org.fakeskymeal.dao.FlightDao;
import org.fakeskymeal.dao.exception.DaoException;
import org.fakeskymeal.dao.impl.FlightDaoImpl;
import org.fakeskymeal.dto.FlightDto;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class FlightTest {
    private static final Logger LOGGER = Logger.getLogger(FlightTest.class.getName());
    private static FlightDao flightDao;
    private static ConnectionPool pool;

    @BeforeAll
    static void setUp() {
        pool = new ConnectionPool(3);
        flightDao = new FlightDaoImpl(pool);
    }

    @AfterAll
    static void tearDown() {
        pool.shutdown();
    }

    @Test
    void testGetById() throws DaoException {
        int ADMIN_TEST_ID = 1;
        FlightDto flightDto = flightDao.get(ADMIN_TEST_ID);

        assertNotNull(flightDto);
        assertEquals(ADMIN_TEST_ID, flightDto.getFlightId());
        System.out.println("Get by ID " + ADMIN_TEST_ID + ": " + flightDto.toJson());
    }

    @Test
    void testInvalidGetById() {
        int missingId = -999;
        DaoException exception = assertThrows(
                DaoException.class,
                () -> flightDao.get(missingId),
                "Expected DaoException for missing ID"
        );

        assertTrue(
                exception.getMessage().contains("No entry found"),
                "Expected a 'No record found' error message"
        );
    }

    @Test
    void testGetOneByParam() throws DaoException {
        String field = "airline_company_id";
        int value = 2;
        FlightDto flightDto = flightDao.getRow(field, value);
        assertNotNull(flightDto);
        System.out.println("Get by field " + field + ": " + flightDto.toJson());
    }

    @Test
    void testInvalidGetOneByParam() {
        DaoException exception = assertThrows(
                DaoException.class,
                () -> flightDao.getRow("flight_number", "This should not exist")
        );

        assertTrue(
                exception.getMessage().contains("No entry found"),
                "Expected a 'No record found' error message"
        );
    }

    @Test
    void testCRUD() throws DaoException {
        // Insert a new test entry [Create]
        FlightDto testFlight = new FlightDto();
        testFlight.setAirlineCompanyId(2);
        testFlight.setFlightNumber("DL5678");
        testFlight.setDepartureTime(LocalDateTime.now());
        testFlight.setArrivalTime(LocalDateTime.now().plusHours(2));

        flightDao.save(testFlight);
        System.out.println("Inserted new flight with ID: " + testFlight.getFlightId());

        assertTrue(testFlight.getFlightId() > 0, "Flight ID should be set after insert");

        // Read the test entry [Read]
        FlightDto retrievedFlight = flightDao.get(testFlight.getFlightId());

        System.out.println("\nBefore update:");
        System.out.println("Returned Flight(" + retrievedFlight.getFlightId() + "):" + retrievedFlight.toJson());

        // Update the test entry [Update]
        String[] updatedParams = {"2", "DL9101", LocalDateTime.now().toString(), LocalDateTime.now().plusHours(2).toString()};
        flightDao.update(retrievedFlight, updatedParams);

        assertNotNull(retrievedFlight);
        assertEquals(Integer.parseInt(updatedParams[0]), retrievedFlight.getAirlineCompanyId());
        assertEquals(updatedParams[1], retrievedFlight.getFlightNumber());
        assertEquals(updatedParams[2], retrievedFlight.getDepartureTime().toString());
        assertEquals(updatedParams[3], retrievedFlight.getArrivalTime().toString());

        System.out.println("\nAfter update:");
        System.out.println("Returned Flight(" + retrievedFlight.getFlightId() + "):" + retrievedFlight.toJson());

        // Delete the test entry [Delete]
        flightDao.delete(retrievedFlight);
        System.out.println("Deleted flight with ID: " + retrievedFlight.getFlightId());

        // Confirm deletion
        assertThrows(DaoException.class, () -> flightDao.get(retrievedFlight.getFlightId()));
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

                    PreparedStatement stmt = conn.prepareStatement(SQLQueries.getProperty("FLIGHT_UPDATE_NUMBER"));
                    stmt.setString(1, "DL6969");
                    stmt.setInt(2, 1);

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
    void testDumpTable() throws DaoException {
        List<FlightDto> flightDtos = flightDao.getAll();
        assertNotNull(flightDtos, "The flight list should not be null");
        assertFalse(flightDtos.isEmpty(), "Expected at least one flight record");

        System.out.println("Dumping flight records:");
        for (FlightDto flightDto : flightDtos) {
            System.out.println("Returned Flight(" + flightDto.getFlightId() + "):" + flightDto.toJson());
        }
    }
}
