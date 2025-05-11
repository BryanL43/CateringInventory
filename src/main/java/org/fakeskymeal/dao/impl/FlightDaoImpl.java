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

import org.fakeskymeal.dto.BaseDto;
import org.fakeskymeal.dto.FlightDto;

import util.jdbc.ConnectionPool;

public class FlightDaoImpl extends BaseDaoImpl implements FlightDao {
    private static final Logger LOGGER = Logger.getLogger(FlightDaoImpl.class.getName());

    String _tableName = "flights";
    String _primaryKey = "id";
    Properties _flightQueries = null;

    public FlightDaoImpl(ConnectionPool pool) {
        super(pool);

        _flightQueries = new Properties();
        try {
            _flightQueries.load(
                    this.getClass().getClassLoader().getResourceAsStream("sql.properties")
            );
        } catch (IOException io) {
            LOGGER.log(Level.WARNING, "Exception during sql.properties load:", io);
        }
    }

    public FlightDto get(Integer id) throws DaoException {
        return (FlightDto) super.get(id);
    }

    public FlightDto getRow(String field, Object value) throws DaoException {
        return (FlightDto) super.getRow(field, value);
    }

    /**
     * save
     *
     * Convert the DTO into a SQL row and INSERT into the table
     *
     * @param FlightDto t - DTO that contains the values for the new row
     */
    public void save(FlightDto t) throws DaoException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet generatedKeys = null;

        try {
            conn = pool.getConnection();
            stmt = conn.prepareStatement(getInsertQuery(), Statement.RETURN_GENERATED_KEYS);

            stmt.setInt(1, t.getAirlineCompanyId());
            stmt.setString(2, t.getFlightNumber());
            stmt.setObject(3, t.getDepartureTime());
            stmt.setObject(4, t.getArrivalTime());

            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new DaoException("Insert failed, no rows affected.");
            }

            // Acquire the generated id for the newly inserted item
            generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int generatedId = generatedKeys.getInt(1);
                t.setFlightId(generatedId);
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
     * @param FlightDto t - pull the primary key out of t
     * @param String[] params - values to update the row
     *
     */
    public void update(FlightDto t, String[] params) throws DaoException {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = pool.getConnection();
            stmt = conn.prepareStatement(getUpdateQuery());

            stmt.setInt(1, Integer.parseInt(params[0])); // new airline_company_id
            stmt.setString(2, params[1]); // new flight_number
            stmt.setObject(3, LocalDateTime.parse(params[2])); // new departure_time
            stmt.setObject(4, LocalDateTime.parse(params[3])); // new arrival_time
            stmt.setInt(5, t.getFlightId()); // WHERE id = ?

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated == 0) {
                throw new DaoException("Update failed: No record found with ID = " + t.getFlightId());
            }

            // Update DTO with new values
            t.setAirlineCompanyId(Integer.parseInt(params[0]));
            t.setFlightNumber(params[1]);
            t.setDepartureTime(LocalDateTime.parse(params[2]));
            t.setArrivalTime(LocalDateTime.parse(params[3]));
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
     * @param FlightDto t - pull the primary key out of t
     *
     */
    public void delete(FlightDto t) throws DaoException {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = pool.getConnection();
            stmt = conn.prepareStatement(getDeleteQuery());

            stmt.setInt(1, t.getFlightId());

            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted == 0) {
                throw new DaoException("Delete failed: no record found with ID = " + t.getFlightId());
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

    /**
     * getFlightsByCompanyName
     *
     * Get all corresponding row in the database for the DTO with the filter
     * of airline company name
     *
     * @param String companyName - The flights associated to said company name.
     *
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
                flight.setFlightId(rs.getInt("id"));
                flight.setAirlineCompanyId(rs.getInt("airline_company_id"));
                flight.setFlightNumber(rs.getString("flight_number"));
                flight.setDepartureTime(rs.getObject("departure_time", LocalDateTime.class));
                flight.setArrivalTime(rs.getObject("arrival_time", LocalDateTime.class));

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
     * @param ResultSet result - the source values from a query to the DB
     * @param BaseDto dto - the destination Data Transfer Object
     */
    void convertRStoDto(ResultSet result, BaseDto dto) throws DaoException {
        FlightDto flight = (FlightDto) dto;
        try {
            flight.setFlightId(result.getInt(1));
            flight.setAirlineCompanyId(result.getInt(2));
            flight.setFlightNumber(result.getString(3));
            flight.setDepartureTime(result.getObject(4, LocalDateTime.class));
            flight.setArrivalTime(result.getObject(5, LocalDateTime.class));
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
        return _flightQueries.getProperty("FLIGHT_GET_ALL");
    }

    /**
     * getInsertQuery
     *
     * Returns the INSERT query for this table
     *
     * @return String - INSERT query
     */
    String getInsertQuery() {
        return _flightQueries.getProperty("FLIGHT_INSERT");
    }

    /**
     * getDeleteQuery
     *
     * Returns the DELETE query for this table
     *
     * @return String - DELETE query
     */
    String getDeleteQuery() {
        return _flightQueries.getProperty("FLIGHT_DELETE_ID");
    }

    /**
     * getUpdateQuery
     *
     * Returns the UPDATE query for this table
     *
     * @return String - UPDATE query
     */
    String getUpdateQuery() {
        return _flightQueries.getProperty("FLIGHT_UPDATE_ID");
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
        return new FlightDto();
    }

    /**
     * getFlightsByCompanyNameQuery
     *
     * Returns the Flight selection by Airline Name Query
     *
     * @return String - SELECT query
     */
    String getFlightsByAirlineNameQuery() {
        return _flightQueries.getProperty("FLIGHT_GET_BY_AIRLINE_NAME");
    }
}
