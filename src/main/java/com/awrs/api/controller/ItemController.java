package com.awrs.api.controller;

import com.awrs.api.dto.CreateItemRequest;
import com.awrs.api.dto.ItemResponse;
import com.awrs.model.Item;
import com.awrs.service.InventoryService;
import com.awrs.service.SessionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
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
@RequestMapping("/api/items")
public class ItemController {

    private final InventoryService inventoryService;
    private final SessionService sessionService;

    public ItemController(InventoryService inventoryService, SessionService sessionService) {
        this.inventoryService = inventoryService;
        this.sessionService = sessionService;
    }

    @GetMapping
    public List<ItemResponse> listItems(@RequestHeader("Authorization") String token) {
        sessionService.requireUser(token);
        return inventoryService.listItems().stream().map(ItemResponse::from).toList();
    }

    @GetMapping("/{sku}")
    public ItemResponse getItem(@RequestHeader("Authorization") String token, @PathVariable String sku) {
        sessionService.requireUser(token);
        return ItemResponse.from(inventoryService.getItem(sku));
    }

    @PostMapping
    public ResponseEntity<ItemResponse> createItem(@RequestHeader("Authorization") String token,
                                                   @Valid @RequestBody CreateItemRequest request) {
        sessionService.requireUser(token);
        Item item = inventoryService.createItem(request.toItem());
        return ResponseEntity.status(HttpStatus.CREATED).body(ItemResponse.from(item));
    }
}
