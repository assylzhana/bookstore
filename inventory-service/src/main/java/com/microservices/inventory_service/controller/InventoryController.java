package com.microservices.inventory_service.controller;

import com.microservices.inventory_service.model.InventoryItem;
import com.microservices.inventory_service.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @GetMapping
    public ResponseEntity<List<InventoryItem>> getAllInventoryItems() {
        List<InventoryItem> inventoryItems = inventoryService.getAllInventoryItems();
        return new ResponseEntity<>(inventoryItems, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventoryItem> getInventoryItem(@PathVariable Long id) {
        InventoryItem inventoryItem = inventoryService.getInventoryItem(id);
        if (inventoryItem != null) {
            return new ResponseEntity<>(inventoryItem, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public ResponseEntity<InventoryItem> createInventoryItem(@RequestBody InventoryItem inventoryItem) {
        InventoryItem createdInventoryItem = inventoryService.createInventoryItem(inventoryItem);
        return new ResponseEntity<>(createdInventoryItem, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<InventoryItem> editInventoryItem(@PathVariable Long id, @RequestBody InventoryItem inventoryItemDetails) {
        InventoryItem inventoryItem = inventoryService.getInventoryItem(id);
        if (inventoryItem != null) {
            if(inventoryItemDetails.getQuantity() == 0){
                inventoryItem.setStatus("not available");
            }
            else{
                inventoryItem.setStatus("available");
            }
            if (inventoryItemDetails.getQuantity() !=null){
                inventoryItem.setQuantity(inventoryItemDetails.getQuantity());
            }
            if (inventoryItemDetails.getPrice() !=null){
                inventoryItem.setPrice(inventoryItemDetails.getPrice());
            }
            inventoryService.editInventoryItem(inventoryItem);
            return new ResponseEntity<>(inventoryItem, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInventoryItem(@PathVariable Long id) {
        InventoryItem inventoryItem = inventoryService.getInventoryItem(id);
        if (inventoryItem != null) {
            inventoryService.deleteInventoryItem(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
