package org.fakeskymeal.dao;

import org.fakeskymeal.dao.exception.DaoException;
import org.fakeskymeal.dto.AirlineDto;

import java.util.List;

public interface AirlineDao {
    /**
     * AirlineDao
     *
     * Interface for Data Access Object, AirlineDao
     */
    AirlineDto get(Integer id) throws DaoException;

    AirlineDto getRow(String field, Object value) throws DaoException;

    List<AirlineDto> getRows(String field, Object value) throws DaoException;

    List<AirlineDto> getAll() throws DaoException;

    void save(AirlineDto t) throws DaoException;

    void update(AirlineDto t, String[] params) throws DaoException;

    void delete(AirlineDto t) throws DaoException;
}
