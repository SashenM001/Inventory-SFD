package com.supremefuneralx.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryItemDTO {

    private Long id;
    private String name;
    private String sku;
    private String category;
    private Integer quantity;
    private Integer minQuantity;
    private BigDecimal price;
    private Long supplierId;
    private String supplierName;
    private String description;
    private String unit;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
