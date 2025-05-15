package org.fakeskymeal.dao;

import org.fakeskymeal.dao.exception.DaoException;
import org.fakeskymeal.dto.BeverageDto;
import org.fakeskymeal.dto.CateringOrderDto;
import org.fakeskymeal.dto.MealDto;

import java.util.List;

public interface CateringOrderDao extends BaseDao<CateringOrderDto> {
    /**
     * CateringOrderDao
     *
     * Interface for Data Access Object, CateringOrderDao.
     * Additional methods not in BaseDao can be declared here.
     */
    List<BeverageDto> getBeveragesForOrder(int orderId) throws DaoException;
    void addBeverageToOrder(int orderId, int beverageId, int quantity) throws DaoException;
    void removeBeverageFromOrder(int orderId, int beverageId) throws DaoException;

    List<MealDto> getMealsForOrder(int orderId) throws DaoException;
    void addMealToOrder(int orderId, int mealId, int quantity) throws DaoException;
    void removeMealFromOrder(int orderId, int mealId) throws DaoException;
}
