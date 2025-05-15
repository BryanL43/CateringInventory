package org.fakeskymeal.dao.impl;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.fakeskymeal.dao.BaseDao;
import org.fakeskymeal.dto.BaseDto;
import org.fakeskymeal.dao.exception.DaoException;
import util.jdbc.ConnectionPool;

/**
 * BaseDaoImpl
 *
 * Base class for Data Access Object implementations.
 *
 * Modifications:
 *
 * 		04/20/2024 - jhui - Created
 */
public abstract class BaseDaoImpl<T extends BaseDto> implements BaseDao<T> {
    private static final Logger LOGGER = Logger.getLogger(BaseDaoImpl.class.getName());
    protected final ConnectionPool pool;
    private final Class<T> dtoClass;

    public BaseDaoImpl(ConnectionPool pool, Class<T> dtoClass) {
        this.pool = pool;
        this.dtoClass = dtoClass;
    }

    // Abstract hooks for subclass-specific logic
    protected abstract void prepareInsert(PreparedStatement stmt, T dto) throws SQLException;
    protected abstract void prepareUpdate(PreparedStatement stmt, T dto, String[] params) throws SQLException;
    protected abstract void applyParamsToDto(T dto, String[] params);
    protected abstract void prepareDelete(PreparedStatement stmt, T dto) throws SQLException;
    protected abstract void setGeneratedId(ResultSet keys, T dto) throws SQLException;

    // Default required implementation
    protected abstract void convertRStoDto(ResultSet results, T dto) throws DaoException;
    protected abstract String getAllRowsQuery();
    protected abstract String getInsertQuery();
    protected abstract String getDeleteQuery();
    protected abstract String getUpdateQuery();
    public abstract String getTableName();
    protected abstract String getPrimaryKey();

    /**
     * createDtoInstance
     *
     * Internal factory method for creating Dto instances of Type T
     *
     * @return the newly instantiated Dto instance
     */
    protected T createDtoInstance() throws DaoException {
        try {
            return dtoClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new DaoException("Unable to create DTO instance: " + e.getMessage(), e);
        }
    }

    /**
     * get
     *
     * Given a primary key value, will return the corresponding row in DTO
     * format.
     *
     * @param Integer id - the primary key value
     * @return the DTO that corresponds to the row with the pKey of id
     */
    public T get(Integer id) throws DaoException {
        List<T> all = getMultipleRows(getPrimaryKey(), id);
        if (all == null || all.isEmpty()) {
            throw new DaoException("No entry found for id: " + id);
        }
        return all.getFirst();
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
    public T getRow(String field, Object value) throws DaoException {
        List<T> all = getMultipleRows(field, value);
        if (all == null || all.isEmpty()) {
            throw new DaoException("No entry found for field: " + field);
        }
        return all.getFirst();
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
    public List<T> getRows(String field, Object value) throws DaoException {
        List<T> all = getMultipleRows(field, value);
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
    public List<T> getAll() throws DaoException {
        List<T> all = getMultipleRows(null, null);
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
    protected List<T> getMultipleRows(String field, Object value) throws DaoException {
        List<T> all = new ArrayList<>();
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
                T dto = createDtoInstance();
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

    /**
     * save
     *
     * Convert the DTO into a SQL row and INSERT into the table
     *
     * @param T dto - DTO that contains the values for the new row
     */
    @Override
    public void save(T dto) throws DaoException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet generatedKeys = null;

        try {
            conn = pool.getConnection();
            stmt = conn.prepareStatement(getInsertQuery(), Statement.RETURN_GENERATED_KEYS);

            prepareInsert(stmt, dto);

            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new DaoException("Insert failed, no rows affected.");
            }

            // Acquire the generated id for the newly inserted item
            generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                setGeneratedId(generatedKeys, dto);
            } else {
                throw new DaoException("Insert succeeded, but no ID returned.");
            }
        } catch (SQLException se) {
            throw new DaoException(se.getMessage());
        } finally {
            if (generatedKeys != null) {
                try {
                    generatedKeys.close();
                } catch (SQLException se) {
                    LOGGER.log(Level.WARNING, "Error closing generated key: ", se.getMessage());
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
    }

    /**
     * update
     *
     * Update the corresponding row in the database for the DTO with the
     * values in params
     *
     * @param T dto - pull the primary key out of t
     * @param String[] params - values to update the row
     *
     */
    @Override
    public void update(T dto, String[] params) throws DaoException {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = pool.getConnection();
            stmt = conn.prepareStatement(getUpdateQuery());

            prepareUpdate(stmt, dto, params);

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated == 0) {
                throw new DaoException("Update failed: No record found with ID = " + dto.getId());
            }

            applyParamsToDto(dto, params);
        } catch (SQLException se) {
            throw new DaoException(se.getMessage());
        } finally {
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
    }

    /**
     * delete
     *
     * Delete the corresponding row in the database for the DTO
     *
     * @param T dto - pull the primary key out of t
     *
     */
    @Override
    public void delete(T dto) throws DaoException {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = pool.getConnection();
            stmt = conn.prepareStatement(getDeleteQuery());

            prepareDelete(stmt, dto);

            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted == 0) {
                throw new DaoException("Delete failed: no record found with ID = " + dto.getId());
            }
        } catch (SQLException se) {
            throw new DaoException(se.getMessage());
        } finally {
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
    }
}
