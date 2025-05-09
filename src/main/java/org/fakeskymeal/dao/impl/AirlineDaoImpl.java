package org.fakeskymeal.dao.impl;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.fakeskymeal.dao.AirlineDao;
import org.fakeskymeal.dao.exception.DaoException;

import org.fakeskymeal.dto.BaseDto;
import org.fakeskymeal.dto.AirlineDto;

public class AirlineDaoImpl extends BaseDaoImpl implements AirlineDao {
    private static final Logger LOGGER = Logger.getLogger(AirlineDaoImpl.class.getName());

    String _tableName = "airline_companies";
    String _primaryKey = "id";
    Properties _airlineQueries = null;

    public AirlineDaoImpl() {
        super();

        _airlineQueries = new Properties();
        try {
            _airlineQueries.load( // Potential fail PATH UNDEFINED
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
        // TO DO: IMPLEMENT
        return;
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
        // TO DO: IMPLEMENT
        return;
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
        // TO DO: IMPLEMENT
        return;
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
