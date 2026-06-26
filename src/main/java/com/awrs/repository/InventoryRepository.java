package com.awrs.repository;

import com.awrs.model.InventoryRecord;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository {

    Optional<InventoryRecord> findByItemAndLocation(String itemSku, String locationId);

    List<InventoryRecord> findAll();

    void save(InventoryRecord record);
}
