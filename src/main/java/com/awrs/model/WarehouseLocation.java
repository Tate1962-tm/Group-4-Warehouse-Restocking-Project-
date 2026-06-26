package com.awrs.model;

import java.util.Objects;

public class WarehouseLocation {

    private final String id;
    private final String name;
    private final String type;
    private final WarehouseLocation parent;

    public WarehouseLocation(String id, String name, String type, WarehouseLocation parent) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Location id cannot be empty");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Location name cannot be empty");
        }
        this.id = id;
        this.name = name;
        this.type = type == null ? "BIN" : type;
        this.parent = parent;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public WarehouseLocation getParent() {
        return parent;
    }

    public String getFullPath() {
        if (parent == null) {
            return name;
        }
        return parent.getFullPath() + " > " + name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WarehouseLocation that = (WarehouseLocation) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
