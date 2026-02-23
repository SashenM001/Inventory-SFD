package com.supremefuneralx.inventory.controller;

import com.supremefuneralx.inventory.dto.InventoryItemDTO;
import com.supremefuneralx.inventory.service.InventoryItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/items")
@RequiredArgsConstructor
@CrossOrigin(origins = { "http://localhost:8080", "http://localhost:8081", "http://localhost:3000",
        "http://localhost:8084", "https://inventory-sfd.vercel.app" })
public class InventoryItemController {

    private final InventoryItemService itemService;

    @PostMapping
    public ResponseEntity<InventoryItemDTO> createItem(@RequestBody InventoryItemDTO itemDTO) {
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
    public ResponseEntity<List<InventoryItemDTO>> getItemsByCategory(@PathVariable String category) {
        return ResponseEntity.ok(itemService.getItemsByCategory(category));
    }

    @GetMapping("/search")
    public ResponseEntity<List<InventoryItemDTO>> searchItems(@RequestParam String searchTerm) {
        return ResponseEntity.ok(itemService.searchItems(searchTerm));
    }

    @GetMapping("/search/category")
    public ResponseEntity<List<InventoryItemDTO>> searchItemsByCategory(
            @RequestParam String category,
            @RequestParam String searchTerm) {
        return ResponseEntity.ok(itemService.searchItemsByCategory(category, searchTerm));
    }

    @GetMapping("/low-stock")
    public ResponseEntity<List<InventoryItemDTO>> getLowStockItems() {
        return ResponseEntity.ok(itemService.getLowStockItems());
    }

    @PutMapping("/{id}")
    public ResponseEntity<InventoryItemDTO> updateItem(@PathVariable Long id, @RequestBody InventoryItemDTO itemDTO) {
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
