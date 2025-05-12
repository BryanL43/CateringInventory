import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.fakeskymeal.dao.FacilityDao;
import org.fakeskymeal.dao.impl.FacilityDaoImpl;
import org.fakeskymeal.dto.FacilityDto;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class FacilityTest extends BaseDaoTest<FacilityDao, FacilityDto> {
    private FacilityDao facilityDao;

    @BeforeEach
    void initDao() {
        facilityDao = new FacilityDaoImpl(pool);
    }

    @Override
    protected FacilityDao getDao() {
        return facilityDao;
    }

    /**
     * createTestDto
     *
     * @return The instantiated dto for CRUD testing.
     */
    @Override
    protected FacilityDto createTestDto() {
        FacilityDto dto = new FacilityDto();
        dto.setFacilityName("F5678");
        dto.setFacilityLocation("123 Main St");
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
        return "name";
    }

    /**
     * getValidValue
     *
     * @return A known test value in the database.
     */
    @Override
    protected Object getValidValue() {
        return "Test Facility";
    }

    /**
     * getId
     *
     * @param dto The Data Transfer Object
     * @return The object Dto's primary key id
     */
    @Override
    protected int getId(FacilityDto dto) {
        return dto.getFacilityId();
    }

    /**
     * getUpdatedParams
     *
     * @return The list of params to update the entry with
     */
    @Override
    protected String[] getUpdatedParams() {
        return new String[] {"F9101", "Test Updated Street"};
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
    protected void verifyUpdated(FacilityDto dto, String[] updatedParams) {
        assertNotNull(dto);
        assertEquals(updatedParams[0], dto.getFacilityName());
        assertEquals(updatedParams[1], dto.getFacilityLocation());
    }

    /**
     * getContentionUpdateSqlKey
     *
     * @return The key that holds the SQL UPDATE DML
     */
    @Override
    protected String getContentionUpdateSqlKey() {
        return "FACILITY_UPDATE_ID";
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
        stmt.setString(1, "Test Facility");
        stmt.setString(2, "Test Street");
        stmt.setInt(3, id);
    }
}