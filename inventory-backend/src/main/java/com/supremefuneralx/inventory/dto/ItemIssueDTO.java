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
public class ItemIssueDTO {

    private Long id;
    private Long itemId;
    private String itemName;
    private Integer quantity;
    private String issuedTo;
    private String reason;
    private String notes;
    private LocalDateTime createdAt;
}
