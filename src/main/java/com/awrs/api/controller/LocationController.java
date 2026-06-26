package com.awrs.api.controller;

import com.awrs.api.dto.CreateLocationRequest;
import com.awrs.api.dto.LocationResponse;
import com.awrs.exception.ResourceNotFoundException;
import com.awrs.model.WarehouseLocation;
import com.awrs.repository.WarehouseLocationRepository;
import com.awrs.service.InventoryService;
import com.awrs.service.SessionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/locations")
public class LocationController {

    private final InventoryService inventoryService;
    private final WarehouseLocationRepository locationRepository;
    private final SessionService sessionService;

    public LocationController(InventoryService inventoryService,
                              WarehouseLocationRepository locationRepository,
                              SessionService sessionService) {
        this.inventoryService = inventoryService;
        this.locationRepository = locationRepository;
        this.sessionService = sessionService;
    }

    @GetMapping
    public List<LocationResponse> listLocations(@RequestHeader("Authorization") String token) {
        sessionService.requireUser(token);
        return inventoryService.listLocations().stream().map(LocationResponse::from).toList();
    }

    @PostMapping
    public ResponseEntity<LocationResponse> createLocation(@RequestHeader("Authorization") String token,
                                                           @Valid @RequestBody CreateLocationRequest request) {
        sessionService.requireUser(token);
        WarehouseLocation parent = null;
        if (request.parentId() != null && !request.parentId().isBlank()) {
            parent = locationRepository.findById(request.parentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent location not found: " + request.parentId()));
        }
        WarehouseLocation location = new WarehouseLocation(request.id(), request.name(), request.type(), parent);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(LocationResponse.from(inventoryService.createLocation(location)));
    }
}
