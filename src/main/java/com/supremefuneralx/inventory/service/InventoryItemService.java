package com.supremefuneralx.inventory.service;

import com.supremefuneralx.inventory.dto.InventoryItemDTO;
import com.supremefuneralx.inventory.entity.InventoryItem;
import com.supremefuneralx.inventory.entity.Supplier;
import com.supremefuneralx.inventory.repository.InventoryItemRepository;
import com.supremefuneralx.inventory.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class InventoryItemService {

    private final InventoryItemRepository itemRepository;
    private final SupplierRepository supplierRepository;

    public InventoryItemDTO createItem(InventoryItemDTO itemDTO) {
        Supplier supplier = supplierRepository.findById(itemDTO.getSupplierId())
                .orElseThrow(() -> new RuntimeException("Supplier not found with id: " + itemDTO.getSupplierId()));

        InventoryItem item = InventoryItem.builder()
                .name(itemDTO.getName())
                .sku(itemDTO.getSku())
                .category(itemDTO.getCategory())
                .quantity(itemDTO.getQuantity())
                .minQuantity(itemDTO.getMinQuantity())
                .price(itemDTO.getPrice())
                .supplier(supplier)
                .description(itemDTO.getDescription())
                .unit(itemDTO.getUnit())
                .build();

        InventoryItem savedItem = itemRepository.save(item);
        return mapToDTO(savedItem);
    }

    @Transactional(readOnly = true)
    public InventoryItemDTO getItemById(Long id) {
        InventoryItem item = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found with id: " + id));
        return mapToDTO(item);
    }

    @Transactional(readOnly = true)
    public List<InventoryItemDTO> getAllItems() {
        return itemRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<InventoryItemDTO> getItemsByCategory(String category) {
        return itemRepository.findByCategory(category).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<InventoryItemDTO> searchItems(String searchTerm) {
        return itemRepository.searchItems(searchTerm).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<InventoryItemDTO> searchItemsByCategory(String category, String searchTerm) {
        return itemRepository.searchItemsByCategory(category, searchTerm).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<InventoryItemDTO> getLowStockItems() {
        return itemRepository.findLowStockItems().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public InventoryItemDTO updateItem(Long id, InventoryItemDTO itemDTO) {
        InventoryItem item = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found with id: " + id));

        if (itemDTO.getSupplierId() != null && !itemDTO.getSupplierId().equals(item.getSupplier().getId())) {
            Supplier supplier = supplierRepository.findById(itemDTO.getSupplierId())
                    .orElseThrow(() -> new RuntimeException("Supplier not found with id: " + itemDTO.getSupplierId()));
            item.setSupplier(supplier);
        }

        item.setName(itemDTO.getName());
        item.setCategory(itemDTO.getCategory());
        item.setQuantity(itemDTO.getQuantity());
        item.setMinQuantity(itemDTO.getMinQuantity());
        item.setPrice(itemDTO.getPrice());
        item.setDescription(itemDTO.getDescription());
        item.setUnit(itemDTO.getUnit());

        InventoryItem updatedItem = itemRepository.save(item);
        return mapToDTO(updatedItem);
    }

    public void adjustQuantity(Long id, Integer quantityAdjustment) {
        InventoryItem item = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found with id: " + id));

        item.setQuantity(item.getQuantity() + quantityAdjustment);
        itemRepository.save(item);
    }

    public void deleteItem(Long id) {
        if (!itemRepository.existsById(id)) {
            throw new RuntimeException("Item not found with id: " + id);
        }
        itemRepository.deleteById(id);
    }

    private InventoryItemDTO mapToDTO(InventoryItem item) {
        return InventoryItemDTO.builder()
                .id(item.getId())
                .name(item.getName())
                .sku(item.getSku())
                .category(item.getCategory())
                .quantity(item.getQuantity())
                .minQuantity(item.getMinQuantity())
                .price(item.getPrice())
                .supplierId(item.getSupplier().getId())
                .supplierName(item.getSupplier().getName())
                .description(item.getDescription())
                .unit(item.getUnit())
                .createdAt(item.getCreatedAt())
                .updatedAt(item.getUpdatedAt())
                .build();
    }
}
