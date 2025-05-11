import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.fakeskymeal.dao.AirlineDao;
import org.fakeskymeal.dao.impl.AirlineDaoImpl;
import org.fakeskymeal.dto.AirlineDto;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class AirlineTest extends BaseDaoTest<AirlineDao, AirlineDto> {
    private AirlineDao airlineDao;

    @BeforeEach
    void initDao() {
        airlineDao = new AirlineDaoImpl(pool);
    }

    @Override
    protected AirlineDao getDao() {
        return airlineDao;
    }

    /**
     * createTestDto
     *
     * @return The instantiated dto for CRUD testing.
     */
    @Override
    protected AirlineDto createTestDto() {
        AirlineDto dto = new AirlineDto();
        dto.setAirlineName("Test Airline");
        dto.setContactInfo("Test@example.com");
        return dto;
    }

    /**
     * getValidId
     *
     * @return The known test ID in the database.
     */
    @Override
    protected int getValidId() {
        return 1;
    }

    /**
     * getValidField
     *
     * @return A known field in the database that will be tested.
     */
    @Override
    protected String getValidField() {
        return "contact_info";
    }

    /**
     * getValidValue
     *
     * @return A known test value in the database.
     */
    @Override
    protected Object getValidValue() {
        return "Admin@example.com";
    }

    /**
     * getId
     *
     * @param dto The Data Transfer Object
     * @return The object Dto's primary key id
     */
    @Override
    protected int getId(AirlineDto dto) {
        return dto.getAirlineId();
    }

    /**
     * getUpdatedParams
     *
     * @return The list of params to update the entry with
     */
    @Override
    protected String[] getUpdatedParams() {
        return new String[] {"Test Updated Airline", "Updated@example.com"};
    }

    /**
     * verifyUpdated
     *
     * Validate that the dto content is updated with the new values.
     *
     * @param dto The Data transfer object
     * @param updatedParams The list of the expected updated values.
     */
    @Override
    protected void verifyUpdated(AirlineDto dto, String[] updatedParams) {
        assertNotNull(dto);
        assertEquals(updatedParams[0], dto.getAirlineName());
        assertEquals(updatedParams[1], dto.getContactInfo());
    }

    /**
     * getContentionUpdateSqlKey
     *
     * @return The key that holds the SQL UPDATE DML
     */
    @Override
    protected String getContentionUpdateSqlKey() {
        return "AIRLINE_UPDATE_ID";
    }

    /**
     * prepareContentionUpdate
     *
     * Loads the specific column and param statement for contention update test.
     *
     * @param stmt The prepared statement object.
     * @param id The p-key id of the test entry.
     * @throws SQLException Any setString exceptions.
     */
    @Override
    protected void prepareContentionUpdate(PreparedStatement stmt, int id) throws SQLException {
        stmt.setString(1, "Admins Test Airline");
        stmt.setString(2, "Admin@example.com");
        stmt.setInt(3, id);
    }
}
