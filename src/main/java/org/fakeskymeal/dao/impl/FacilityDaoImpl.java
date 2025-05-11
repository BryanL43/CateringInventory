//package org.fakeskymeal.dao.impl;
//
//import java.io.IOException;
//import java.sql.*;
//import java.util.Properties;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//
//import org.fakeskymeal.dao.FacilityDao;
//import org.fakeskymeal.dao.exception.DaoException;
//
//import org.fakeskymeal.dto.AirlineDto;
//import org.fakeskymeal.dto.BaseDto;
//import org.fakeskymeal.dto.FacilityDto;
//
//import util.jdbc.ConnectionPool;
//
//public class FacilityDaoImpl extends BaseDaoImpl implements FacilityDao {
//    private static final Logger LOGGER = Logger.getLogger(FacilityDaoImpl.class.getName());
//
//    String _tableName = "catering_facilities";
//    String _primaryKey = "id";
//    Properties _facilityQueries = null;
//
//    public FacilityDaoImpl(ConnectionPool pool) {
//        super(pool);
//
//        _facilityQueries = new Properties();
//        try {
//            _facilityQueries.load(
//                    this.getClass().getClassLoader().getResourceAsStream("sql.properties")
//            );
//        } catch (IOException io) {
//            LOGGER.log(Level.WARNING, "Exception during sql.properties load:", io);
//        }
//    }
//
//    public FacilityDto get(Integer id) throws DaoException {
//        return (FacilityDto) super.get(id);
//    }
//
//    public FacilityDto getRow(String field, Object value) throws DaoException {
//        return (FacilityDto) super.getRow(field, value);
//    }
//
//    /**
//     * save
//     *
//     * Convert the DTO into a SQL row and INSERT into the table
//     *
//     * @param FacilityDto t - DTO that contains the values for the new row
//     */
//    public void save(FacilityDto t) throws DaoException {
//        Connection conn = null;
//        PreparedStatement stmt = null;
//        ResultSet generatedKeys = null;
//
//        try {
//            conn = pool.getConnection();
//            stmt = conn.prepareStatement(getInsertQuery(), Statement.RETURN_GENERATED_KEYS);
//
//            stmt.setString(1, t.getFacilityName());
//            stmt.setString(2, t.getFacilityLocation());
//
//            int rows = stmt.executeUpdate();
//            if (rows == 0) {
//                throw new DaoException("Insert failed, no rows affected.");
//            }
//
//            // Acquire the generated id for the newly inserted item
//            generatedKeys = stmt.getGeneratedKeys();
//            if (generatedKeys.next()) {
//                int generatedId = generatedKeys.getInt(1);
//                t.setFacilityId(generatedId);
//            } else {
//                throw new DaoException("Insert succeeded, but no ID returned.");
//            }
//        } catch (SQLException se) {
//            throw new DaoException(se.getMessage());
//        } finally {
//            if (generatedKeys != null) {
//                try {
//                    generatedKeys.close();
//                } catch (SQLException se) {
//                    LOGGER.log(Level.WARNING, "Error closing generated key: ", se.getMessage());
//                }
//            }
//
//            if (stmt != null) {
//                try {
//                    stmt.close();
//                } catch (SQLException se) {
//                    LOGGER.log(Level.WARNING, "Error closing Statement: ", se.getMessage());
//                }
//            }
//
//            if (conn != null) {
//                pool.releaseConnection(conn);
//            }
//        }
//    }
//
//    /**
//     * update
//     *
//     * Update the corresponding row in the database for the DTO with the
//     * values in params
//     *
//     * @param FacilityDto t - pull the primary key out of t
//     * @param String[] params - values to update the row
//     *
//     */
//    public void update(FacilityDto t, String[] params) throws DaoException {
//        Connection conn = null;
//        PreparedStatement stmt = null;
//
//        try {
//            conn = pool.getConnection();
//            stmt = conn.prepareStatement(getUpdateQuery());
//
//            stmt.setString(1, params[0]); // new name
//            stmt.setString(2, params[1]); // new location
//            stmt.setInt(3, t.getFacilityId()); // WHERE id = ?
//
//            int rowsUpdated = stmt.executeUpdate();
//            if (rowsUpdated == 0) {
//                throw new DaoException("Update failed: No record found with ID = " + t.getFacilityId());
//            }
//
//            // Update DTO with new values
//            t.setFacilityName(params[0]);
//            t.setFacilityLocation(params[1]);
//        } catch (SQLException se) {
//            throw new DaoException(se.getMessage());
//        } finally {
//            if (stmt != null) {
//                try {
//                    stmt.close();
//                } catch (SQLException se) {
//                    LOGGER.log(Level.WARNING, "Error closing Statement: ", se.getMessage());
//                }
//            }
//
//            if (conn != null) {
//                pool.releaseConnection(conn);
//            }
//        }
//    }
//
//    /**
//     * delete
//     *
//     * Delete the corresponding row in the database for the DTO
//     *
//     * @param FacilityDto t - pull the primary key out of t
//     *
//     */
//    public void delete(FacilityDto t) throws DaoException {
//        Connection conn = null;
//        PreparedStatement stmt = null;
//
//        try {
//            conn = pool.getConnection();
//            stmt = conn.prepareStatement(getDeleteQuery());
//
//            stmt.setInt(1, t.getFacilityId());
//
//            int rowsDeleted = stmt.executeUpdate();
//            if (rowsDeleted == 0) {
//                throw new DaoException("Delete failed: no record found with ID = " + t.getFacilityId());
//            }
//        } catch (SQLException se) {
//            throw new DaoException(se.getMessage());
//        } finally {
//            if (stmt != null) {
//                try {
//                    stmt.close();
//                } catch (SQLException se) {
//                    LOGGER.log(Level.WARNING, "Error closing Statement: ", se.getMessage());
//                }
//            }
//
//            if (conn != null) {
//                pool.releaseConnection(conn);
//            }
//        }
//    }
//
//    /**
//     * convertRStoDto
//     *
//     * Utility method that copies the values in the ResultSet into the DTO.
//     * Needed specific implementation for the method getMultipleRows in the
//     * BaseDaoImpl.
//     *
//     * @param ResultSet result - the source values from a query to the DB
//     * @param BaseDto dto - the destination Data Transfer Object
//     */
//    void convertRStoDto(ResultSet result, BaseDto dto) throws DaoException {
//        FacilityDto facility = (FacilityDto) dto;
//        try {
//            facility.setFacilityId(result.getInt(1));
//            facility.setFacilityName(result.getString(2));
//            facility.setFacilityLocation(result.getString(3));
//        } catch (SQLException se) {
//            throw new DaoException(se.getMessage());
//        }
//    }
//
//    /**
//     * getAllRowsQuery
//     *
//     * Returns the query for retrieving all rows for this table
//     *
//     * @return String - equivalent to "select * from tableName"
//     */
//    String getAllRowsQuery() {
//        return _facilityQueries.getProperty("FACILITY_GET_ALL");
//    }
//
//    /**
//     * getInsertQuery
//     *
//     * Returns the INSERT query for this table
//     *
//     * @return String - INSERT query
//     */
//    String getInsertQuery() {
//        return _facilityQueries.getProperty("FACILITY_INSERT");
//    }
//
//    /**
//     * getDeleteQuery
//     *
//     * Returns the DELETE query for this table
//     *
//     * @return String - DELETE query
//     */
//    String getDeleteQuery() {
//        return _facilityQueries.getProperty("FACILITY_UPDATE_ID");
//    }
//
//    /**
//     * getUpdateQuery
//     *
//     * Returns the UPDATE query for this table
//     *
//     * @return String - UPDATE query
//     */
//    String getUpdateQuery() {
//        return _facilityQueries.getProperty("FACILITY_DELETE_ID");
//    }
//
//    /**
//     * getTableName
//     *
//     * Return the Table Name
//     *
//     * @return String - Table Name
//     */
//    String getTableName() {
//        return _tableName;
//    }
//
//    /**
//     * getPrimaryKey
//     *
//     * Returns the Primary Key for this table
//     *
//     * @return String - Primary Key
//     */
//    String getPrimaryKey() {
//        return _primaryKey;
//    }
//
//    /**
//     * getDto
//     *
//     * Returns the appropriate Data Transfer Object for this Data Access Object.
//     *
//     * @return appropriate DTO
//     */
//    BaseDto getDto() {
//        return new FacilityDto();
//    }
//}
