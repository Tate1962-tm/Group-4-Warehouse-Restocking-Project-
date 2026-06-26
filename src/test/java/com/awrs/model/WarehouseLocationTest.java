package com.awrs.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WarehouseLocationTest {

    @Test
    void hierarchyPath() {
        WarehouseLocation aisle = new WarehouseLocation("A1", "Aisle 1", "AISLE", null);
        WarehouseLocation shelf = new WarehouseLocation("S1", "Shelf 1", "SHELF", aisle);
        assertEquals("Aisle 1 > Shelf 1", shelf.getFullPath());
    }

    @Test
    void emptyNameRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new WarehouseLocation("B1", "", "BIN", null));
    }
}
