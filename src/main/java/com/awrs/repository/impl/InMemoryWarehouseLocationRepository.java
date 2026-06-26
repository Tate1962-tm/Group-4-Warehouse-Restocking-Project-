package com.awrs.repository.impl;

import com.awrs.model.WarehouseLocation;
import com.awrs.repository.WarehouseLocationRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryWarehouseLocationRepository implements WarehouseLocationRepository {

    private final Map<String, WarehouseLocation> locations = new ConcurrentHashMap<>();

    @Override
    public Optional<WarehouseLocation> findById(String id) {
        return Optional.ofNullable(locations.get(id));
    }

    @Override
    public List<WarehouseLocation> findAll() {
        return new ArrayList<>(locations.values());
    }

    @Override
    public void save(WarehouseLocation location) {
        locations.put(location.getId(), location);
    }

    @Override
    public boolean existsById(String id) {
        return locations.containsKey(id);
    }
}
