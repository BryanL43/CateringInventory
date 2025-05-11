package org.fakeskymeal.dao;

import org.fakeskymeal.dao.exception.DaoException;
import org.fakeskymeal.dto.FlightDto;

import java.util.List;

public interface FlightDao {
    /**
     * FlightDao
     *
     * Interface for Data Access Object, FlightDao
     */
    FlightDto get(Integer id) throws DaoException;

    FlightDto getRow(String field, Object value) throws DaoException;

    List<FlightDto> getRows(String field, Object value) throws DaoException;

    List<FlightDto> getAll() throws DaoException;

    void save(FlightDto t) throws DaoException;

    void update(FlightDto t, String[] params) throws DaoException;

    void delete(FlightDto t) throws DaoException;

    List<FlightDto> getFlightsByAirlineName(String companyName) throws DaoException;
}
