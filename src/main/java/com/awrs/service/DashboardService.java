package com.awrs.service;

import com.awrs.api.dto.AuditLogResponse;
import com.awrs.api.dto.DashboardResponse;
import com.awrs.api.dto.RestockTaskResponse;
import com.awrs.model.InventoryRecord;
import com.awrs.model.Item;
import com.awrs.model.RestockTask;
import com.awrs.model.RestockTaskStatus;
import com.awrs.repository.AuditLogRepository;
import com.awrs.repository.InventoryRepository;
import com.awrs.repository.ItemRepository;
import com.awrs.repository.RestockTaskRepository;
import com.awrs.repository.WarehouseLocationRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final ItemRepository itemRepository;
    private final WarehouseLocationRepository locationRepository;
    private final InventoryRepository inventoryRepository;
    private final RestockTaskRepository restockTaskRepository;
    private final AuditLogRepository auditLogRepository;

    public DashboardService(ItemRepository itemRepository,
                            WarehouseLocationRepository locationRepository,
                            InventoryRepository inventoryRepository,
                            RestockTaskRepository restockTaskRepository,
                            AuditLogRepository auditLogRepository) {
        this.itemRepository = itemRepository;
        this.locationRepository = locationRepository;
        this.inventoryRepository = inventoryRepository;
        this.restockTaskRepository = restockTaskRepository;
        this.auditLogRepository = auditLogRepository;
    }

    public DashboardResponse getDashboard() {
        Map<String, Item> itemsBySku = itemRepository.findAll().stream()
                .collect(Collectors.toMap(Item::getSku, Function.identity()));

        List<InventoryRecord> records = inventoryRepository.findAll();
        List<DashboardResponse.LowStockAlert> alerts = new ArrayList<>();

        for (InventoryRecord record : records) {
            Item item = itemsBySku.get(record.getItemSku());
            if (item == null) {
                continue;
            }
            if (record.getQuantity() < item.getMinimumThreshold()) {
                alerts.add(new DashboardResponse.LowStockAlert(
                        item.getSku(),
                        item.getDescription(),
                        record.getLocationId(),
                        record.getQuantity(),
                        item.getMinimumThreshold(),
                        item.getReorderPoint(),
                        "CRITICAL"));
            } else if (record.getQuantity() < item.getReorderPoint()) {
                alerts.add(new DashboardResponse.LowStockAlert(
                        item.getSku(),
                        item.getDescription(),
                        record.getLocationId(),
                        record.getQuantity(),
                        item.getMinimumThreshold(),
                        item.getReorderPoint(),
                        "WARNING"));
            }
        }

        alerts.sort(Comparator.comparingInt(DashboardResponse.LowStockAlert::quantity));

        List<RestockTask> urgentTasks = restockTaskRepository.findAll().stream()
                .filter(task -> task.getStatus() != RestockTaskStatus.COMPLETED)
                .sorted(Comparator.comparingInt(RestockTask::getPriority))
                .limit(5)
                .toList();

        List<AuditLogResponse> recentActivity = auditLogRepository.findAll().stream()
                .sorted(Comparator.comparing(log -> log.getTimestamp(), Comparator.reverseOrder()))
                .limit(10)
                .map(AuditLogResponse::from)
                .toList();

        long pendingTasks = restockTaskRepository.findAll().stream()
                .filter(task -> task.getStatus() != RestockTaskStatus.COMPLETED)
                .count();

        long completedTasks = restockTaskRepository.findAll().stream()
                .filter(task -> task.getStatus() == RestockTaskStatus.COMPLETED)
                .count();

        return new DashboardResponse(
                itemRepository.findAll().size(),
                locationRepository.findAll().size(),
                records.size(),
                (int) alerts.stream().filter(a -> "CRITICAL".equals(a.riskLevel())).count(),
                (int) pendingTasks,
                (int) completedTasks,
                alerts,
                urgentTasks.stream().map(RestockTaskResponse::from).toList(),
                recentActivity);
    }
}
