package com.awrs.repository;

import com.awrs.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {

    Optional<Item> findBySku(String sku);

    List<Item> findAll();

    void save(Item item);

    void delete(String sku);

    boolean existsBySku(String sku);
}
