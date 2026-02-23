package com.supremefuneralx.inventory.repository;

import com.supremefuneralx.inventory.entity.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {

    Optional<InventoryItem> findBySku(String sku);

    List<InventoryItem> findByCategory(String category);

    List<InventoryItem> findBySupplierId(Long supplierId);

    @Query("SELECT i FROM InventoryItem i WHERE i.name LIKE %:searchTerm% OR i.sku LIKE %:searchTerm%")
    List<InventoryItem> searchItems(@Param("searchTerm") String searchTerm);

    @Query("SELECT i FROM InventoryItem i WHERE i.category = :category AND (i.name LIKE %:searchTerm% OR i.sku LIKE %:searchTerm%)")
    List<InventoryItem> searchItemsByCategory(@Param("category") String category,
            @Param("searchTerm") String searchTerm);

    @Query("SELECT i FROM InventoryItem i WHERE i.quantity <= i.minQuantity")
    List<InventoryItem> findLowStockItems();
}
