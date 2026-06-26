package com.awrs.model;

import java.util.Objects;

public class Item {

    private final String sku;
    private final String description;
    private final String supplier;
    private final String unitOfMeasure;
    private final int minimumThreshold;
    private final int reorderPoint;

    public Item(String sku, String description, String supplier, String unitOfMeasure,
                int minimumThreshold, int reorderPoint) {
        if (sku == null || sku.isBlank()) {
            throw new IllegalArgumentException("SKU cannot be empty");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Description cannot be empty");
        }
        this.sku = sku;
        this.description = description;
        this.supplier = supplier == null ? "" : supplier;
        this.unitOfMeasure = unitOfMeasure == null ? "EA" : unitOfMeasure;
        this.minimumThreshold = minimumThreshold;
        this.reorderPoint = reorderPoint;
    }

    public Item(String sku, String description, String supplier, String unitOfMeasure) {
        this(sku, description, supplier, unitOfMeasure, 10, 20);
    }

    public String getSku() {
        return sku;
    }

    public String getDescription() {
        return description;
    }

    public String getSupplier() {
        return supplier;
    }

    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public int getMinimumThreshold() {
        return minimumThreshold;
    }

    public int getReorderPoint() {
        return reorderPoint;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Item item = (Item) o;
        return Objects.equals(sku, item.sku);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sku);
    }
}
