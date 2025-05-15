package org.fakeskymeal.dao.impl;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.fakeskymeal.dao.CateringOrderDao;
import org.fakeskymeal.dao.exception.DaoException;

import org.fakeskymeal.dto.CateringOrderDto;

import util.jdbc.ConnectionPool;

public class CateringOrderDaoImpl extends BaseDaoImpl<CateringOrderDto> implements CateringOrderDao {
    private static final Logger LOGGER = Logger.getLogger(CateringOrderDaoImpl.class.getName());

    String _tableName = "catering_orders";
    String _primaryKey = "id";
    Properties _queries;

    public CateringOrderDaoImpl(ConnectionPool pool) {
        super(pool, CateringOrderDto.class);

        _queries = new Properties();
        try {
            _queries.load(
                    this.getClass().getClassLoader().getResourceAsStream("sql.properties")
            );
        } catch (IOException io) {
            LOGGER.log(Level.WARNING, "Exception during sql.properties load:", io);
        }
    }

    public CateringOrderDto get(Integer id) throws DaoException {
        return super.get(id);
    }

    public CateringOrderDto getRow(String field, Object value) throws DaoException {
        return super.getRow(field, value);
    }

    /**
     * prepareInsert
     *
     * This method is called by the generic save() logic in BaseDaoImpl to bind
     * specific column values (e.g., name) into the SQL INSERT query.
     *
     * @param stmt the prepared statement to populate
     * @param dto the Data Transfer Object containing the values to insert
     * @throws SQLException if a database access error occurs
     */
    @Override
    protected void prepareInsert(PreparedStatement stmt, CateringOrderDto dto) throws SQLException {
        stmt.setInt(1, dto.getFlightId());
        stmt.setInt(2, dto.getFacilityId());
        stmt.setObject(3, dto.getDeliveryTime());
    }

    /**
     * prepareUpdate
     *
     * This method binds the new field values from the {@code params} array and the primary key
     * from the {@code dto} to the prepared statement.
     *
     * @param stmt the prepared statement to populate
     * @param dto the Data Transfer Object containing the primary key (ID)
     * @param params an array of new values to apply (e.g., name)
     * @throws SQLException if a database access error occurs
     */
    @Override
    protected void prepareUpdate(PreparedStatement stmt, CateringOrderDto dto, String[] params) throws SQLException {
        stmt.setInt(1, Integer.parseInt(params[0])); // new flight_id
        stmt.setInt(2, Integer.parseInt(params[1])); // new facility_id
        stmt.setObject(3, LocalDateTime.parse(params[2])); // new delivery_time
        stmt.setInt(4, dto.getOrderId()); // WHERE id = ?
    }

    /**
     * applyParamsToDto
     *
     * Applies the given update parameters to the provided Data Transfer Object instance.
     * This method is called after a successful UPDATE operation to synchronize the
     * in-memory DTO with the new values that were written to the database.
     *
     * @param dto the Data Transfer Object to update
     * @param params the array of new values (e.g., name)
     */
    @Override
    protected void applyParamsToDto(CateringOrderDto dto, String[] params) {
        dto.setFlightId(Integer.parseInt(params[0]));
        dto.setFacilityId(Integer.parseInt(params[1]));
        dto.setDeliveryTime(LocalDateTime.parse(params[2]));
    }

    /**
     * prepareDelete
     *
     * Populates the {@link PreparedStatement} with the primary key needed to delete
     * the specified Data Transfer Object from the database.
     *
     * @param stmt the prepared statement to populate
     * @param dto the Data Transfer Object containing the ID to delete
     * @throws SQLException if a database access error occurs
     */
    @Override
    protected void prepareDelete(PreparedStatement stmt, CateringOrderDto dto) throws SQLException {
        stmt.setInt(1, dto.getOrderId());
    }

    /**
     * setGeneratedId
     *
     * Extracts the generated primary key from the given {@link ResultSet}
     * and assigns it to the Data Transfer Object after a successful INSERT.
     *
     * @param keys the ResultSet containing generated keys
     * @param dto the Data Transfer Object to update with the generated ID
     * @throws SQLException if a database access error occurs or no key is found
     */
    @Override
    protected void setGeneratedId(ResultSet keys, CateringOrderDto dto) throws SQLException {
        dto.setOrderId(keys.getInt(1));
    }

    /**
     * convertRStoDto
     *
     * Utility method that copies the values in the ResultSet into the DTO.
     * Needed specific implementation for the method getMultipleRows in the
     * BaseDaoImpl.
     *
     * @param ResultSet result - the source values from a query to the DB
     * @param CateringOrderDto dto - the destination Data Transfer Object
     */
    @Override
    protected void convertRStoDto(ResultSet result, CateringOrderDto dto) throws DaoException {
        try {
            dto.setOrderId(result.getInt(1));
            dto.setFlightId(result.getInt(2));
            dto.setFacilityId(result.getInt(3));
            dto.setDeliveryTime(result.getObject(4, LocalDateTime.class));
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
        return _queries.getProperty("CATERING_ORDER_GET_ALL");
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
        return _queries.getProperty("CATERING_ORDER_INSERT");
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
        return _queries.getProperty("CATERING_ORDER_DELETE_ID");
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
        return _queries.getProperty("CATERING_ORDER_UPDATE_ID");
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
