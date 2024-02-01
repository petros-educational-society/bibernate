package com.petros.bibernate.entity;

import com.petros.bibernate.annotation.Entity;
import com.petros.bibernate.annotation.Id;
import com.petros.bibernate.annotation.OneToMany;
import com.petros.bibernate.annotation.Table;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table("addresses")
@ToString(exclude = "orders")
public class Address {
    @Id
    private Long id;
    private String city;
    private String street;

    @OneToMany
    private List<Order> orders = new ArrayList<>();

    public Address(Long id, String city, String street) {
        this.id = id;
        this.city = city;
        this.street = street;
    }
}
