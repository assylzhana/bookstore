package com.microservices.inventory_service.service;

import com.microservices.inventory_service.dto.Book;
import com.microservices.inventory_service.model.InventoryItem;
import com.microservices.inventory_service.repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;

    @KafkaListener(topics = "book-events", groupId = "inventory-service-group")
    public void consumeBookEvent(Book book) {
        System.out.println("Received event for book: " + book);
        List<InventoryItem> existingItems = inventoryRepository.findByBookId(book.getId());
        if (existingItems.isEmpty()) {
            InventoryItem newItem = new InventoryItem();
            newItem.setBookId(book.getId());
            newItem.setStatus("available");
            newItem.setQuantity(0);
            newItem.setPrice(0.0);
            inventoryRepository.save(newItem);
            System.out.println("Created new inventory item for book ID: " + book.getId());
        }
    }



    public List<InventoryItem> getAllInventoryItems() {
        return inventoryRepository.findAll();
    }

    public InventoryItem getInventoryItem(Long id) {
        return inventoryRepository.findById(id).orElse(null);
    }

    public void editInventoryItem(InventoryItem inventoryItem) {
        inventoryRepository.save(inventoryItem);
    }

    public void deleteInventoryItem(Long id) {
        inventoryRepository.deleteById(id);
    }

    public InventoryItem createInventoryItem(InventoryItem inventoryItem) {
        return inventoryRepository.save(inventoryItem);
    }
}
