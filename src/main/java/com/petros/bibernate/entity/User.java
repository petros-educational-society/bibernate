package com.petros.bibernate.entity;

import com.petros.bibernate.annotation.Entity;
import com.petros.bibernate.annotation.Id;

@Entity
public class User {
    @Id
    private Integer id;
    private String name;
    private String email;
}
