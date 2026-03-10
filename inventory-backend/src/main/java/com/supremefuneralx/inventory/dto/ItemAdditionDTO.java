package com.supremefuneralx.inventory.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    @NotNull(message = "Item ID is required")
    private Long itemId;

    private String itemName;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    @NotNull(message = "Supplier ID is required")
    private Long supplierId;

    private String supplierName;

    @Size(max = 500, message = "Notes must not exceed 500 characters")
    private String notes;

    private LocalDateTime createdAt;
}
