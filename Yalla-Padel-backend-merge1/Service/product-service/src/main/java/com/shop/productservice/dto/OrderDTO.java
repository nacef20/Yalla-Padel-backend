package com.shop.productservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import jakarta.validation.constraints.Pattern;

@ToString
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDTO {
    private String firstName;
    private String lastName;
    private String email;

    @Pattern(regexp = "\\d{8}", message = "Telephone must be exactly 8 digits")
    private String telephone;

    private String address;
    private String city;
    private String state;

    @Pattern(regexp = "\\d{4}", message = "Postal code must be exactly 4 digits")
    private String postalCode;

    private String paymentType;
}
