package org.fakeskymeal.dao;

import org.fakeskymeal.dao.exception.DaoException;

import java.util.List;

public interface BaseDao<T> {
    /**
     * BaseDao
     *
     * Generic Interface for all Data Access Object
     */
    T get(Integer id) throws DaoException;

    T getRow(String field, Object value) throws DaoException;

    List<T> getRows(String field, Object value) throws DaoException;

    List<T> getAll() throws DaoException;

    void save(T dto) throws DaoException;

    void update(T dto, String[] params) throws DaoException;

    void delete(T dto) throws DaoException;
}