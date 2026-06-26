package com.awrs.api.controller;

import com.awrs.api.dto.AdjustInventoryRequest;
import com.awrs.api.dto.AuditLogResponse;
import com.awrs.api.dto.InventoryRecordResponse;
import com.awrs.api.dto.InventoryTransactionRequest;
import com.awrs.model.InventoryRecord;
import com.awrs.service.InventoryService;
import com.awrs.service.SessionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;
    private final SessionService sessionService;

    public InventoryController(InventoryService inventoryService, SessionService sessionService) {
        this.inventoryService = inventoryService;
        this.sessionService = sessionService;
    }

    @GetMapping
    public List<InventoryRecordResponse> listInventory(@RequestHeader("Authorization") String token) {
        sessionService.requireUser(token);
        return inventoryService.listInventory().stream().map(InventoryRecordResponse::from).toList();
    }

    @GetMapping("/{itemSku}/{locationId}")
    public InventoryRecordResponse getInventory(@RequestHeader("Authorization") String token,
                                                @PathVariable String itemSku,
                                                @PathVariable String locationId) {
        sessionService.requireUser(token);
        return InventoryRecordResponse.from(inventoryService.getInventory(itemSku, locationId));
    }

    @PostMapping("/receive")
    public InventoryRecordResponse receive(@RequestHeader("Authorization") String token,
                                           @Valid @RequestBody InventoryTransactionRequest request) {
        var user = sessionService.requireUser(token);
        InventoryRecord record = inventoryService.receiveShipment(
                request.itemSku(), request.locationId(), request.quantity(), user.getUsername());
        return InventoryRecordResponse.from(record);
    }

    @PostMapping("/fulfill")
    public InventoryRecordResponse fulfill(@RequestHeader("Authorization") String token,
                                           @Valid @RequestBody InventoryTransactionRequest request) {
        var user = sessionService.requireUser(token);
        InventoryRecord record = inventoryService.fulfillOrder(
                request.itemSku(), request.locationId(), request.quantity(), user.getUsername());
        return InventoryRecordResponse.from(record);
    }

    @PostMapping("/adjust")
    public InventoryRecordResponse adjust(@RequestHeader("Authorization") String token,
                                          @Valid @RequestBody AdjustInventoryRequest request) {
        var user = sessionService.requireUser(token);
        InventoryRecord record = inventoryService.adjustInventory(
                user, request.itemSku(), request.locationId(), request.newQuantity(), request.reason());
        return InventoryRecordResponse.from(record);
    }

    @GetMapping("/audit-logs")
    public List<AuditLogResponse> auditLogs(@RequestHeader("Authorization") String token) {
        sessionService.requireUser(token);
        return inventoryService.listAuditLogs().stream().map(AuditLogResponse::from).toList();
    }
}
