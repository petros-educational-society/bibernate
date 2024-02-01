package com.petros.bibernate.entity;


import com.petros.bibernate.annotation.Entity;
import com.petros.bibernate.annotation.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
public class Payment {

    @Id
    private Long id;
}
