package org.fakeskymeal.dao.impl;

import java.io.IOException;
import java.sql.*;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.fakeskymeal.dao.AirlineDao;
import org.fakeskymeal.dao.exception.DaoException;

import org.fakeskymeal.dto.BaseDto;
import org.fakeskymeal.dto.AirlineDto;
import util.jdbc.JdbcConnection;

public class AirlineDaoImpl extends BaseDaoImpl implements AirlineDao {
    private static final Logger LOGGER = Logger.getLogger(AirlineDaoImpl.class.getName());

    String _tableName = "airline_companies";
    String _primaryKey = "id";
    Properties _airlineQueries = null;

    public AirlineDaoImpl() {
        super();

        _airlineQueries = new Properties();
        try {
            _airlineQueries.load(
                this.getClass().getClassLoader().getResourceAsStream("main/resources/sql.properties")
            );
        } catch (IOException io) {
            LOGGER.log(Level.WARNING, "Exception during sql.properties load:", io);
        }
    }

    public AirlineDto get(Integer id) throws DaoException {
        return (AirlineDto) super.get(id);
    }

    public AirlineDto getRow(String field, Object value) throws DaoException {
        return (AirlineDto) super.getRow(field, value);
    }

    /**
     * save
     *
     * Convert the DTO into a SQL row and INSERT into the table
     *
     * @param AirlineDto t - DTO that contains the values for the new row
     */
    public void save(AirlineDto t) throws DaoException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet generatedKeys = null;

        try {
            conn = JdbcConnection.getConnection();
            stmt = conn.prepareStatement(getInsertQuery(), Statement.RETURN_GENERATED_KEYS);

            stmt.setString(1, t.getAirlineName());
            stmt.setString(2, t.getContactInfo());

            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new DaoException("Insert failed, no rows affected.");
            }

            // Acquire the generated id for the newly inserted item
            generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int generatedId = generatedKeys.getInt(1);
                t.setAirlineId(generatedId);
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
                JdbcConnection.resetConnection();
            }

            System.out.print("Confirm connection close. Expect: null; got: ");
            JdbcConnection.checkStatus();
        }
    }

    /**
     * update
     *
     * Update the corresponding row in the database for the DTO with the
     * values in params
     *
     * @param AirlineDto t - pull the primary key out of t
     * @param String[] params - values to update the row
     *
     */
    public void update(AirlineDto t, String[] params) throws DaoException {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = JdbcConnection.getConnection();
            stmt = conn.prepareStatement(getUpdateQuery());

            stmt.setString(1, params[0]); // new name
            stmt.setString(2, params[1]); // new contact_info
            stmt.setInt(3, t.getAirlineId()); // WHERE airline_id = ?

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated == 0) {
                throw new DaoException("Update failed: No record found with ID = " + t.getAirlineId());
            }

            // Update DTO with new values
            t.setAirlineName(params[0]);
            t.setContactInfo(params[1]);
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
                JdbcConnection.resetConnection();
            }

            System.out.print("Confirm connection close. Expect: null; got: ");
            JdbcConnection.checkStatus();
        }
    }

    /**
     * delete
     *
     * Delete the corresponding row in the database for the DTO
     *
     * @param AirlineDto t - pull the primary key out of t
     *
     */
    public void delete(AirlineDto t) throws DaoException {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = JdbcConnection.getConnection();
            stmt = conn.prepareStatement(getDeleteQuery());

            stmt.setInt(1, t.getAirlineId());

            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted == 0) {
                throw new DaoException("Delete failed: no record found with ID = " + t.getAirlineId());
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
                JdbcConnection.resetConnection();
            }

            System.out.print("Confirm connection close. Expect: null; got: ");
            JdbcConnection.checkStatus();
        }
    }

    /**
     * convertRStoDto
     *
     * Utility method that copies the values in the ResultSet into the DTO.
     * Needed specific implementation for the method getMultipleRows in the
     * BaseDaoImpl.
     *
     * @param ResultSet result - the source values from a query to the DB
     * @param BaseDto dto - the destination Data Transfer Object
     */
    void convertRStoDto(ResultSet result, BaseDto dto) throws DaoException {
        AirlineDto airline = (AirlineDto) dto;
        try {
            airline.setAirlineId(result.getInt(1));
            airline.setAirlineName(result.getString(2));
            airline.setContactInfo(result.getString(3));
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
    String getAllRowsQuery() {
        return _airlineQueries.getProperty("AIRLINE_GET_ALL");
    }

    /**
     * getInsertQuery
     *
     * Returns the INSERT query for this table
     *
     * @return String - INSERT query
     */
    String getInsertQuery() {
        return _airlineQueries.getProperty("AIRLINE_INSERT");
    }

    /**
     * getDeleteQuery
     *
     * Returns the DELETE query for this table
     *
     * @return String - DELETE query
     */
    String getDeleteQuery() {
        return _airlineQueries.getProperty("AIRLINE_DELETE_ID");
    }

    /**
     * getUpdateQuery
     *
     * Returns the UPDATE query for this table
     *
     * @return String - UPDATE query
     */
    String getUpdateQuery() {
        return _airlineQueries.getProperty("AIRLINE_UPDATE_ID");
    }

    /**
     * getTableName
     *
     * Return the Table Name
     *
     * @return String - Table Name
     */
    String getTableName() {
        return _tableName;
    }

    /**
     * getPrimaryKey
     *
     * Returns the Primary Key for this table
     *
     * @return String - Primary Key
     */
    String getPrimaryKey() {
        return _primaryKey;
    }

    /**
     * getDto
     *
     * Returns the appropriate Data Transfer Object for this Data Access Object.
     *
     * @return appropriate DTO
     */
    BaseDto getDto() {
        return new AirlineDto();
    }
}
