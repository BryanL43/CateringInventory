package org.fakeskymeal;

import org.fakeskymeal.dao.BeverageDao;
import org.fakeskymeal.dao.CateringOrderDao;
import org.fakeskymeal.dao.FlightDao;
import org.fakeskymeal.dao.MealDao;
import org.fakeskymeal.dao.exception.DaoException;

import org.fakeskymeal.dao.impl.CateringOrderDaoImpl;
import org.fakeskymeal.dao.impl.FlightDaoImpl;
import org.fakeskymeal.dao.impl.BeverageDaoImpl;
import org.fakeskymeal.dao.impl.MealDaoImpl;

import org.fakeskymeal.dto.CateringOrderDto;
import org.fakeskymeal.dto.FlightDto;
import org.fakeskymeal.dto.BeverageDto;

import org.fakeskymeal.dto.MealDto;
import util.jdbc.ConnectionPool;
import util.jdbc.ConnectionPoolSingleton;

import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A mock application to simulate a Manager creating a new Catering Order.
 * Assuming a predefined flight, beverage and meal in the inventory already.
 * Refer to PopulateDB.sql for instantiated entities.
 */
public class App {
    private static final Logger LOGGER = Logger.getLogger(App.class.getName());
    private static final ConnectionPool pool = ConnectionPoolSingleton.getInstance();

    public static void main(String[] args) {
        System.out.println("=== Starting Catering Order Creation ===");

        try {
            CateringOrderDao orderDao = new CateringOrderDaoImpl(pool);
            BeverageDao beverageDao = new BeverageDaoImpl(pool);
            MealDao mealDao = new MealDaoImpl(pool);
            FlightDao flightDao = new FlightDaoImpl(pool);

            // Load an existing flight
            int flightId = 1;
            FlightDto flight = flightDao.get(flightId);
            System.out.println("Using flight: " + flight.getFlightId());

            // Load an existing beverage
            int beverageId = 1;
            BeverageDto beverage = beverageDao.get(beverageId);
            System.out.println("Using beverage: " + beverage.getName());

            // Load an existing meal
            int mealId = 1;
            MealDto meal = mealDao.get(mealId);
            System.out.println("Using meal: " + meal.getName());

            // Create new catering order
            int facilityId = 1;
            CateringOrderDto newOrder = new CateringOrderDto();
            newOrder.setFlightId(flightId);
            newOrder.setFacilityId(facilityId);
            newOrder.setDeliveryTime(LocalDateTime.now().plusDays(1));

            orderDao.save(newOrder);
            System.out.println("Created catering order with ID: " + newOrder.getOrderId());

            // Associate beverage with the new order
            orderDao.addBeverageToOrder(newOrder.getOrderId(), beverageId, 40);
            System.out.println("Associated beverage '" + beverage.getName() + "' to order.");

            // Associate meal with the new order
            orderDao.addMealToOrder(newOrder.getOrderId(), mealId, 90);
            System.out.println("Associated meal '" + meal.getName() + "' to order.");

            // Print all beverages for the order
            List<BeverageDto> beverages = orderDao.getBeveragesForOrder(newOrder.getOrderId());
            System.out.println("\nBeverage(s) linked to order:");
            for (BeverageDto b : beverages) {
                System.out.println(b.toJson());
            }

            System.out.println();

            // Print all meals for the order
            List<MealDto> meals = orderDao.getMealsForOrder(newOrder.getOrderId());
            System.out.println("Meal(s) linked to order:");
            for (MealDto m : meals) {
                System.out.println(m.toJson());
            }
        } catch (DaoException e) {
            LOGGER.log(Level.SEVERE, "DAO Error", e.getMessage());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in simulated app.", e.getStackTrace());
        }

        System.out.println("=== Finished ===");
    }
}
