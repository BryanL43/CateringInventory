package org.fakeskymeal.dao;

import org.fakeskymeal.dao.exception.DaoException;
import org.fakeskymeal.dto.FacilityDto;

import java.util.List;

public interface FacilityDao {
    /**
     * FacilityDao
     *
     * Interface for Data Access Object, FacilityDao
     */
    FacilityDto get(Integer id) throws DaoException;

    FacilityDto getRow(String field, Object value) throws DaoException;

    List<FacilityDto> getRows(String field, Object value) throws DaoException;

    List<FacilityDto> getAll() throws DaoException;

    void save(FacilityDto t) throws DaoException;

    void update(FacilityDto t, String[] params) throws DaoException;

    void delete(FacilityDto t) throws DaoException;
}
