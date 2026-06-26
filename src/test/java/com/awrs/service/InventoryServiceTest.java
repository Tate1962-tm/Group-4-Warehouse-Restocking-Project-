package com.awrs.service;

import com.awrs.exception.InsufficientStockException;
import com.awrs.model.AuditLog;
import com.awrs.model.InventoryRecord;
import com.awrs.model.Item;
import com.awrs.model.Role;
import com.awrs.model.User;
import com.awrs.model.WarehouseLocation;
import com.awrs.repository.AuditLogRepository;
import com.awrs.repository.InventoryRepository;
import com.awrs.repository.ItemRepository;
import com.awrs.repository.WarehouseLocationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock private InventoryRepository inventoryRepository;
    @Mock private AuditLogRepository auditLogRepository;
    @Mock private ItemRepository itemRepository;
    @Mock private WarehouseLocationRepository locationRepository;
    @Mock private AuthService authService;

    private InventoryService inventoryService;

    @BeforeEach
    void setUp() {
        inventoryService = new InventoryService(
                inventoryRepository, auditLogRepository, itemRepository, locationRepository, authService);
    }

    @Test
    void receiveShipmentIncreasesQuantity() {
        stubItemAndLocation();
        when(inventoryRepository.findByItemAndLocation("SKU-001", "LOC-1"))
                .thenReturn(Optional.of(new InventoryRecord("SKU-001", "LOC-1", 5)));

        InventoryRecord result = inventoryService.receiveShipment("SKU-001", "LOC-1", 10, "worker");

        assertEquals(15, result.getQuantity());
        verify(auditLogRepository).save(any(AuditLog.class));
    }

    @Test
    void fulfillOrderDeductsQuantity() {
        stubItemAndLocation();
        when(inventoryRepository.findByItemAndLocation("SKU-001", "LOC-1"))
                .thenReturn(Optional.of(new InventoryRecord("SKU-001", "LOC-1", 20)));

        InventoryRecord result = inventoryService.fulfillOrder("SKU-001", "LOC-1", 5, "worker");

        assertEquals(15, result.getQuantity());
    }

    @Test
    void fulfillOrderInsufficientStock() {
        stubItemAndLocation();
        when(inventoryRepository.findByItemAndLocation("SKU-001", "LOC-1"))
                .thenReturn(Optional.of(new InventoryRecord("SKU-001", "LOC-1", 3)));

        assertThrows(InsufficientStockException.class,
                () -> inventoryService.fulfillOrder("SKU-001", "LOC-1", 5, "worker"));
    }

    @Test
    void adjustInventoryRequiresManagerRole() {
        User worker = new User("worker", "pass", Role.WORKER);
        assertThrows(Exception.class,
                () -> inventoryService.adjustInventory(worker, "SKU-001", "LOC-1", 10, "cycle count"));
    }

    private void stubItemAndLocation() {
        when(itemRepository.existsBySku("SKU-001")).thenReturn(true);
        when(locationRepository.existsById("LOC-1")).thenReturn(true);
    }
}
