package org.fakeskymeal.dao.impl;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.fakeskymeal.dao.FlightDao;
import org.fakeskymeal.dao.exception.DaoException;

import org.fakeskymeal.dto.FlightDto;

import util.jdbc.ConnectionPool;

/**
 * FlightDaoImpl
 *
 * Implementation for FlightDao (Data Access Object).
 */
public class FlightDaoImpl extends BaseDaoImpl<FlightDto> implements FlightDao {
    private static final Logger LOGGER = Logger.getLogger(FlightDaoImpl.class.getName());

    String _tableName = "flights";
    String _primaryKey = "id";
    Properties _queries;

    public FlightDaoImpl(ConnectionPool pool) {
        super(pool, FlightDto.class);

        // Load the SQL queries
        _queries = new Properties();
        try {
            _queries.load(
                    this.getClass().getClassLoader().getResourceAsStream("sql.properties")
            );
        } catch (IOException io) {
            LOGGER.log(Level.WARNING, "Exception during sql.properties load:", io);
        }
    }

    /**
     * get
     *
     * Method redirect, given a primary key value, will return the corresponding row in DTO
     * format.
     *
     * @param Integer id - The primary key value.
     * @return the DTO that corresponds to the row with the pKey of id.
     * @throws DaoException Any errors that occur when retrieving the DTO instance.
     */
    public FlightDto get(Integer id) throws DaoException {
        return super.get(id);
    }

    /**
     * getRow
     *
     * Method redirect, given a field and value for a WHERE clause, this method will return
     * the first row that matches the condition.
     *
     * @param String field - Database column name to filter on.
     * @param Object value - Value for the filter.
     * @return first DTO that matches "field = value"
     * @throws DaoException Any errors that occur when retrieving the DTO instance.
     */
    public FlightDto getRow(String field, Object value) throws DaoException {
        return super.getRow(field, value);
    }

    /**
     * prepareInsert
     *
     * This method is called by the generic save() logic in BaseDaoImpl to bind
     * specific column values (e.g., name) into the SQL INSERT query.
     *
     * @param PreparedStatement stmt - The prepared statement to populate.
     * @param FlightDto dto - The Data Transfer Object containing the values to insert.
     * @throws SQLException if a database access error occurs.
     */
    @Override
    protected void prepareInsert(PreparedStatement stmt, FlightDto dto) throws SQLException {
        stmt.setInt(1, dto.getAirlineCompanyId());
        stmt.setString(2, dto.getFlightNumber());
        stmt.setObject(3, dto.getDepartureTime());
        stmt.setObject(4, dto.getArrivalTime());
    }

    /**
     * prepareUpdate
     *
     * This method binds the new field values from the {@code params} array and the primary key
     * from the {@code dto} to the prepared statement.
     *
     * @param PreparedStatement stmt - The prepared statement to populate.
     * @param FlightDto dto - The Data Transfer Object containing the primary key (ID).
     * @param String[] params - An array of new values to apply (e.g., name).
     * @throws SQLException if a database access error occurs.
     */
    @Override
    protected void prepareUpdate(PreparedStatement stmt, FlightDto dto, String[] params) throws SQLException {
        stmt.setInt(1, Integer.parseInt(params[0])); // new airline_company_id
        stmt.setString(2, params[1]); // new flight_number
        stmt.setObject(3, LocalDateTime.parse(params[2])); // new departure_time
        stmt.setObject(4, LocalDateTime.parse(params[3])); // new arrival_time
        stmt.setInt(5, dto.getFlightId()); // WHERE id = ?
    }

    /**
     * applyParamsToDto
     *
     * Applies the given update parameters to the provided Data Transfer Object instance.
     * This method is called after a successful UPDATE operation to synchronize the
     * in-memory DTO with the new values that were written to the database.
     *
     * @param FlightDto dto - The Data Transfer Object to update.
     * @param String[] params - An array of new values (e.g., name).
     */
    @Override
    protected void applyParamsToDto(FlightDto dto, String[] params) {
        dto.setAirlineCompanyId(Integer.parseInt(params[0]));
        dto.setFlightNumber(params[1]);
        dto.setDepartureTime(LocalDateTime.parse(params[2]));
        dto.setArrivalTime(LocalDateTime.parse(params[3]));
    }

    /**
     * prepareDelete
     *
     * Populates the {@link PreparedStatement} with the primary key needed to delete
     * the specified Data Transfer Object from the database.
     *
     * @param PreparedStatement stmt - The prepared statement to populate
     * @param FlightDto dto - The Data Transfer Object containing the ID to delete.
     * @throws SQLException if a database access error occurs.
     */
    @Override
    protected void prepareDelete(PreparedStatement stmt, FlightDto dto) throws SQLException {
        stmt.setInt(1, dto.getFlightId());
    }

    /**
     * setGeneratedId
     *
     * Extracts the generated primary key from the given {@link ResultSet}
     * and assigns it to the Data Transfer Object after a successful INSERT.
     *
     * @param ResultSet keys - The ResultSet containing generated keys.
     * @param FlightDto dto - The Data Transfer Object to update with the generated ID.
     * @throws SQLException if a database access error occurs or no key is found.
     */
    @Override
    protected void setGeneratedId(ResultSet keys, FlightDto dto) throws SQLException {
        dto.setFlightId(keys.getInt(1));
    }

    /**
     * getFlightsByCompanyName
     *
     * Get all corresponding row in the database for the DTO with the filter
     * of airline company name.
     *
     * @param String companyName - The airline company's name.
     * @return The list of flights associated to the company name.
     * @throws DaoException Any errors that occur during connection, statement, and resultset.
     */
    public List<FlightDto> getFlightsByAirlineName(String companyName) throws DaoException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        List<FlightDto> flights = new ArrayList<>();

        try {
            conn = pool.getConnection();
            stmt = conn.prepareStatement(getFlightsByAirlineNameQuery());

            stmt.setString(1, companyName);

            rs = stmt.executeQuery();
            while (rs.next()) {
                FlightDto flight = new FlightDto();
                convertRStoDto(rs, flight);
                flights.add(flight);
            }
        } catch (SQLException se) {
            throw new DaoException(se.getMessage());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException se) {
                    LOGGER.log(Level.WARNING, "Error closing ResultSet: ", se.getMessage());
                }
            }

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

        return flights;
    }

    /**
     * convertRStoDto
     *
     * Utility method that copies the values in the ResultSet into the DTO.
     * Needed specific implementation for the method getMultipleRows in the
     * BaseDaoImpl.
     *
     * @param ResultSet result - The source values from a query to the DB.
     * @param FlightDto dto - The destination Data Transfer Object.
     * @throws DaoException Any errors that occur when converting ResultSet to an DTO instance.
     */
    @Override
    protected void convertRStoDto(ResultSet result, FlightDto dto) throws DaoException {
        try {
            dto.setFlightId(result.getInt(1));
            dto.setAirlineCompanyId(result.getInt(2));
            dto.setFlightNumber(result.getString(3));
            dto.setDepartureTime(result.getObject(4, LocalDateTime.class));
            dto.setArrivalTime(result.getObject(5, LocalDateTime.class));
        } catch (SQLException se) {
            throw new DaoException(se.getMessage());
        }
    }

    /**
     * getAllRowsQuery
     *
     * Returns the query for retrieving all rows for this table
     *
     * @return String - equivalent to "select * from tableName"
     */
    @Override
    protected String getAllRowsQuery() {
        return _queries.getProperty("FLIGHT_GET_ALL");
    }

    /**
     * getInsertQuery
     *
     * Returns the INSERT query for this table
     *
     * @return String - INSERT query
     */
    @Override
    protected String getInsertQuery() {
        return _queries.getProperty("FLIGHT_INSERT");
    }

    /**
     * getDeleteQuery
     *
     * Returns the DELETE query for this table
     *
     * @return String - DELETE query
     */
    @Override
    protected String getDeleteQuery() {
        return _queries.getProperty("FLIGHT_DELETE_ID");
    }

    /**
     * getUpdateQuery
     *
     * Returns the UPDATE query for this table
     *
     * @return String - UPDATE query
     */
    @Override
    protected String getUpdateQuery() {
        return _queries.getProperty("FLIGHT_UPDATE_ID");
    }

    /**
     * getTableName
     *
     * Return the Table Name
     *
     * @return String - Table Name
     */
    @Override
    public String getTableName() {
        return _tableName;
    }

    /**
     * getPrimaryKey
     *
     * Returns the Primary Key for this table
     *
     * @return String - Primary Key
     */
    @Override
    protected String getPrimaryKey() {
        return _primaryKey;
    }

    /**
     * getFlightsByCompanyNameQuery
     *
     * Returns the Flight selection by Airline Name Query
     *
     * @return String - SELECT query
     */
    private String getFlightsByAirlineNameQuery() {
        return _queries.getProperty("FLIGHT_GET_BY_AIRLINE_NAME");
    }
}
