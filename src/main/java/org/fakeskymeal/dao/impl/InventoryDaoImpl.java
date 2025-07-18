package org.fakeskymeal.dao.impl;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.fakeskymeal.dao.InventoryDao;
import org.fakeskymeal.dao.exception.DaoException;

import org.fakeskymeal.dto.InventoryDto;

import util.jdbc.ConnectionPool;

/**
 * InventoryDaoImpl
 *
 * Implementation for InventoryDao (Data Access Object).
 */
public class InventoryDaoImpl extends BaseDaoImpl<InventoryDto> implements InventoryDao {
    private static final Logger LOGGER = Logger.getLogger(InventoryDaoImpl.class.getName());

    String _tableName = "inventory_stock";
    String _primaryKey = "id";
    Properties _queries;

    public InventoryDaoImpl(ConnectionPool pool) {
        super(pool, InventoryDto.class);

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
    public InventoryDto get(Integer id) throws DaoException {
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
    public InventoryDto getRow(String field, Object value) throws DaoException {
        return super.getRow(field, value);
    }

    /**
     * prepareInsert
     *
     * This method is called by the generic save() logic in BaseDaoImpl to bind
     * specific column values (e.g., name) into the SQL INSERT query.
     *
     * @param PreparedStatement stmt - The prepared statement to populate.
     * @param InventoryDto dto - The Data Transfer Object containing the values to insert.
     * @throws SQLException if a database access error occurs.
     */
    @Override
    protected void prepareInsert(PreparedStatement stmt, InventoryDto dto) throws SQLException {
        stmt.setInt(1, dto.getFacilityId());
        stmt.setString(2, dto.getName());
        stmt.setString(3, dto.getUnit());
    }

    /**
     * prepareUpdate
     *
     * This method binds the new field values from the {@code params} array and the primary key
     * from the {@code dto} to the prepared statement.
     *
     * @param PreparedStatement stmt - The prepared statement to populate.
     * @param InventoryDto dto - The Data Transfer Object containing the primary key (ID).
     * @param String[] params - An array of new values to apply (e.g., name).
     * @throws SQLException if a database access error occurs.
     */
    @Override
    protected void prepareUpdate(PreparedStatement stmt, InventoryDto dto, String[] params) throws SQLException {
        stmt.setInt(1, Integer.parseInt(params[0])); // new facility_id
        stmt.setString(2, params[1]); // new name
        stmt.setString(3, params[2]); // new unit
        stmt.setInt(4, dto.getInventoryId()); // WHERE id = ?
    }

    /**
     * applyParamsToDto
     *
     * Applies the given update parameters to the provided Data Transfer Object instance.
     * This method is called after a successful UPDATE operation to synchronize the
     * in-memory DTO with the new values that were written to the database.
     *
     * @param InventoryDto dto - The Data Transfer Object to update.
     * @param String[] params - An array of new values (e.g., name).
     */
    @Override
    protected void applyParamsToDto(InventoryDto dto, String[] params) {
        dto.setFacilityId(Integer.parseInt(params[0]));
        dto.setName(params[1]);
        dto.setUnit(params[2]);
    }

    /**
     * prepareDelete
     *
     * Populates the {@link PreparedStatement} with the primary key needed to delete
     * the specified Data Transfer Object from the database.
     *
     * @param PreparedStatement stmt - The prepared statement to populate
     * @param InventoryDto dto - The Data Transfer Object containing the ID to delete.
     * @throws SQLException if a database access error occurs.
     */
    @Override
    protected void prepareDelete(PreparedStatement stmt, InventoryDto dto) throws SQLException {
        stmt.setInt(1, dto.getInventoryId());
    }

    /**
     * setGeneratedId
     *
     * Extracts the generated primary key from the given {@link ResultSet}
     * and assigns it to the Data Transfer Object after a successful INSERT.
     *
     * @param ResultSet keys - The ResultSet containing generated keys.
     * @param InventoryDto dto - The Data Transfer Object to update with the generated ID.
     * @throws SQLException if a database access error occurs or no key is found.
     */
    @Override
    protected void setGeneratedId(ResultSet keys, InventoryDto dto) throws SQLException {
        dto.setInventoryId(keys.getInt(1));
    }

    /**
     * getAllByFacilityId
     *
     * Get all corresponding row in the database for the DTO with the filter
     * of facility id.
     *
     * @param String facilityId - The specified facility id.
     * @return The list of inventories that belongs to the specified facility.
     * @throws DaoException Any errors that occur during connection, statement, and resultset.
     */
    public List<InventoryDto> getAllByFacilityId(int facilityId) throws DaoException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        List<InventoryDto> inventories = new ArrayList<>();

        try {
            conn = pool.getConnection();
            stmt = conn.prepareStatement(getInventoriesByFacilityIDQuery());

            stmt.setInt(1, facilityId);

            rs = stmt.executeQuery();
            while (rs.next()) {
                InventoryDto inventory = new InventoryDto();
                convertRStoDto(rs, inventory);
                inventories.add(inventory);
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

        return inventories;
    }

    /**
     * convertRStoDto
     *
     * Utility method that copies the values in the ResultSet into the DTO.
     * Needed specific implementation for the method getMultipleRows in the
     * BaseDaoImpl.
     *
     * @param ResultSet result - The source values from a query to the DB.
     * @param InventoryDto dto - The destination Data Transfer Object.
     * @throws DaoException Any errors that occur when converting ResultSet to an DTO instance.
     */
    @Override
    protected void convertRStoDto(ResultSet result, InventoryDto dto) throws DaoException {
        try {
            dto.setInventoryId(result.getInt(1));
            dto.setFacilityId(result.getInt(2));
            dto.setName(result.getString(3));
            dto.setUnit(result.getString(4));
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
        return _queries.getProperty("INVENTORY_GET_ALL");
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
        return _queries.getProperty("INVENTORY_INSERT");
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
        return _queries.getProperty("INVENTORY_DELETE_ID");
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
        return _queries.getProperty("INVENTORY_UPDATE_ID");
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
     * getInventoriesByFacilityIDQuery
     *
     * Returns the Inventory selection by Facility ID Query
     *
     * @return String - SELECT query
     */
    String getInventoriesByFacilityIDQuery() {
        return _queries.getProperty("INVENTORY_GET_BY_FACILITY_ID");
    }
}
