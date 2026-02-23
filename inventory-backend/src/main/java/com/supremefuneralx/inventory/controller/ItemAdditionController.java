package com.supremefuneralx.inventory.controller;

import com.supremefuneralx.inventory.dto.ItemAdditionDTO;
import com.supremefuneralx.inventory.service.ItemAdditionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/additions")
@RequiredArgsConstructor
@CrossOrigin(origins = { "http://localhost:8081", "http://localhost:3000", "http://localhost:8084" })
public class ItemAdditionController {

    private final ItemAdditionService additionService;

    @PostMapping
    public ResponseEntity<ItemAdditionDTO> addItem(@RequestBody ItemAdditionDTO additionDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(additionService.addItem(additionDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemAdditionDTO> getAdditionById(@PathVariable Long id) {
        return ResponseEntity.ok(additionService.getAdditionById(id));
    }

    @GetMapping
    public ResponseEntity<List<ItemAdditionDTO>> getAllAdditions() {
        return ResponseEntity.ok(additionService.getAllAdditions());
    }

    @GetMapping("/item/{itemId}")
    public ResponseEntity<List<ItemAdditionDTO>> getAdditionsByItem(@PathVariable Long itemId) {
        return ResponseEntity.ok(additionService.getAdditionsByItem(itemId));
    }

    @GetMapping("/supplier/{supplierId}")
    public ResponseEntity<List<ItemAdditionDTO>> getAdditionsBySupplier(@PathVariable Long supplierId) {
        return ResponseEntity.ok(additionService.getAdditionsBySupplier(supplierId));
    }

    @GetMapping("/recent")
    public ResponseEntity<List<ItemAdditionDTO>> getRecentAdditions() {
        return ResponseEntity.ok(additionService.getRecentAdditions());
    }

    @GetMapping("/range")
    public ResponseEntity<List<ItemAdditionDTO>> getAdditionsBetweenDates(
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate) {
        return ResponseEntity.ok(additionService.getAdditionsBetweenDates(startDate, endDate));
    }
}
