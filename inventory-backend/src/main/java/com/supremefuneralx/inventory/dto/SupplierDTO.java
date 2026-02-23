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
public class SupplierDTO {

    private Long id;
    private String name;
    private String contactPerson;
    private String phoneNumber;
    private String email;
    private String address;
    private String city;
    private String zipCode;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
