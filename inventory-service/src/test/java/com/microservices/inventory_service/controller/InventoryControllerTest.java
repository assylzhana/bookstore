package com.microservices.inventory_service.controller;

import com.microservices.inventory_service.model.InventoryItem;
import com.microservices.inventory_service.model.Status;
import com.microservices.inventory_service.service.InventoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class InventoryControllerTest {

    @Mock
    private InventoryService inventoryService;

    @InjectMocks
    private InventoryController inventoryController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllInventoryItems() {
        List<InventoryItem> inventoryItems = Arrays.asList(
                new InventoryItem(1L, Status.Available, 10, 100.0, 1L),
                new InventoryItem(2L, Status.Available, 0, 50.0,2L)
        );
        when(inventoryService.getAllInventoryItems()).thenReturn(inventoryItems);

        ResponseEntity<List<InventoryItem>> response = inventoryController.getAllInventoryItems();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, Objects.requireNonNull(response.getBody()).size());
        verify(inventoryService, times(1)).getAllInventoryItems();
    }

    @Test
    void getInventoryItem() {
        Long itemId = 1L;
        InventoryItem inventoryItem = new InventoryItem(itemId, Status.Available, 10, 100.0,1L);
        when(inventoryService.getInventoryItem(itemId)).thenReturn(inventoryItem);
        ResponseEntity<InventoryItem> response = inventoryController.getInventoryItem(itemId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(inventoryItem, response.getBody());
        verify(inventoryService, times(1)).getInventoryItem(itemId);
    }

    @Test
    void getInventoryItemNotFound() {
        Long itemId = 1L;
        when(inventoryService.getInventoryItem(itemId)).thenReturn(null);

        ResponseEntity<InventoryItem> response = inventoryController.getInventoryItem(itemId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(inventoryService, times(1)).getInventoryItem(itemId);
    }

    @Test
    void createInventoryItem() {
        InventoryItem inventoryItem = new InventoryItem(null, Status.Available, 10, 100.0, 1L);
        InventoryItem createdInventoryItem = new InventoryItem(1L, Status.Available, 10, 100.0, 1L);
        when(inventoryService.createInventoryItem(inventoryItem)).thenReturn(createdInventoryItem);

        ResponseEntity<InventoryItem> response = inventoryController.createInventoryItem(inventoryItem);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(createdInventoryItem, response.getBody());
        verify(inventoryService, times(1)).createInventoryItem(inventoryItem);
    }

    @Test
    void editInventoryItem() {
        Long itemId = 1L;
        InventoryItem existingItem = new InventoryItem(itemId, Status.Available, 10, 100.0, 1L);
        InventoryItem updatedItemDetails = new InventoryItem(null, null, 0, 150.0, null);

        when(inventoryService.getInventoryItem(itemId)).thenReturn(existingItem);

        doAnswer(invocation -> {
            InventoryItem item = invocation.getArgument(0);
            item.setQuantity(updatedItemDetails.getQuantity());
            item.setPrice(updatedItemDetails.getPrice());
            item.setStatus(updatedItemDetails.getQuantity() == 0 ? Status.Not_Available : Status.Available);
            return null;
        }).when(inventoryService).editInventoryItem(existingItem);

        ResponseEntity<InventoryItem> response = inventoryController.editInventoryItem(itemId, updatedItemDetails);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, existingItem.getQuantity());
        assertEquals(150.0, existingItem.getPrice());
        assertEquals(Status.Not_Available, existingItem.getStatus());
        verify(inventoryService, times(1)).getInventoryItem(itemId);
        verify(inventoryService, times(1)).editInventoryItem(existingItem);
    }

    @Test
    void deleteInventoryItem() {
        Long itemId = 1L;
        InventoryItem inventoryItem = new InventoryItem(itemId, Status.Available, 10, 100.0, 1L);

        when(inventoryService.getInventoryItem(itemId)).thenReturn(inventoryItem);
        doNothing().when(inventoryService).deleteInventoryItem(itemId);

        ResponseEntity<Void> response = inventoryController.deleteInventoryItem(itemId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(inventoryService, times(1)).getInventoryItem(itemId);
        verify(inventoryService, times(1)).deleteInventoryItem(itemId);
    }

    @Test
    void deleteInventoryItemNotFound() {
        Long itemId = 1L;
        when(inventoryService.getInventoryItem(itemId)).thenReturn(null);

        ResponseEntity<Void> response = inventoryController.deleteInventoryItem(itemId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(inventoryService, times(1)).getInventoryItem(itemId);
        verify(inventoryService, times(0)).deleteInventoryItem(itemId);
    }
}
