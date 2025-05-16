import org.fakeskymeal.dao.impl.BaseDaoImpl;
import util.jdbc.ConnectionPool;
import util.jdbc.ConnectionPoolSingleton;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Properties;
import java.util.concurrent.*;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.io.IOException;
import java.sql.SQLException;

import org.fakeskymeal.dao.BaseDao;
import org.fakeskymeal.dao.exception.DaoException;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * BaseDaoTest
 *
 * The generic test object that will be inherited by selected entities to test.
 *
 * @param <D> - The Data Access Object (DAO).
 * @param <T> - The Data Transfer Object (DTO).
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BaseDaoTest<D extends BaseDao<T>, T> {
    private static final Logger LOGGER = Logger.getLogger(BaseDaoTest.class.getName());
    protected final ConnectionPool pool = ConnectionPoolSingleton.getInstance();

    protected abstract D getDao();
    protected abstract T createTestDto();
    protected abstract int getValidId();
    protected abstract String getValidField();
    protected abstract Object getValidValue();
    protected abstract int getId(T dto);
    protected abstract String[] getUpdatedParams();
    protected abstract void verifyUpdated(T dto, String[] updatedParams);
    protected abstract String getContentionUpdateSqlKey();
    protected abstract void prepareContentionUpdate(PreparedStatement stmt, int id) throws SQLException;

    @Test
    void testGetById() throws DaoException {
        T dto = getDao().get(getValidId());
        assertNotNull(dto);
    }

    @Test
    void testInvalidGetById() {
        assertThrows(DaoException.class, () -> getDao().get(-999));
    }

    @Test
    void testGetOneByParam() throws DaoException {
        T dto = getDao().getRow(getValidField(), getValidValue());
        assertNotNull(dto);
    }

    @Test
    void testInvalidGetOneByParam() {
        assertThrows(DaoException.class, () -> getDao().getRow("invalid_field", "invalid_value"));
    }

    @Test
    void testCRUD() {
        T dto = createTestDto();
        boolean inserted = false, deleted = false;
        int originalAutoIncrement = -1;

        try {
            // Create
            getDao().save(dto);
            inserted = true;
            assertTrue(getId(dto) > 0, "ID should be set after insert");
            originalAutoIncrement = getId(dto);
            System.out.println("Created " + dto.getClass().getSimpleName() + " with ID: " + getId(dto));

            // Retrieve
            T reloaded = getDao().get(getId(dto));
            System.out.println("\nBefore update:\n" + proxyToJson(reloaded));

            // Update
            String[] params = getUpdatedParams();
            getDao().update(reloaded, params);
            verifyUpdated(reloaded, params);

            System.out.println("\nAfter update:\n" + proxyToJson(reloaded) + "\n");

            // Delete
            getDao().delete(reloaded);
            deleted = true;
            assertThrows(DaoException.class, () -> getDao().get(getId(reloaded)));
            System.out.println("Deleted " + reloaded.getClass().getSimpleName() + " with ID: " + getId(reloaded));
        } catch (Exception e) {
            if (inserted && !deleted) {
                try {
                    getDao().delete(dto);
                } catch (Exception ignored) {
                    // Object may have already been deleted or not exist [ignored]
                }
            }
            throw new AssertionError("Test failed during CRUD operation.", e);
        } finally {
            // Workaround to auto incrementing on CRUD testing
            if (inserted && originalAutoIncrement > 0) {
                String sql = "ALTER TABLE " + ((BaseDaoImpl<?>) getDao()).getTableName() + " AUTO_INCREMENT = ?";

                try (Connection conn = pool.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {

                    pstmt.setInt(1, originalAutoIncrement);
                    pstmt.executeUpdate();

                } catch (SQLException se) {
                    System.err.println("Failed to reset AUTO_INCREMENT: " + se.getMessage());
                }
            }

        }
    }

    @Test
    void testConcurrentUpdateContention() {
        // Load SQL Resources
        String updateQueryKey = getContentionUpdateSqlKey();

        Properties sqlQueries = new Properties();
        try {
            sqlQueries.load(this.getClass().getClassLoader().getResourceAsStream("sql.properties"));
        } catch (IOException io) {
            LOGGER.log(Level.WARNING, "Exception during sql.properties load:", io);
            fail("Failed to load SQL properties.");
        }

        // Instantiate multi-thread contention update
        ExecutorService executor = Executors.newFixedThreadPool(2);
        try (executor) {
            Runnable updateTask = () -> {
                Connection conn = null;
                PreparedStatement stmt = null;
                try {
                    // Acquire connection & stop individual transaction
                    conn = pool.getConnection();
                    conn.setAutoCommit(false);

                    // Create the preparedStatement
                    stmt = conn.prepareStatement(sqlQueries.getProperty(updateQueryKey));
                    prepareContentionUpdate(stmt, getValidId());

                    System.out.println(Thread.currentThread().getName() + " executing update...");
                    int rowsAffected = stmt.executeUpdate();
                    System.out.println(Thread.currentThread().getName() + " updated rows: " + rowsAffected);

                    // Update contention simulation
                    Thread.sleep(300); // simulate lock duration
                    conn.commit();
                    System.out.println(Thread.currentThread().getName() + " committed.");

                    conn.setAutoCommit(true); // reset to prevent deadlocks
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, Thread.currentThread().getName() + " failed: ", e);
                } finally {
                    if (stmt != null) {
                        try {
                            stmt.close();
                        } catch (SQLException se) {
                            LOGGER.log(Level.WARNING, "Error closing Statement: ", se.getMessage());
                        }
                    }

                    if (conn != null) {
                        pool.releaseConnection(conn);
                    }
                }
            };

            // Submit the two updates simultaneously
            Future<?> f1 = executor.submit(updateTask);
            Future<?> f2 = executor.submit(updateTask);
            f1.get();
            f2.get();
        } catch (Exception e) {
            fail("Concurrency test failed: " + e.getMessage());
        }

        assertTrue(executor.isShutdown() || executor.isTerminated(), "Executor should be shut down.");
    }

    /**
     * proxyToJson
     *
     * Utility method to convert the DTO content into a JSON string.
     *
     * @param T dto - The Data Transfer Object.
     * @return The converted DTO object as JSON.
     */
    protected String proxyToJson(T dto) {
        try {
            return (String) dto.getClass().getMethod("toJson").invoke(dto);
        } catch (Exception e) {
            return dto.toString();
        }
    }
}
