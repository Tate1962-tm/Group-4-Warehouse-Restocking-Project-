package com.awrs.service;

import com.awrs.model.InventoryRecord;
import com.awrs.model.Item;
import com.awrs.model.RestockTask;
import com.awrs.model.RestockTaskStatus;
import com.awrs.repository.AuditLogRepository;
import com.awrs.repository.InventoryRepository;
import com.awrs.repository.ItemRepository;
import com.awrs.repository.RestockTaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestockServiceTest {

    @Mock private RestockTaskRepository restockTaskRepository;
    @Mock private InventoryRepository inventoryRepository;
    @Mock private ItemRepository itemRepository;
    @Mock private InventoryService inventoryService;
    @Mock private AuditLogRepository auditLogRepository;

    private RestockService restockService;

    @BeforeEach
    void setUp() {
        restockService = new RestockService(
                restockTaskRepository, inventoryRepository, itemRepository, inventoryService, auditLogRepository);
    }

    @Test
    void evaluateThresholdCreatesTaskWhenBelowMinimum() {
        Item item = new Item("SKU-001", "Widget", "Acme", "EA", 10, 50);
        when(itemRepository.findBySku("SKU-001")).thenReturn(Optional.of(item));
        when(inventoryRepository.findByItemAndLocation("SKU-001", "LOC-1"))
                .thenReturn(Optional.of(new InventoryRecord("SKU-001", "LOC-1", 5)));

        RestockTask task = restockService.evaluateThreshold("SKU-001", "LOC-1");

        assertNotNull(task);
        assertEquals(RestockTaskStatus.CREATED, task.getStatus());
        verify(restockTaskRepository).save(any(RestockTask.class));
    }

    @Test
    void noTaskWhenQuantityAtMinimum() {
        Item item = new Item("SKU-001", "Widget", "Acme", "EA", 10, 50);
        when(itemRepository.findBySku("SKU-001")).thenReturn(Optional.of(item));
        when(inventoryRepository.findByItemAndLocation("SKU-001", "LOC-1"))
                .thenReturn(Optional.of(new InventoryRecord("SKU-001", "LOC-1", 10)));

        RestockTask task = restockService.evaluateThreshold("SKU-001", "LOC-1");

        assertNull(task);
    }

    @Test
    void completeTaskUpdatesInventory() {
        RestockTask task = new RestockTask("task-1", "SKU-001", "LOC-1", 20, 5, RestockTaskStatus.ASSIGNED);
        when(restockTaskRepository.findById("task-1")).thenReturn(Optional.of(task));
        when(inventoryService.receiveShipment("SKU-001", "LOC-1", 20, "worker"))
                .thenReturn(new InventoryRecord("SKU-001", "LOC-1", 20));

        RestockTask completed = restockService.completeTask("task-1", "worker");

        assertEquals(RestockTaskStatus.COMPLETED, completed.getStatus());
    }

    @Test
    void batchEvaluateProcessesMultipleRecords() {
        Item item = new Item("SKU-001", "Widget", "Acme", "EA", 10, 50);
        when(inventoryRepository.findAll())
                .thenReturn(List.of(new InventoryRecord("SKU-001", "LOC-1", 3)));
        when(itemRepository.findBySku("SKU-001")).thenReturn(Optional.of(item));
        when(inventoryRepository.findByItemAndLocation("SKU-001", "LOC-1"))
                .thenReturn(Optional.of(new InventoryRecord("SKU-001", "LOC-1", 3)));

        List<RestockTask> tasks = restockService.batchEvaluate();

        assertEquals(1, tasks.size());
    }
}
