package org.fakeskymeal.dao.impl;

import java.io.IOException;
import java.sql.*;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.fakeskymeal.dao.MealDao;
import org.fakeskymeal.dao.exception.DaoException;

import org.fakeskymeal.dto.MealDto;

import util.jdbc.ConnectionPool;

/**
 * MealDaoImpl
 *
 * Implementation for MealDao (Data Access Object).
 */
public class MealDaoImpl extends BaseDaoImpl<MealDto> implements MealDao {
    private static final Logger LOGGER = Logger.getLogger(MealDaoImpl.class.getName());

    String _tableName = "meals";
    String _primaryKey = "id";
    Properties _queries;

    public MealDaoImpl(ConnectionPool pool) {
        super(pool, MealDto.class);

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
    public MealDto get(Integer id) throws DaoException {
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
    public MealDto getRow(String field, Object value) throws DaoException {
        return super.getRow(field, value);
    }

    /**
     * prepareInsert
     *
     * This method is called by the generic save() logic in BaseDaoImpl to bind
     * specific column values (e.g., name) into the SQL INSERT query.
     *
     * @param PreparedStatement stmt - The prepared statement to populate.
     * @param MealDto dto - The Data Transfer Object containing the values to insert.
     * @throws SQLException if a database access error occurs.
     */
    @Override
    protected void prepareInsert(PreparedStatement stmt, MealDto dto) throws SQLException {
        stmt.setInt(1, dto.getInventoryId());
        stmt.setString(2, dto.getName());
        stmt.setString(3, dto.getMealType());
        stmt.setBoolean(4, dto.isVegetarian());
        stmt.setInt(5, dto.getQuantity());
        stmt.setFloat(6, dto.getWeight());
        stmt.setDate(7, dto.getCreatedDate());
        stmt.setString(8, dto.getDescription());
    }

    /**
     * prepareUpdate
     *
     * This method binds the new field values from the {@code params} array and the primary key
     * from the {@code dto} to the prepared statement.
     *
     * @param PreparedStatement stmt - The prepared statement to populate.
     * @param MealDto dto - The Data Transfer Object containing the primary key (ID).
     * @param String[] params - An array of new values to apply (e.g., name).
     * @throws SQLException if a database access error occurs.
     */
    @Override
    protected void prepareUpdate(PreparedStatement stmt, MealDto dto, String[] params) throws SQLException {
        stmt.setInt(1, Integer.parseInt(params[0])); // new inventory_id
        stmt.setString(2, params[1]); // new name
        stmt.setString(3, params[2]); // new meal_type
        stmt.setBoolean(4, Boolean.parseBoolean(params[3]));
        stmt.setInt(5, Integer.parseInt(params[4])); // new quantity
        stmt.setFloat(6, Float.parseFloat(params[5])); // new weight
        stmt.setDate(7, Date.valueOf(params[6])); // new created_date
        stmt.setString(8, params[7]); // new description
        stmt.setInt(9, dto.getMealId()); // WHERE id = ?
    }

    /**
     * applyParamsToDto
     *
     * Applies the given update parameters to the provided Data Transfer Object instance.
     * This method is called after a successful UPDATE operation to synchronize the
     * in-memory DTO with the new values that were written to the database.
     *
     * @param MealDto dto - The Data Transfer Object to update.
     * @param String[] params - An array of new values (e.g., name).
     */
    @Override
    protected void applyParamsToDto(MealDto dto, String[] params) {
        dto.setInventoryId(Integer.parseInt(params[0]));
        dto.setName(params[1]);
        dto.setMealType(params[2]);
        dto.setVegetarian(Boolean.parseBoolean(params[3]));
        dto.setQuantity(Integer.parseInt(params[4]));
        dto.setWeight(Float.parseFloat(params[5]));
        dto.setCreatedDate(Date.valueOf(params[6]));
        dto.setDescription(params[7]);
    }

    /**
     * prepareDelete
     *
     * Populates the {@link PreparedStatement} with the primary key needed to delete
     * the specified Data Transfer Object from the database.
     *
     * @param PreparedStatement stmt - The prepared statement to populate
     * @param MealDto dto - The Data Transfer Object containing the ID to delete.
     * @throws SQLException if a database access error occurs.
     */
    @Override
    protected void prepareDelete(PreparedStatement stmt, MealDto dto) throws SQLException {
        stmt.setInt(1, dto.getMealId());
    }

    /**
     * setGeneratedId
     *
     * Extracts the generated primary key from the given {@link ResultSet}
     * and assigns it to the Data Transfer Object after a successful INSERT.
     *
     * @param ResultSet keys - The ResultSet containing generated keys.
     * @param MealDto dto - The Data Transfer Object to update with the generated ID.
     * @throws SQLException if a database access error occurs or no key is found.
     */
    @Override
    protected void setGeneratedId(ResultSet keys, MealDto dto) throws SQLException {
        dto.setMealId(keys.getInt(1));
    }

    /**
     * convertRStoDto
     *
     * Utility method that copies the values in the ResultSet into the DTO.
     * Needed specific implementation for the method getMultipleRows in the
     * BaseDaoImpl.
     *
     * @param ResultSet result - The source values from a query to the DB.
     * @param MealDto dto - The destination Data Transfer Object.
     * @throws DaoException Any errors that occur when converting ResultSet to an DTO instance.
     */
    @Override
    protected void convertRStoDto(ResultSet result, MealDto dto) throws DaoException {
        try {
            dto.setMealId(result.getInt(1));
            dto.setInventoryId(result.getInt(2));
            dto.setName(result.getString(3));
            dto.setMealType(result.getString(4));
            dto.setVegetarian(result.getBoolean(5));
            dto.setQuantity(result.getInt(6));
            dto.setWeight(result.getFloat(7));
            dto.setCreatedDate(result.getDate(8));
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
        return _queries.getProperty("MEAL_GET_ALL");
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
        return _queries.getProperty("MEAL_INSERT");
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
        return _queries.getProperty("MEAL_DELETE_ID");
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
        return _queries.getProperty("MEAL_UPDATE_ID");
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
