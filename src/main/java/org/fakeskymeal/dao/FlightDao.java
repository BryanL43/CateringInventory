package org.fakeskymeal.dao;

import org.fakeskymeal.dao.exception.DaoException;
import org.fakeskymeal.dto.FlightDto;

import java.util.List;

public interface FlightDao extends BaseDao<FlightDto> {
    /**
     * FlightDao
     *
     * Interface for Data Access Object, FlightDao.
     * Additional methods not in BaseDao can be declared here.
     */
    List<FlightDto> getFlightsByAirlineName(String companyName) throws DaoException;
}
