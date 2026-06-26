package com.awrs.repository.impl;

import com.awrs.model.Item;
import com.awrs.repository.ItemRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryItemRepository implements ItemRepository {

    private final Map<String, Item> items = new ConcurrentHashMap<>();

    @Override
    public Optional<Item> findBySku(String sku) {
        return Optional.ofNullable(items.get(sku));
    }

    @Override
    public List<Item> findAll() {
        return new ArrayList<>(items.values());
    }

    @Override
    public void save(Item item) {
        items.put(item.getSku(), item);
    }

    @Override
    public void delete(String sku) {
        items.remove(sku);
    }

    @Override
    public boolean existsBySku(String sku) {
        return items.containsKey(sku);
    }
}
