import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import org.fakeskymeal.dao.FlightDao;
import org.fakeskymeal.dao.exception.DaoException;
import org.fakeskymeal.dao.impl.FlightDaoImpl;
import org.fakeskymeal.dto.FlightDto;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class FlightTest extends BaseDaoTest<FlightDao, FlightDto> {
    private static FlightDao flightDao;

    @BeforeEach
    void initDao() {
        flightDao = new FlightDaoImpl(pool);
    }

    @Override
    protected FlightDao getDao() {
        return flightDao;
    }

    /**
     * createTestDto
     *
     * @return The instantiated dto for CRUD testing.
     */
    @Override
    protected FlightDto createTestDto() {
        FlightDto dto = new FlightDto();
        dto.setAirlineCompanyId(2);
        dto.setFlightNumber("DL5678");
        dto.setDepartureTime(LocalDateTime.now());
        dto.setArrivalTime(LocalDateTime.now().plusHours(2));
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
        return "airline_company_id";
    }

    /**
     * getValidValue
     *
     * @return A known test value in the database.
     */
    @Override
    protected Object getValidValue() {
        return 2;
    }

    /**
     * getId
     *
     * @param dto The Data Transfer Object
     * @return The object Dto's primary key id
     */
    @Override
    protected int getId(FlightDto dto) {
        return dto.getFlightId();
    }

    /**
     * getUpdatedParams
     *
     * @return The list of params to update the entry with
     */
    @Override
    protected String[] getUpdatedParams() {
        return new String[] {"2", "DL9101", LocalDateTime.now().toString(), LocalDateTime.now().plusHours(2).toString()};
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
    protected void verifyUpdated(FlightDto dto, String[] updatedParams) {
        assertNotNull(dto);
        assertEquals(Integer.parseInt(updatedParams[0]), dto.getAirlineCompanyId());
        assertEquals(updatedParams[1], dto.getFlightNumber());
        assertEquals(updatedParams[2], dto.getDepartureTime().toString());
        assertEquals(updatedParams[3], dto.getArrivalTime().toString());
    }

    /**
     * getContentionUpdateSqlKey
     *
     * @return The key that holds the SQL UPDATE DML
     */
    @Override
    protected String getContentionUpdateSqlKey() {
        return "FLIGHT_UPDATE_NUMBER";
    }

    /**
     * prepareContentionUpdate
     *
     * Loads the specific column and param statement for contention update test.
     *
     * @param stmt The prepared statement object.
     * @param id The p-key id of the test entry.
     * @throws SQLException Any statement set exceptions.
     */
    @Override
    protected void prepareContentionUpdate(PreparedStatement stmt, int id) throws SQLException {
        stmt.setString(1, "DL6969");
        stmt.setInt(2, id);
    }

    @Test
    void testAcquireAllFlightsFromAirlineByName() throws DaoException {
        String companyName = "Delta";

        List<FlightDto> flightDtos = flightDao.getFlightsByAirlineName(companyName);

        assertNotNull(flightDtos, "The flight list should not be null");
        assertFalse(flightDtos.isEmpty(), "There should be at least one flight for airline " + companyName);

        for (FlightDto flightDto : flightDtos) {
            System.out.println("Returned Flight(" + flightDto.getFlightId() + "):" + flightDto.toJson());
        }
    }
}
