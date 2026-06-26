package com.awrs.service;

import com.awrs.exception.ResourceNotFoundException;
import com.awrs.model.AuditLog;
import com.awrs.model.InventoryRecord;
import com.awrs.model.Item;
import com.awrs.model.RestockTask;
import com.awrs.model.RestockTaskStatus;
import com.awrs.repository.AuditLogRepository;
import com.awrs.repository.InventoryRepository;
import com.awrs.repository.ItemRepository;
import com.awrs.repository.RestockTaskRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class RestockService {

    private final RestockTaskRepository restockTaskRepository;
    private final InventoryRepository inventoryRepository;
    private final ItemRepository itemRepository;
    private final InventoryService inventoryService;
    private final AuditLogRepository auditLogRepository;

    public RestockService(RestockTaskRepository restockTaskRepository,
                          InventoryRepository inventoryRepository,
                          ItemRepository itemRepository,
                          InventoryService inventoryService,
                          AuditLogRepository auditLogRepository) {
        this.restockTaskRepository = restockTaskRepository;
        this.inventoryRepository = inventoryRepository;
        this.itemRepository = itemRepository;
        this.inventoryService = inventoryService;
        this.auditLogRepository = auditLogRepository;
    }

    public RestockTask evaluateThreshold(String itemSku, String locationId) {
        Item item = itemRepository.findBySku(itemSku)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found: " + itemSku));

        InventoryRecord record = inventoryRepository.findByItemAndLocation(itemSku, locationId)
                .orElse(null);

        int quantity = record == null ? 0 : record.getQuantity();
        if (quantity >= item.getMinimumThreshold()) {
            return null;
        }

        int priority = Math.max(1, item.getMinimumThreshold() - quantity);
        int requestedQuantity = Math.max(item.getReorderPoint() - quantity, 1);
        RestockTask task = new RestockTask(itemSku, locationId, requestedQuantity, priority);
        restockTaskRepository.save(task);
        return task;
    }

    public List<RestockTask> batchEvaluate() {
        List<RestockTask> created = new ArrayList<>();
        for (InventoryRecord record : inventoryRepository.findAll()) {
            RestockTask task = evaluateThreshold(record.getItemSku(), record.getLocationId());
            if (task != null) {
                created.add(task);
            }
        }
        created.sort(Comparator.comparingInt(RestockTask::getPriority));
        return created;
    }

    public List<RestockTask> listTasks() {
        return restockTaskRepository.findAll().stream()
                .sorted(Comparator.comparingInt(RestockTask::getPriority))
                .toList();
    }

    public List<RestockTask> listPendingTasks() {
        return restockTaskRepository.findAll().stream()
                .filter(task -> task.getStatus() != RestockTaskStatus.COMPLETED)
                .sorted(Comparator.comparingInt(RestockTask::getPriority))
                .toList();
    }

    public RestockTask getTask(String taskId) {
        return restockTaskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Restock task not found: " + taskId));
    }

    public RestockTask assignTask(String taskId, String workerUsername) {
        RestockTask task = getTask(taskId);
        task.setStatus(RestockTaskStatus.ASSIGNED);
        restockTaskRepository.save(task);
        return task;
    }

    public RestockTask completeTask(String taskId, String performedBy) {
        RestockTask task = getTask(taskId);
        if (task.getStatus() == RestockTaskStatus.COMPLETED) {
            throw new IllegalStateException("Task already completed: " + taskId);
        }

        inventoryService.receiveShipment(
                task.getItemSku(), task.getLocationId(), task.getRequestedQuantity(), performedBy);
        task.setStatus(RestockTaskStatus.COMPLETED);
        restockTaskRepository.save(task);
        auditLogRepository.save(new AuditLog(
                AuditLog.Action.RESTOCK_COMPLETE,
                task.getItemSku(),
                task.getLocationId(),
                task.getRequestedQuantity(),
                performedBy,
                "Restock task " + taskId));
        return task;
    }
}
