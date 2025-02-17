package com.microservices.inventory_service.controller;

import com.microservices.inventory_service.model.InventoryItem;
import com.microservices.inventory_service.model.Status;
import com.microservices.inventory_service.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name="Inventory methods",  description = "Operations related to inventory")
@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @Operation(summary = "Get all inventory")
    @GetMapping
    public ResponseEntity<List<InventoryItem>> getAllInventoryItems() {
        try {
            List<InventoryItem> inventoryItems = inventoryService.getAllInventoryItems();
            return new ResponseEntity<>(inventoryItems, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Get inventory by id")
    @GetMapping("/{id}")
    public ResponseEntity<InventoryItem> getInventoryItem(@PathVariable Long id) {
        try {
            InventoryItem inventoryItem = inventoryService.getInventoryItem(id);
            if (inventoryItem != null) {
                return new ResponseEntity<>(inventoryItem, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Create inventory")
    @PostMapping
    public ResponseEntity<InventoryItem> createInventoryItem(@RequestBody InventoryItem inventoryItem) {
        try {
            InventoryItem createdInventoryItem = inventoryService.createInventoryItem(inventoryItem);
            return new ResponseEntity<>(createdInventoryItem, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Edit inventory")
    @PutMapping("/{id}")
    public ResponseEntity<InventoryItem> editInventoryItem(@PathVariable Long id, @RequestBody InventoryItem inventoryItemDetails) {
        try {
            InventoryItem inventoryItem = inventoryService.getInventoryItem(id);
            if (inventoryItem != null) {
                if (inventoryItemDetails.getQuantity() == 0) {
                    inventoryItem.setStatus(Status.Not_Available);
                } else {
                    inventoryItem.setStatus(Status.Available);
                }
                if (inventoryItemDetails.getQuantity() != null) {
                    inventoryItem.setQuantity(inventoryItemDetails.getQuantity());
                }
                if (inventoryItemDetails.getPrice() != null) {
                    inventoryItem.setPrice(inventoryItemDetails.getPrice());
                }
                inventoryService.editInventoryItem(inventoryItem);
                return new ResponseEntity<>(inventoryItem, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Delete inventory")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInventoryItem(@PathVariable Long id) {
        try {
            InventoryItem inventoryItem = inventoryService.getInventoryItem(id);
            if (inventoryItem != null) {
                inventoryService.deleteInventoryItem(id);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
