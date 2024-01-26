package com.petros.bibernate.entity;


import com.petros.bibernate.annotation.Entity;
import com.petros.bibernate.annotation.Id;
import lombok.Data;

@Data
@Entity
public class Payment {

    @Id
    private Long id;
}
