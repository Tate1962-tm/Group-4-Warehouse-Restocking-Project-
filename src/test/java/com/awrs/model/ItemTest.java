package com.awrs.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ItemTest {

    @Test
    void validItemCreation() {
        Item item = new Item("SKU-001", "Widget", "Acme", "EA");
        assertEquals("SKU-001", item.getSku());
        assertEquals("Widget", item.getDescription());
    }

    @Test
    void emptySkuRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Item("", "Widget", "Acme", "EA"));
    }

    @Test
    void emptyDescriptionRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Item("SKU-001", "", "Acme", "EA"));
    }
}
