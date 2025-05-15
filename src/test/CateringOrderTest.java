import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;

import org.fakeskymeal.dao.CateringOrderDao;

import org.fakeskymeal.dao.impl.CateringOrderDaoImpl;
import org.fakeskymeal.dto.CateringOrderDto;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class CateringOrderTest extends BaseDaoTest<CateringOrderDao, CateringOrderDto> {
    private static CateringOrderDao cateringOrderDao;

    @BeforeEach
    void initDao() {
        cateringOrderDao = new CateringOrderDaoImpl(pool);
    }

    @Override
    protected CateringOrderDao getDao() {
        return cateringOrderDao;
    }

    /**
     * createTestDto
     *
     * @return The instantiated dto for CRUD testing.
     */
    @Override
    protected CateringOrderDto createTestDto() {
        CateringOrderDto dto = new CateringOrderDto();
        dto.setFlightId(2);
        dto.setFacilityId(1);
        dto.setDeliveryTime(LocalDateTime.now());
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
        return "facility_id";
    }

    /**
     * getValidValue
     *
     * @return A known test value in the database.
     */
    @Override
    protected Object getValidValue() {
        return 1;
    }

    /**
     * getId
     *
     * @param dto The Data Transfer Object
     * @return The object Dto's primary key id
     */
    @Override
    protected int getId(CateringOrderDto dto) {
        return dto.getOrderId();
    }

    /**
     * getUpdatedParams
     *
     * @return The list of params to update the entry with
     */
    @Override
    protected String[] getUpdatedParams() {
        return new String[] {"1", "1", LocalDateTime.now().toString()};
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
    protected void verifyUpdated(CateringOrderDto dto, String[] updatedParams) {
        assertNotNull(dto);
        assertEquals(Integer.parseInt(updatedParams[0]), dto.getFlightId());
        assertEquals(Integer.parseInt(updatedParams[1]), dto.getFacilityId());
        assertEquals(updatedParams[2], dto.getDeliveryTime().toString());
    }

    /**
     * getContentionUpdateSqlKey
     *
     * @return The key that holds the SQL UPDATE DML
     */
    @Override
    protected String getContentionUpdateSqlKey() {
        return "CATERING_ORDER_UPDATE_FLIGHT_ID";
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
        stmt.setInt(1, 2);
        stmt.setInt(2, id);
    }
}