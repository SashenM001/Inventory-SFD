package com.supremefuneralx.inventory.service;

import com.supremefuneralx.inventory.dto.ItemAdditionDTO;
import com.supremefuneralx.inventory.entity.ItemAddition;
import com.supremefuneralx.inventory.entity.InventoryItem;
import com.supremefuneralx.inventory.entity.Supplier;
import com.supremefuneralx.inventory.repository.ItemAdditionRepository;
import com.supremefuneralx.inventory.repository.InventoryItemRepository;
import com.supremefuneralx.inventory.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemAdditionService {

    private final ItemAdditionRepository additionRepository;
    private final InventoryItemRepository itemRepository;
    private final SupplierRepository supplierRepository;

    public ItemAdditionDTO addItem(ItemAdditionDTO additionDTO) {
        InventoryItem item = itemRepository.findById(additionDTO.getItemId())
                .orElseThrow(() -> new RuntimeException("Item not found with id: " + additionDTO.getItemId()));

        Supplier supplier = null;
        if (additionDTO.getSupplierId() != null) {
            supplier = supplierRepository.findById(additionDTO.getSupplierId())
                    .orElseThrow(
                            () -> new RuntimeException("Supplier not found with id: " + additionDTO.getSupplierId()));
        }

        ItemAddition addition = ItemAddition.builder()
                .item(item)
                .quantity(additionDTO.getQuantity())
                .supplier(supplier)
                .notes(additionDTO.getNotes())
                .build();

        // Increase the quantity
        item.setQuantity(item.getQuantity() + additionDTO.getQuantity());
        itemRepository.save(item);

        ItemAddition savedAddition = additionRepository.save(addition);
        return mapToDTO(savedAddition);
    }

    @Transactional(readOnly = true)
    public ItemAdditionDTO getAdditionById(Long id) {
        ItemAddition addition = additionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Addition not found with id: " + id));
        return mapToDTO(addition);
    }

    @Transactional(readOnly = true)
    public List<ItemAdditionDTO> getAllAdditions() {
        return additionRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ItemAdditionDTO> getAdditionsByItem(Long itemId) {
        return additionRepository.findByItemId(itemId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ItemAdditionDTO> getAdditionsBySupplier(Long supplierId) {
        return additionRepository.findBySupplierId(supplierId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ItemAdditionDTO> getAdditionsBetweenDates(LocalDateTime startDate, LocalDateTime endDate) {
        return additionRepository.findAdditionsBetweenDates(startDate, endDate).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ItemAdditionDTO> getRecentAdditions() {
        return additionRepository.findRecentAdditions().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private ItemAdditionDTO mapToDTO(ItemAddition addition) {
        return ItemAdditionDTO.builder()
                .id(addition.getId())
                .itemId(addition.getItem().getId())
                .itemName(addition.getItem().getName())
                .quantity(addition.getQuantity())
                .supplierId(addition.getSupplier() != null ? addition.getSupplier().getId() : null)
                .supplierName(addition.getSupplier() != null ? addition.getSupplier().getName() : null)
                .notes(addition.getNotes())
                .createdAt(addition.getCreatedAt())
                .build();
    }
}
