package org.fakeskymeal.dao.impl;

import java.io.IOException;
import java.sql.*;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.fakeskymeal.dao.AirlineDao;
import org.fakeskymeal.dao.exception.DaoException;

import org.fakeskymeal.dto.AirlineDto;

import util.jdbc.ConnectionPool;

/**
 * AirlineDaoImpl
 *
 * Implementation for AirlineDao (Data Access Object).
 */
public class AirlineDaoImpl extends BaseDaoImpl<AirlineDto> implements AirlineDao {
    private static final Logger LOGGER = Logger.getLogger(AirlineDaoImpl.class.getName());

    String _tableName = "airline_companies";
    String _primaryKey = "id";
    Properties _queries;

    public AirlineDaoImpl(ConnectionPool pool) {
        super(pool, AirlineDto.class);

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
    public AirlineDto get(Integer id) throws DaoException {
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
    public AirlineDto getRow(String field, Object value) throws DaoException {
        return super.getRow(field, value);
    }

    /**
     * prepareInsert
     *
     * This method is called by the generic save() logic in BaseDaoImpl to bind
     * specific column values (e.g., name) into the SQL INSERT query.
     *
     * @param PreparedStatement stmt - The prepared statement to populate.
     * @param AirlineDto dto - The Data Transfer Object containing the values to insert.
     * @throws SQLException if a database access error occurs.
     */
    @Override
    protected void prepareInsert(PreparedStatement stmt, AirlineDto dto) throws SQLException {
        stmt.setString(1, dto.getAirlineName());
        stmt.setString(2, dto.getContactInfo());
    }

    /**
     * prepareUpdate
     *
     * This method binds the new field values from the {@code params} array and the primary key
     * from the {@code dto} to the prepared statement.
     *
     * @param PreparedStatement stmt - The prepared statement to populate.
     * @param AirlineDto dto - The Data Transfer Object containing the primary key (ID).
     * @param String[] params - An array of new values to apply (e.g., name).
     * @throws SQLException if a database access error occurs.
     */
    @Override
    protected void prepareUpdate(PreparedStatement stmt, AirlineDto dto, String[] params) throws SQLException {
        stmt.setString(1, params[0]); // new name
        stmt.setString(2, params[1]); // new contact_info
        stmt.setInt(3, dto.getAirlineId()); // WHERE id = ?
    }

    /**
     * applyParamsToDto
     *
     * Applies the given update parameters to the provided Data Transfer Object instance.
     * This method is called after a successful UPDATE operation to synchronize the
     * in-memory DTO with the new values that were written to the database.
     *
     * @param AirlineDto dto - The Data Transfer Object to update.
     * @param String[] params - An array of new values (e.g., name).
     */
    @Override
    protected void applyParamsToDto(AirlineDto dto, String[] params) {
        dto.setAirlineName(params[0]);
        dto.setContactInfo(params[1]);
    }

    /**
     * prepareDelete
     *
     * Populates the {@link PreparedStatement} with the primary key needed to delete
     * the specified Data Transfer Object from the database.
     *
     * @param PreparedStatement stmt - The prepared statement to populate
     * @param AirlineDto dto - The Data Transfer Object containing the ID to delete.
     * @throws SQLException if a database access error occurs.
     */
    @Override
    protected void prepareDelete(PreparedStatement stmt, AirlineDto dto) throws SQLException {
        stmt.setInt(1, dto.getAirlineId());
    }

    /**
     * setGeneratedId
     *
     * Extracts the generated primary key from the given {@link ResultSet}
     * and assigns it to the Data Transfer Object after a successful INSERT.
     *
     * @param ResultSet keys - The ResultSet containing generated keys.
     * @param AirlineDto dto - The Data Transfer Object to update with the generated ID.
     * @throws SQLException if a database access error occurs or no key is found.
     */
    @Override
    protected void setGeneratedId(ResultSet keys, AirlineDto dto) throws SQLException {
        dto.setAirlineId(keys.getInt(1));
    }

    /**
     * convertRStoDto
     *
     * Utility method that copies the values in the ResultSet into the DTO.
     * Needed specific implementation for the method getMultipleRows in the
     * BaseDaoImpl.
     *
     * @param ResultSet result - The source values from a query to the DB.
     * @param AirlineDto dto - The destination Data Transfer Object.
     * @throws DaoException Any errors that occur when converting ResultSet to an DTO instance.
     */
    @Override
    protected void convertRStoDto(ResultSet result, AirlineDto dto) throws DaoException {
        try {
            dto.setAirlineId(result.getInt(1));
            dto.setAirlineName(result.getString(2));
            dto.setContactInfo(result.getString(3));
        } catch (SQLException se) {
            throw new DaoException(se.getMessage());
        }
    }

    /**
     * getAllRowsQuery
     *
     * Returns the query for retrieving all rows for this table.
     *
     * @return String - equivalent to "select * from tableName"
     */
    @Override
    protected String getAllRowsQuery() {
        return _queries.getProperty("AIRLINE_GET_ALL");
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
        return _queries.getProperty("AIRLINE_INSERT");
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
        return _queries.getProperty("AIRLINE_DELETE_ID");
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
        return _queries.getProperty("AIRLINE_UPDATE_ID");
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
}
