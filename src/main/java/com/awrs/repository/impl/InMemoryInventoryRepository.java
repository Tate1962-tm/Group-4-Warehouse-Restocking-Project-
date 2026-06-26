package com.awrs.repository.impl;

import com.awrs.model.InventoryRecord;
import com.awrs.repository.InventoryRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryInventoryRepository implements InventoryRepository {

    private final Map<String, InventoryRecord> records = new ConcurrentHashMap<>();

    @Override
    public Optional<InventoryRecord> findByItemAndLocation(String itemSku, String locationId) {
        return Optional.ofNullable(records.get(itemSku + "@" + locationId));
    }

    @Override
    public List<InventoryRecord> findAll() {
        return new ArrayList<>(records.values());
    }

    @Override
    public void save(InventoryRecord record) {
        records.put(record.getKey(), record);
    }
}
