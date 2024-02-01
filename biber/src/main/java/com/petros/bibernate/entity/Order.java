package com.petros.bibernate.entity;

import com.petros.bibernate.annotation.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Entity
@Table("orders")
@NoArgsConstructor
public class Order {

    @Id
    private Long id;
    private String name;
    private BigDecimal price;

    @ManyToOne
    @JoinColumn(value = "address_id")
    private Address address;

    public Order(Long id, String name, BigDecimal price, Address address) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.address = address;
    }
}
