package com.petros.bibernate.entity;

import com.petros.bibernate.annotation.Entity;
import com.petros.bibernate.annotation.Id;
import com.petros.bibernate.annotation.Table;
import lombok.Data;

@Data
@Entity
@Table("addresses")
public class Address {
    @Id
    private Integer id;
    private String city;
    private String street;
}
