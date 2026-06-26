package com.awrs.model;

import java.util.Objects;

public class InventoryRecord {

    private final String itemSku;
    private final String locationId;
    private int quantity;

    public InventoryRecord(String itemSku, String locationId, int quantity) {
        if (itemSku == null || itemSku.isBlank()) {
            throw new IllegalArgumentException("Item SKU is required");
        }
        if (locationId == null || locationId.isBlank()) {
            throw new IllegalArgumentException("Location id is required");
        }
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        this.itemSku = itemSku;
        this.locationId = locationId;
        this.quantity = quantity;
    }

    public String getItemSku() {
        return itemSku;
    }

    public String getLocationId() {
        return locationId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void increaseQuantity(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        quantity += amount;
    }

    public void decreaseQuantity(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        quantity -= amount;
    }

    public String getKey() {
        return itemSku + "@" + locationId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        InventoryRecord that = (InventoryRecord) o;
        return Objects.equals(itemSku, that.itemSku) && Objects.equals(locationId, that.locationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemSku, locationId);
    }
}
