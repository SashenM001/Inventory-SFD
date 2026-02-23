package com.supremefuneralx.inventory.controller;

import com.supremefuneralx.inventory.dto.SupplierDTO;
import com.supremefuneralx.inventory.service.SupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/suppliers")
@RequiredArgsConstructor
@CrossOrigin(origins = { "http://localhost:8080", "http://localhost:8081", "http://localhost:3000",
        "http://localhost:8084", "https://inventory-sfd.vercel.app" })
public class SupplierController {

    private final SupplierService supplierService;

    @PostMapping
    public ResponseEntity<SupplierDTO> createSupplier(@RequestBody SupplierDTO supplierDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(supplierService.createSupplier(supplierDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SupplierDTO> getSupplierById(@PathVariable Long id) {
        return ResponseEntity.ok(supplierService.getSupplierById(id));
    }

    @GetMapping
    public ResponseEntity<List<SupplierDTO>> getAllSuppliers() {
        return ResponseEntity.ok(supplierService.getAllSuppliers());
    }

    @GetMapping("/search")
    public ResponseEntity<List<SupplierDTO>> searchSuppliers(@RequestParam String searchTerm) {
        return ResponseEntity.ok(supplierService.searchSuppliers(searchTerm));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SupplierDTO> updateSupplier(@PathVariable Long id, @RequestBody SupplierDTO supplierDTO) {
        return ResponseEntity.ok(supplierService.updateSupplier(id, supplierDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSupplier(@PathVariable Long id) {
        supplierService.deleteSupplier(id);
        return ResponseEntity.noContent().build();
    }
}
