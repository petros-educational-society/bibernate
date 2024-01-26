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
@Table("users")
public class UserWithPayments {
    @Id
    private Long id;
    private String name;
    private String email;

    @OneToMany
    private List<Payment> payments = new ArrayList<>();
}
