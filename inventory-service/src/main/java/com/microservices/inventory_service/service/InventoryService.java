package com.microservices.inventory_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservices.inventory_service.dto.BookDto;
import com.microservices.inventory_service.model.InventoryItem;
import com.microservices.inventory_service.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;

    private static final String TOPIC = "inventory-events";

    private final KafkaTemplate<String, InventoryItem> kafkaTemplate;


    public void sendInventoryCreatedEvent(InventoryItem inventoryItem) {
        kafkaTemplate.send(TOPIC, inventoryItem.getId().toString(), inventoryItem);
    }

    @KafkaListener(topics = "book-events", groupId = "group_id")
    public void consumeBookEvent(BookDto book) {
        if ("create".equals(book.getAction())) {
            List<InventoryItem> existingItems = inventoryRepository.findByBookId(book.getId());
            if (existingItems.isEmpty()) {
                InventoryItem newItem = new InventoryItem();
                newItem.setBookId(book.getId());
                newItem.setStatus("not available");
                newItem.setQuantity(0);
                newItem.setPrice(0.0);
                inventoryRepository.save(newItem);
                System.out.println("Created new inventory item for book ID: " + book.getId());
            }
        } else if ("delete".equals(book.getAction())) {
            List<InventoryItem> existingItems = inventoryRepository.findByBookId(book.getId());
            if (!existingItems.isEmpty()) {
                InventoryItem itemToDelete = existingItems.get(0);
                inventoryRepository.delete(itemToDelete);
                System.out.println("Deleted inventory item for book ID: " + book.getId());
            }
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
        sendInventoryCreatedEvent(inventoryItem);
    }

    public void deleteInventoryItem(Long id) {
        inventoryRepository.deleteById(id);
    }

    public InventoryItem createInventoryItem(InventoryItem inventoryItem) {
        return inventoryRepository.save(inventoryItem);
    }
}
