package com.awrs.service;

import com.awrs.exception.DuplicateResourceException;
import com.awrs.exception.ForbiddenException;
import com.awrs.exception.InsufficientStockException;
import com.awrs.exception.ResourceNotFoundException;
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
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final AuditLogRepository auditLogRepository;
    private final ItemRepository itemRepository;
    private final WarehouseLocationRepository locationRepository;
    private final AuthService authService;

    public InventoryService(InventoryRepository inventoryRepository,
                            AuditLogRepository auditLogRepository,
                            ItemRepository itemRepository,
                            WarehouseLocationRepository locationRepository,
                            AuthService authService) {
        this.inventoryRepository = inventoryRepository;
        this.auditLogRepository = auditLogRepository;
        this.itemRepository = itemRepository;
        this.locationRepository = locationRepository;
        this.authService = authService;
    }

    public Item createItem(Item item) {
        if (itemRepository.existsBySku(item.getSku())) {
            throw new DuplicateResourceException("Item SKU already exists: " + item.getSku());
        }
        itemRepository.save(item);
        return item;
    }

    public List<Item> listItems() {
        return itemRepository.findAll();
    }

    public Item getItem(String sku) {
        return itemRepository.findBySku(sku)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found: " + sku));
    }

    public WarehouseLocation createLocation(WarehouseLocation location) {
        if (locationRepository.existsById(location.getId())) {
            throw new DuplicateResourceException("Location already exists: " + location.getId());
        }
        locationRepository.save(location);
        return location;
    }

    public List<WarehouseLocation> listLocations() {
        return locationRepository.findAll();
    }

    public InventoryRecord receiveShipment(String itemSku, String locationId, int quantity, String performedBy) {
        validateItemAndLocation(itemSku, locationId);
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        InventoryRecord record = inventoryRepository.findByItemAndLocation(itemSku, locationId)
                .orElseGet(() -> new InventoryRecord(itemSku, locationId, 0));
        record.increaseQuantity(quantity);
        inventoryRepository.save(record);
        auditLogRepository.save(new AuditLog(AuditLog.Action.RECEIVE, itemSku, locationId, quantity, performedBy, null));
        return record;
    }

    public InventoryRecord fulfillOrder(String itemSku, String locationId, int quantity, String performedBy) {
        validateItemAndLocation(itemSku, locationId);
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        InventoryRecord record = inventoryRepository.findByItemAndLocation(itemSku, locationId)
                .orElseThrow(() -> new InsufficientStockException(
                        "Insufficient stock for " + itemSku + " at " + locationId));

        if (record.getQuantity() < quantity) {
            throw new InsufficientStockException(
                    "Insufficient stock for " + itemSku + " at " + locationId
                            + ": requested " + quantity + ", available " + record.getQuantity());
        }

        record.decreaseQuantity(quantity);
        inventoryRepository.save(record);
        auditLogRepository.save(new AuditLog(AuditLog.Action.FULFILL, itemSku, locationId, -quantity, performedBy, null));
        return record;
    }

    public InventoryRecord adjustInventory(User user, String itemSku, String locationId,
                                           int newQuantity, String reason) {
        authService.requireAnyRole(user, Role.ADMIN, Role.MANAGER);
        validateItemAndLocation(itemSku, locationId);
        if (newQuantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }

        InventoryRecord record = inventoryRepository.findByItemAndLocation(itemSku, locationId)
                .orElseGet(() -> new InventoryRecord(itemSku, locationId, 0));
        int change = newQuantity - record.getQuantity();
        record = new InventoryRecord(itemSku, locationId, newQuantity);
        inventoryRepository.save(record);
        auditLogRepository.save(new AuditLog(
                AuditLog.Action.ADJUST, itemSku, locationId, change, user.getUsername(), reason));
        return record;
    }

    public List<InventoryRecord> listInventory() {
        return inventoryRepository.findAll();
    }

    public InventoryRecord getInventory(String itemSku, String locationId) {
        return inventoryRepository.findByItemAndLocation(itemSku, locationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No inventory for " + itemSku + " at " + locationId));
    }

    public List<AuditLog> listAuditLogs() {
        return auditLogRepository.findAll();
    }

    private void validateItemAndLocation(String itemSku, String locationId) {
        if (!itemRepository.existsBySku(itemSku)) {
            throw new ResourceNotFoundException("Item not found: " + itemSku);
        }
        if (!locationRepository.existsById(locationId)) {
            throw new ResourceNotFoundException("Location not found: " + locationId);
        }
    }
}
