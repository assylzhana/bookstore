package com.microservices.inventory_service.repository;

import com.microservices.inventory_service.model.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryRepository extends JpaRepository<InventoryItem,Long> {
    List<InventoryItem> findByBookId(Long id);
}
