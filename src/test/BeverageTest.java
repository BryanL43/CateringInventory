import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Date;
import java.util.Calendar;

import org.fakeskymeal.dao.BeverageDao;
import org.fakeskymeal.dao.impl.BeverageDaoImpl;
import org.fakeskymeal.dto.BeverageDto;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class BeverageTest extends BaseDaoTest<BeverageDao, BeverageDto> {
    private static BeverageDao beverageDao;

    @BeforeEach
    void initDao() {
        beverageDao = new BeverageDaoImpl(pool);
    }

    @Override
    protected BeverageDao getDao() {
        return beverageDao;
    }

    /**
     * createTestDto
     *
     * @return The instantiated dto for CRUD testing.
     */
    @Override
    protected BeverageDto createTestDto() {
        BeverageDto dto = new BeverageDto();
        dto.setInventoryId(1);
        dto.setName("Test Beverage");
        dto.setBrand("Test Brand");
        dto.setQuantity(5);
        dto.setWeight(3.27F);
        dto.setDeliveredDate(new Date(System.currentTimeMillis()));

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 2);
        Date futureDate = new Date(cal.getTimeInMillis());
        dto.setExpirationDate(futureDate);

        dto.setDescription("Test Description");
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
        return "brand";
    }

    /**
     * getValidValue
     *
     * @return A known test value in the database.
     */
    @Override
    protected Object getValidValue() {
        return "Bloxy";
    }

    /**
     * getId
     *
     * @param dto The Data Transfer Object
     * @return The object Dto's primary key id
     */
    @Override
    protected int getId(BeverageDto dto) {
        return dto.getBeverageId();
    }

    /**
     * getUpdatedParams
     *
     * @return The list of params to update the entry with
     */
    @Override
    protected String[] getUpdatedParams() {
        return new String[] {
            "1",
            "Test Updated Beverage",
            "Test Updated Brand",
            "5",
            "3.27",
            new Date(System.currentTimeMillis()).toString(),
            new Date(System.currentTimeMillis()).toString(),
            "Updated Description"
        };
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
    protected void verifyUpdated(BeverageDto dto, String[] updatedParams) {
        assertNotNull(dto);
        assertEquals(Integer.parseInt(updatedParams[0]), dto.getInventoryId());
        assertEquals(updatedParams[1], dto.getName());
        assertEquals(updatedParams[2], dto.getBrand());
        assertEquals(Integer.parseInt(updatedParams[3]), dto.getQuantity());
        assertEquals(Float.parseFloat(updatedParams[4]), dto.getWeight());
        assertEquals(updatedParams[5], dto.getDeliveredDate().toString());
        assertEquals(updatedParams[6], dto.getExpirationDate().toString());
        assertEquals(updatedParams[7], dto.getDescription());
    }

    /**
     * getContentionUpdateSqlKey
     *
     * @return The key that holds the SQL UPDATE DML
     */
    @Override
    protected String getContentionUpdateSqlKey() {
        return "BEVERAGE_UPDATE_NAME_ID";
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
        stmt.setString(1, "Cola Test");
        stmt.setInt(2, id);
    }
}
