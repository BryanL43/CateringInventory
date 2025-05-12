package org.fakeskymeal.dao;

import org.fakeskymeal.dao.exception.DaoException;
import org.fakeskymeal.dto.InventoryDto;

import java.util.List;

public interface InventoryDao extends BaseDao<InventoryDto> {
    /**
     * InventoryDao
     *
     * Interface for Data Access Object, InventoryDao.
     * Additional methods not in BaseDao can be declared here.
     */
    List<InventoryDto> getAllByFacilityId(int facilityId) throws DaoException;
}
