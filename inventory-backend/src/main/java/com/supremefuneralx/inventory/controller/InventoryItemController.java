package com.supremefuneralx.inventory.controller;

import com.supremefuneralx.inventory.dto.InventoryItemDTO;
import com.supremefuneralx.inventory.service.InventoryItemService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * REST Controller for Inventory Item management.
 * All endpoints require proper input validation.
 * CORS is configured globally in SecurityConfig.
 */
@RestController
@RequestMapping("/api/v1/items")
@RequiredArgsConstructor
public class InventoryItemController {

    private final InventoryItemService itemService;

    @PostMapping
    public ResponseEntity<InventoryItemDTO> createItem(@Valid @RequestBody InventoryItemDTO itemDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(itemService.createItem(itemDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventoryItemDTO> getItemById(@PathVariable Long id) {
        return ResponseEntity.ok(itemService.getItemById(id));
    }

    @GetMapping
    public ResponseEntity<List<InventoryItemDTO>> getAllItems() {
        return ResponseEntity.ok(itemService.getAllItems());
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<InventoryItemDTO>> getItemsByCategory(
            @PathVariable @NotBlank(message = "Category cannot be blank") String category) {
        return ResponseEntity.ok(itemService.getItemsByCategory(category));
    }

    @GetMapping("/search")
    public ResponseEntity<List<InventoryItemDTO>> searchItems(
            @RequestParam @NotBlank(message = "Search term cannot be blank") String searchTerm) {
        return ResponseEntity.ok(itemService.searchItems(searchTerm));
    }

    @GetMapping("/search/category")
    public ResponseEntity<List<InventoryItemDTO>> searchItemsByCategory(
            @RequestParam @NotBlank(message = "Category cannot be blank") String category,
            @RequestParam @NotBlank(message = "Search term cannot be blank") String searchTerm) {
        return ResponseEntity.ok(itemService.searchItemsByCategory(category, searchTerm));
    }

    @GetMapping("/low-stock")
    public ResponseEntity<List<InventoryItemDTO>> getLowStockItems() {
        return ResponseEntity.ok(itemService.getLowStockItems());
    }

    @PutMapping("/{id}")
    public ResponseEntity<InventoryItemDTO> updateItem(@PathVariable Long id,
            @Valid @RequestBody InventoryItemDTO itemDTO) {
        return ResponseEntity.ok(itemService.updateItem(id, itemDTO));
    }

    @PatchMapping("/{id}/quantity")
    public ResponseEntity<Void> adjustQuantity(@PathVariable Long id, @RequestParam Integer adjustment) {
        itemService.adjustQuantity(id, adjustment);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        itemService.deleteItem(id);
        return ResponseEntity.noContent().build();
    }
}
