package com.awrs.api.dto;

import com.awrs.model.Role;

import java.util.List;

public record DashboardResponse(
        int totalItems,
        int totalLocations,
        int totalInventoryRecords,
        int lowStockCount,
        int pendingRestockTasks,
        int completedRestockTasks,
        List<LowStockAlert> lowStockAlerts,
        List<RestockTaskResponse> urgentTasks,
        List<AuditLogResponse> recentActivity
) {
    public record LowStockAlert(
            String itemSku,
            String description,
            String locationId,
            int quantity,
            int minimumThreshold,
            int reorderPoint,
            String riskLevel
    ) {
    }
}
