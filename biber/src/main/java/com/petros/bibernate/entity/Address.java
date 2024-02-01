package com.petros.bibernate.entity;

import com.petros.bibernate.annotation.Entity;
import com.petros.bibernate.annotation.Id;
import com.petros.bibernate.annotation.OneToMany;
import com.petros.bibernate.annotation.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table("addresses")
@ToString(exclude = "orders")
@NoArgsConstructor
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

    public void addOrder(Order order) {
        orders.add(order);
        order.setAddress(this);
    }

    public void removeOrder(Order order) {
        orders.remove(order);
        order.setAddress(null);
    }
}
