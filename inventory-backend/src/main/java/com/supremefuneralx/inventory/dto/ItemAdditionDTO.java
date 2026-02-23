package com.supremefuneralx.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemAdditionDTO {

    private Long id;
    private Long itemId;
    private String itemName;
    private Integer quantity;
    private Long supplierId;
    private String supplierName;
    private String notes;
    private LocalDateTime createdAt;
}
