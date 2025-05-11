import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.concurrent.*;

import util.jdbc.ConnectionPool;
import org.fakeskymeal.dao.AirlineDao;
import org.fakeskymeal.dao.exception.DaoException;
import org.fakeskymeal.dao.impl.AirlineDaoImpl;
import org.fakeskymeal.dto.AirlineDto;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class Airline {
    private static AirlineDao airlineDao;
    private static ConnectionPool pool;

    @BeforeAll
    static void setUp() {
        pool = new ConnectionPool(1);
        airlineDao = new AirlineDaoImpl(pool);
    }

    @AfterAll
    static void tearDown() {
        pool.shutdown();
    }

    @Test
    void testGetById() throws DaoException {
        int existingId = 16;
        AirlineDto airlineDto = airlineDao.get(existingId);

        assertNotNull(airlineDto);
        assertEquals(existingId, airlineDto.getAirlineId());
        System.out.println("Get by ID: " + airlineDto.toJson());
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
        AirlineDto airlineDto = airlineDao.getRow("name", "Hawaiian");
        assertNotNull(airlineDto);
        System.out.println("Get by field: " + airlineDto.toJson());
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
        System.out.println("Returned Department(" + retrievedAirline.getAirlineId() + "):" + retrievedAirline.toJson());

        // Update the test entry [Update]
        String[] updatedParams = {"Test Updated Airline", "Updated@example.com"};
        airlineDao.update(retrievedAirline, updatedParams);

        assertNotNull(retrievedAirline);
        assertEquals("Test Updated Airline", retrievedAirline.getAirlineName());
        assertEquals("Updated@example.com", retrievedAirline.getContactInfo());

        System.out.println("\nAfter update:");
        System.out.println("Returned Department(" + retrievedAirline.getAirlineId() + "):" + retrievedAirline.toJson());

        // Delete the test entry [Delete]
        airlineDao.delete(retrievedAirline);
        System.out.println("Deleted airline with ID: " + retrievedAirline.getAirlineId());

        // Confirm deletion
        assertThrows(DaoException.class, () -> {
            airlineDao.get(retrievedAirline.getAirlineId());
        });
    }

    @Test
    void testConcurrentUpdateContention() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(2);

        Runnable updateTask = () -> {
            try (Connection conn = pool.getConnection()) {
                conn.setAutoCommit(false); // Start transaction

                PreparedStatement stmt = conn.prepareStatement(
                        "UPDATE airline_companies SET name = ?, contact_info = ? WHERE id = ?"
                );

                // All threads update the same row (ID = 1) to cause contention
                stmt.setString(1, "Updated Airline " + Thread.currentThread().getName());
                stmt.setString(2, "contact@" + Thread.currentThread().getName() + ".com");
                stmt.setInt(3, 16); // same target row ID

                System.out.println(Thread.currentThread().getName() + " executing update...");
                stmt.executeUpdate();

                // Hold the transaction open to simulate a lock
                Thread.sleep(3000);

                conn.commit();
                System.out.println(Thread.currentThread().getName() + " committed.");
            } catch (Exception e) {
                System.err.println(Thread.currentThread().getName() + " failed: " + e.getMessage());
            }
        };

        // Submit two concurrent update tasks
        executor.submit(updateTask);
        executor.submit(updateTask);

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
    }

    @Test
    void testDumpTable() throws DaoException {
        List<AirlineDto> airlineDtos = airlineDao.getAll();
        assertNotNull(airlineDtos, "The airline list should not be null");
        assertFalse(airlineDtos.isEmpty(), "Expected at least one airline record");

        System.out.println("Dumping airline records:");
        for (AirlineDto airlineDto : airlineDtos) {
            System.out.println("Returned Department(" + airlineDto.getAirlineId() + "):" + airlineDto.toJson());
        }
    }
}
