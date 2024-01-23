package com.petros.bibernate.entity;

import com.petros.bibernate.annotation.Entity;
import com.petros.bibernate.annotation.Id;
import com.petros.bibernate.annotation.Table;
import lombok.Data;

@Data
@Entity
@Table("users")
public class User {
    @Id
    private Integer id;
    private String name;
    private String email;
}
