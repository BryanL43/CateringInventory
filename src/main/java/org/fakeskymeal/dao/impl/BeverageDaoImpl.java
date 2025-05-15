package org.fakeskymeal.dao.impl;

import java.io.IOException;
import java.sql.*;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.fakeskymeal.dao.BeverageDao;
import org.fakeskymeal.dao.exception.DaoException;

import org.fakeskymeal.dto.BeverageDto;

import util.jdbc.ConnectionPool;

public class BeverageDaoImpl extends BaseDaoImpl<BeverageDto> implements BeverageDao {
    private static final Logger LOGGER = Logger.getLogger(BeverageDaoImpl.class.getName());

    String _tableName = "beverages";
    String _primaryKey = "id";
    Properties _queries;

    public BeverageDaoImpl(ConnectionPool pool) {
        super(pool, BeverageDto.class);

        _queries = new Properties();
        try {
            _queries.load(
                    this.getClass().getClassLoader().getResourceAsStream("sql.properties")
            );
        } catch (IOException io) {
            LOGGER.log(Level.WARNING, "Exception during sql.properties load:", io);
        }
    }

    public BeverageDto get(Integer id) throws DaoException {
        return super.get(id);
    }

    public BeverageDto getRow(String field, Object value) throws DaoException {
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
    protected void prepareInsert(PreparedStatement stmt, BeverageDto dto) throws SQLException {
        stmt.setInt(1, dto.getInventoryId());
        stmt.setString(2, dto.getName());
        stmt.setString(3, dto.getBrand());
        stmt.setInt(4, dto.getQuantity());
        stmt.setFloat(5, dto.getWeight());
        stmt.setDate(6, dto.getDeliveredDate());
        stmt.setDate(7, dto.getExpirationDate());
        stmt.setString(8, dto.getDescription());
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
    protected void prepareUpdate(PreparedStatement stmt, BeverageDto dto, String[] params) throws SQLException {
        stmt.setInt(1, Integer.parseInt(params[0])); // new inventory_id
        stmt.setString(2, params[1]); // new name
        stmt.setString(3, params[2]); // new brand
        stmt.setInt(4, Integer.parseInt(params[3])); // new quantity
        stmt.setFloat(5, Float.parseFloat(params[4])); // new weight
        stmt.setDate(6, Date.valueOf(params[5])); // new delivery_date
        stmt.setDate(7, Date.valueOf(params[6])); // new expiration_date
        stmt.setString(8, params[7]); // new description
        stmt.setInt(9, dto.getBeverageId()); // WHERE id = ?
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
    protected void applyParamsToDto(BeverageDto dto, String[] params) {
        dto.setInventoryId(Integer.parseInt(params[0]));
        dto.setName(params[1]);
        dto.setBrand(params[2]);
        dto.setQuantity(Integer.parseInt(params[3]));
        dto.setWeight(Float.parseFloat(params[4]));
        dto.setDeliveredDate(Date.valueOf(params[5]));
        dto.setExpirationDate(Date.valueOf(params[6]));
        dto.setDescription(params[7]);
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
    protected void prepareDelete(PreparedStatement stmt, BeverageDto dto) throws SQLException {
        stmt.setInt(1, dto.getBeverageId());
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
    protected void setGeneratedId(ResultSet keys, BeverageDto dto) throws SQLException {
        dto.setBeverageId(keys.getInt(1));
    }

    /**
     * convertRStoDto
     *
     * Utility method that copies the values in the ResultSet into the DTO.
     * Needed specific implementation for the method getMultipleRows in the
     * BaseDaoImpl.
     *
     * @param ResultSet result - the source values from a query to the DB
     * @param BeverageDto dto - the destination Data Transfer Object
     */
    @Override
    protected void convertRStoDto(ResultSet result, BeverageDto dto) throws DaoException {
        try {
            dto.setBeverageId(result.getInt(1));
            dto.setInventoryId(result.getInt(2));
            dto.setName(result.getString(3));
            dto.setBrand(result.getString(4));
            dto.setQuantity(result.getInt(5));
            dto.setWeight(result.getFloat(6));
            dto.setDeliveredDate(result.getDate(7));
            dto.setExpirationDate(result.getDate(8));
            dto.setDescription(result.getString(9));
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
        return _queries.getProperty("BEVERAGE_GET_ALL");
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
        return _queries.getProperty("BEVERAGE_INSERT");
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
        return _queries.getProperty("BEVERAGE_DELETE_ID");
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
        return _queries.getProperty("BEVERAGE_UPDATE_ID");
    }

    /**
     * getTableName
     *
     * Return the Table Name
     *
     * @return String - Table Name
     */
    @Override
    protected String getTableName() {
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
