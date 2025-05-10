package org.fakeskymeal.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.fakeskymeal.dto.BaseDto;
import org.fakeskymeal.dao.exception.DaoException;
import util.jdbc.ConnectionPool;
import util.jdbc.JdbcConnection;


/**
 * BaseDaoImpl
 *
 * Base class for Data Access Object implementations.
 *
 * Modifications:
 *
 * 		04/20/2024 - jhui - Created
 */
public abstract class BaseDaoImpl {
    private static final Logger LOGGER = Logger.getLogger(BaseDaoImpl.class.getName());
    protected final ConnectionPool pool;

    public BaseDaoImpl(ConnectionPool pool) {
        this.pool = pool;
    }

    abstract void convertRStoDto(ResultSet results, BaseDto dto) throws DaoException;
    abstract String getAllRowsQuery();
    abstract String getInsertQuery();
    abstract String getDeleteQuery();
    abstract String getUpdateQuery();
    abstract String getPrimaryKey();
    abstract BaseDto getDto();

    /**
     * get
     *
     * Given a primary key value, will return the corresponding row in DTO
     * format.
     *
     * @param Integer id - the primary key value
     * @return the DTO that corresponds to the row with the pKey of id
     */
    public BaseDto get(Integer id) throws DaoException {
        List<BaseDto> all = null;

        all = getMultipleRows(getPrimaryKey(), id);
        if (all == null || all.isEmpty()) {
            throw new DaoException("No entry found for id: " + id);
        }

        return (BaseDto) all.getFirst();
    }

    /**
     * getRow
     *
     * Given a field and value for a WHERE clause, this method will return
     * the first row that matches the condition.
     *
     * @param String field - database column name to filter on
     * @param Object value - value for the filter
     * @return first DTO that matches "field = value"
     */
    public BaseDto getRow(String field, Object value) throws DaoException {
        List<BaseDto> all = null;

        all = getMultipleRows(field, value);
        if (all == null || all.isEmpty()) {
            throw new DaoException("No entry found for field: " + field);
        }

        return (BaseDto) all.getFirst();
    }

    /**
     * getRows
     *
     * Given a field and value for a WHERE clause, this method will return
     * all the rows that matches the condition.
     *
     * @param String field - database column name to filter on
     * @param Object value - value for the filter
     * @return List of DTOs that match "field = value"
     */
    public List getRows(String field, Object value) throws DaoException {
        List all = null;

        all = getMultipleRows(field, value);
        if (all == null || all.isEmpty()) {
            throw new DaoException("No entry found for field: " + field);
        }

        return all;
    }

    /**
     * getAll
     *
     * Retrieve all the rows for this table and convert the rows into a List
     * of DTOs
     *
     * @return List of DTOs for all the rows in the table
     */
    public List getAll() throws DaoException {
        List all = null;

        all = getMultipleRows(null, null);
        if (all == null || all.isEmpty()) {
            throw new DaoException("No entry found");
        }

        return all;
    }

    /**
     * getMultipleRows
     *
     * General purpose method to retrieve rows from the database and convert them
     * into Data Transfer Objects (DTOs).
     *
     * @return List of the DTOs
     */
    List<BaseDto> getMultipleRows(String field, Object value) throws DaoException {
        List<BaseDto> all = new ArrayList<BaseDto>();;
        BaseDto dto = null;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet result = null;

        try {
            conn = pool.getConnection();
            String allRowsQuery = Objects.requireNonNull(getAllRowsQuery(), "Query not found for getAllRowsQuery() for class, " + this.getClass().getName());
            if (field != null) {
                allRowsQuery = allRowsQuery + " WHERE " + field + " = ?";
            }

            stmt = conn.prepareStatement(allRowsQuery);
            if (field != null) {
                stmt.setObject(1, value);
            }
            result = stmt.executeQuery();
            while (result.next()) {
                dto = getDto();
                all.add(dto);
                convertRStoDto(result, dto);
            }
        } catch (SQLException se) {
            throw new DaoException(se);
        } finally {
            if (result != null) {
                try {
                    result.close();
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

        return all;
    }
}
