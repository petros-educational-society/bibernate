package com.petros.bibernate.entity;

import com.petros.bibernate.annotation.Entity;
import com.petros.bibernate.annotation.Id;
import com.petros.bibernate.annotation.OneToMany;
import com.petros.bibernate.annotation.Table;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table("addresses")
public class Address {
    @Id
    private Long id;
    private String city;
    private String street;

    @OneToMany
    private List<Order> orders = new ArrayList<>();
}
