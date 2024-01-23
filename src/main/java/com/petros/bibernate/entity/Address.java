package com.petros.bibernate.entity;

import com.petros.bibernate.annotation.Entity;
import com.petros.bibernate.annotation.Id;

@Entity
public class Address {
    @Id
    private Integer id;
    private String city;
    private String street;
}
