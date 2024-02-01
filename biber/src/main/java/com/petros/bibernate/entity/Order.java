package com.petros.bibernate.entity;

import com.petros.bibernate.annotation.*;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table("orders")
public class Order {

    @Id
    private Long id;
    private String name;
    private BigDecimal price;

    @ManyToOne
    @JoinColumn(value = "address_id")
    private Address address;

    public Order(String name, BigDecimal price, Address address) {
        this.name = name;
        this.price = price;
        this.address = address;
    }
}
