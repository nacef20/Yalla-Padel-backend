package com.shop.productservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderAdress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String firstName;

    private String lastName;

    private String email;

    @Column(length = 8)
    @Pattern(regexp = "\\d{8}", message = "Telephone must be exactly 8 digits")
    private String telephone;

    private String address;

    private String city;

    private String state;

    @Column(length = 4)
    @Pattern(regexp = "\\d{4}", message = "Postal code must be exactly 4 digits")
    private String postalCode;

}
