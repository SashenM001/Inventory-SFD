package com.supremefuneralx.inventory.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
public class SupplierDTO {

    private Long id;

    @NotBlank(message = "Supplier name is required")
    @Size(min = 2, max = 255, message = "Supplier name must be between 2 and 255 characters")
    private String name;

    @Size(max = 100, message = "Contact person must not exceed 100 characters")
    private String contactPerson;

    @Pattern(regexp = "^[+]?[0-9]{7,15}$|^$", message = "Phone number must be valid (7-15 digits, optional + prefix)")
    private String phoneNumber;

    @Email(message = "Email must be valid")
    private String email;

    @Size(max = 255, message = "Address must not exceed 255 characters")
    private String address;

    @Size(max = 100, message = "City must not exceed 100 characters")
    private String city;

    @Pattern(regexp = "^[0-9]{5}$|^$", message = "Zip code must be 5 digits")
    private String zipCode;

    @Size(max = 500, message = "Notes must not exceed 500 characters")
    private String notes;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
