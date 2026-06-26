package com.awrs.repository;

import com.awrs.model.WarehouseLocation;

import java.util.List;
import java.util.Optional;

public interface WarehouseLocationRepository {

    Optional<WarehouseLocation> findById(String id);

    List<WarehouseLocation> findAll();

    void save(WarehouseLocation location);

    boolean existsById(String id);
}
