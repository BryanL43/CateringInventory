import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.fakeskymeal.dao.InventoryDao;
import org.fakeskymeal.dao.exception.DaoException;
import org.fakeskymeal.dao.impl.InventoryDaoImpl;
import org.fakeskymeal.dto.InventoryDto;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class InventoryTest extends BaseDaoTest<InventoryDao, InventoryDto> {
    private InventoryDao inventoryDao;

    @BeforeEach
    void initDao() {
        inventoryDao = new InventoryDaoImpl(pool);
    }

    @Override
    protected InventoryDao getDao() {
        return inventoryDao;
    }

    /**
     * createTestDto
     *
     * @return The instantiated dto for CRUD testing.
     */
    @Override
    protected InventoryDto createTestDto() {
        InventoryDto dto = new InventoryDto();
        dto.setFacilityId(1);
        dto.setName("Test Inv");
        dto.setUnit("lb");
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
    protected int getId(InventoryDto dto) {
        return dto.getInventoryId();
    }

    /**
     * getUpdatedParams
     *
     * @return The list of params to update the entry with
     */
    @Override
    protected String[] getUpdatedParams() {
        return new String[] {"1", "Test Updated Inventory", "kg"};
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
    protected void verifyUpdated(InventoryDto dto, String[] updatedParams) {
        assertNotNull(dto);
        assertEquals(Integer.parseInt(updatedParams[0]), dto.getFacilityId());
        assertEquals(updatedParams[1], dto.getName());
        assertEquals(updatedParams[2], dto.getUnit());
    }

    /**
     * getContentionUpdateSqlKey
     *
     * @return The key that holds the SQL UPDATE DML
     */
    @Override
    protected String getContentionUpdateSqlKey() {
        return "INVENTORY_UPDATE_ID";
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
        stmt.setInt(1, 1);
        stmt.setString(2, "Admins Test Inventory");
        stmt.setString(3, "kg");
        stmt.setInt(4, id);
    }

    @Test
    void testAcquireAllInventoriesFromFacilityByID() throws DaoException {
        int facilityId = 1;

        List<InventoryDto> inventoryDtos = inventoryDao.getAllByFacilityId(facilityId);

        assertNotNull(inventoryDtos, "The inventory list should not be null");
        assertFalse(inventoryDtos.isEmpty(), "There should be at least one inventory for facility id " + facilityId);

        for (InventoryDto inventoryDto : inventoryDtos) {
            System.out.println("Returned Inventory(" + inventoryDto.getInventoryId() + "):" + inventoryDto.toJson());
        }
    }
}
