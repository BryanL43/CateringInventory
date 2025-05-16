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

import org.fakeskymeal.dto.BeverageDto;
import org.fakeskymeal.dto.CateringOrderDto;
import org.fakeskymeal.dto.MealDto;

import util.jdbc.ConnectionPool;

/**
 * CateringOrderDaoImpl
 *
 * Implementation for CateringOrderDao (Data Access Object).
 */
public class CateringOrderDaoImpl extends BaseDaoImpl<CateringOrderDto> implements CateringOrderDao {
    private static final Logger LOGGER = Logger.getLogger(CateringOrderDaoImpl.class.getName());

    String _tableName = "catering_orders";
    String _primaryKey = "id";
    Properties _queries;

    public CateringOrderDaoImpl(ConnectionPool pool) {
        super(pool, CateringOrderDto.class);

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
    public CateringOrderDto get(Integer id) throws DaoException {
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
    public CateringOrderDto getRow(String field, Object value) throws DaoException {
        return super.getRow(field, value);
    }

    /**
     * prepareInsert
     *
     * This method is called by the generic save() logic in BaseDaoImpl to bind
     * specific column values (e.g., name) into the SQL INSERT query.
     *
     * @param PreparedStatement stmt - The prepared statement to populate.
     * @param CateringOrderDto dto - The Data Transfer Object containing the values to insert.
     * @throws SQLException if a database access error occurs.
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
     * @param PreparedStatement stmt - The prepared statement to populate.
     * @param CateringOrderDto dto - The Data Transfer Object containing the primary key (ID).
     * @param String[] params - An array of new values to apply (e.g., name).
     * @throws SQLException if a database access error occurs.
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
     * @param CateringOrderDto dto - The Data Transfer Object to update.
     * @param String[] params - An array of new values (e.g., name).
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
     * @param PreparedStatement stmt - The prepared statement to populate
     * @param CateringOrderDto dto - The Data Transfer Object containing the ID to delete.
     * @throws SQLException if a database access error occurs.
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
     * @param ResultSet keys - The ResultSet containing generated keys.
     * @param CateringOrderDto dto - The Data Transfer Object to update with the generated ID.
     * @throws SQLException if a database access error occurs or no key is found.
     */
    @Override
    protected void setGeneratedId(ResultSet keys, CateringOrderDto dto) throws SQLException {
        dto.setOrderId(keys.getInt(1));
    }


    /**
     * getBeveragesForOrder
     *
     * Acquire all beverages associated with a Catering Order id.
     *
     * @param int orderId - The catering order id.
     * @return A list of BeverageDto.
     * @throws DaoException Any errors that occur during connection, statement, and resultset.
     */
    public List<BeverageDto> getBeveragesForOrder(int orderId) throws DaoException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        List<BeverageDto> beverages = new ArrayList<>();

        try {
            conn = pool.getConnection();
            stmt = conn.prepareStatement(getBeveragesByOrderIdQuery());

            stmt.setInt(1, orderId);

            rs = stmt.executeQuery();
            while (rs.next()) {
                BeverageDto beverage = new BeverageDto();
                beverage.setBeverageId(rs.getInt(1));
                beverage.setInventoryId(rs.getInt(2));
                beverage.setName(rs.getString(3));
                beverage.setBrand(rs.getString(4));
                beverage.setQuantity(rs.getInt(5));
                beverage.setWeight(rs.getFloat(6));
                beverage.setDeliveredDate(rs.getDate(7));
                beverage.setExpirationDate(rs.getDate(8));
                beverage.setDescription(rs.getString(9));
                beverages.add(beverage);
            }
        } catch (SQLException se) {
            throw new DaoException("Failed to get beverages for order ID " + orderId, se);
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

        return beverages;
    }

    /**
     * addBeverageToOrder
     *
     * Associate a beverage to a catering order via a many-to-many (join table) relationship.
     *
     * @param int orderId - The catering order id.
     * @param int beverageId - The beverage id.
     * @param int quantity - The number of said beverages to add to the catering order.
     * @throws DaoException Any errors that occur during connection, statement, and resultset.
     */
    public void addBeverageToOrder(int orderId, int beverageId, int quantity) throws DaoException {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = pool.getConnection();
            stmt = conn.prepareStatement(getInsertBeverageToOrderQuery());

            stmt.setInt(1, orderId);
            stmt.setInt(2, beverageId);
            stmt.setInt(3, quantity);

            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new DaoException("Failed to insert beverage id " + beverageId + " to catering order id " + orderId);
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
     * removeBeverageFromOrder
     *
     * Remove a beverage from a catering order via a many-to-many (join table) relationship.
     *
     * @param int orderId - The catering order id.
     * @param int beverageId - The beverage id.
     * @throws DaoException Any errors that occur during connection, statement, and resultset.
     */
    public void removeBeverageFromOrder(int orderId, int beverageId) throws DaoException {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = pool.getConnection();
            stmt = conn.prepareStatement(getDeleteBeverageFromOrderQuery());

            stmt.setInt(1, orderId);
            stmt.setInt(2, beverageId);

            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted == 0) {
                throw new DaoException("Failed to delete beverage id " + beverageId + " from catering order id " + orderId);
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
     * getMealsForOrder
     *
     * Acquire all meals associated with a Catering Order id.
     *
     * @param int orderId - The catering order id.
     * @return A list of MealDto.
     * @throws DaoException Any errors that occur during connection, statement, and resultset.
     */
    public List<MealDto> getMealsForOrder(int orderId) throws DaoException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        List<MealDto> meals = new ArrayList<>();

        try {
            conn = pool.getConnection();
            stmt = conn.prepareStatement(getMealsByOrderIdQuery());

            stmt.setInt(1, orderId);

            rs = stmt.executeQuery();
            while (rs.next()) {
                MealDto meal = new MealDto();
                meal.setMealId(rs.getInt(1));
                meal.setInventoryId(rs.getInt(2));
                meal.setName(rs.getString(3));
                meal.setMealType(rs.getString(4));
                meal.setVegetarian(rs.getBoolean(5));
                meal.setQuantity(rs.getInt(6));
                meal.setWeight(rs.getFloat(7));
                meal.setCreatedDate(rs.getDate(8));
                meal.setDescription(rs.getString(9));
                meals.add(meal);
            }
        } catch (SQLException se) {
            throw new DaoException("Failed to get beverages for order ID " + orderId, se);
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

        return meals;
    }

    /**
     * addMealToOrder
     *
     * Associate a meal for a catering order via a many-to-many (join table) relationship.
     *
     * @param int orderId - The catering order id.
     * @param int mealId - The beverage id.
     * @param int quantity - The number of said beverages to add to the catering order.
     * @throws DaoException Any errors that occur during connection, statement, and resultset.
     */
    public void addMealToOrder(int orderId, int mealId, int quantity) throws DaoException {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = pool.getConnection();
            stmt = conn.prepareStatement(getInsertMealToOrderQuery());

            stmt.setInt(1, orderId);
            stmt.setInt(2, mealId);
            stmt.setInt(3, quantity);

            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new DaoException("Failed to insert meal id " + mealId + " to catering order id " + orderId);
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
     * removeMealFromOrder
     *
     * Remove a meal from a catering order via a many-to-many (join table) relationship.
     *
     * @param int orderId - The catering order id.
     * @param int mealId - The meal id.
     * @throws DaoException Any errors that occur during connection, statement, and resultset.
     */
    public void removeMealFromOrder(int orderId, int mealId) throws DaoException {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = pool.getConnection();
            stmt = conn.prepareStatement(getDeleteMealFromOrderQuery());

            stmt.setInt(1, orderId);
            stmt.setInt(2, mealId);

            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted == 0) {
                throw new DaoException("Failed to delete meal id " + mealId + " from catering order id " + orderId);
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
     * convertRStoDto
     *
     * Utility method that copies the values in the ResultSet into the DTO.
     * Needed specific implementation for the method getMultipleRows in the
     * BaseDaoImpl.
     *
     * @param ResultSet result - The source values from a query to the DB.
     * @param CateringOrderDto dto - The destination Data Transfer Object.
     * @throws DaoException Any errors that occur when converting ResultSet to an DTO instance.
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
     * Returns the query for retrieving all rows for this table.
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

    /**
     * getBeveragesByOrderIdQuery
     *
     * Returns the Beverages associated to the Catering Order
     *
     * @return String - SELECT query
     */
    private String getBeveragesByOrderIdQuery() {
        return _queries.getProperty("GET_BEVERAGES_FOR_ORDER");
    }

    /**
     * getInsertBeverageToOrderQuery
     *
     * Returns the INSERT query for the catering_order_beverages join table.
     *
     * @return String - INSERT query
     */
    private String getInsertBeverageToOrderQuery() {
        return _queries.getProperty("INSERT_BEVERAGE_TO_ORDER");
    }

    /**
     * getDeleteBeverageFromOrderQuery
     *
     * Returns the DELETE query for the catering_order_beverages join table.
     *
     * @return String - DELETE query
     */
    private String getDeleteBeverageFromOrderQuery() {
        return _queries.getProperty("DELETE_BEVERAGE_FROM_ORDER");
    }

    /**
     * getMealsByOrderIdQuery
     *
     * Returns the Meals associated to the Catering Order
     *
     * @return String - SELECT query
     */
    private String getMealsByOrderIdQuery() {
        return _queries.getProperty("GET_MEALS_FOR_ORDER");
    }

    /**
     * getInsertMealToOrderQuery
     *
     * Returns the INSERT query for the catering_order_meals join table.
     *
     * @return String - INSERT query
     */
    private String getInsertMealToOrderQuery() {
        return _queries.getProperty("INSERT_MEAL_TO_ORDER");
    }

    /**
     * getDeleteMealFromOrderQuery
     *
     * Returns the DELETE query for the catering_order_meals join table.
     *
     * @return String - DELETE query
     */
    private String getDeleteMealFromOrderQuery() {
        return _queries.getProperty("DELETE_MEAL_FROM_ORDER");
    }
}
