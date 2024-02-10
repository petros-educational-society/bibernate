package com.petros.bibernate.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.petros.bibernate.annotation.Column;
import com.petros.bibernate.annotation.Entity;
import com.petros.bibernate.annotation.Id;
import com.petros.bibernate.annotation.ManyToMany;
import com.petros.bibernate.annotation.Table;
import lombok.Data;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table("users")
@ToString(exclude = "buyers")
public class User {
    @Id
    private Long id;
    @Column
    private String name;
    @Column
    private String email;

    @JsonIgnore
    @ManyToMany(mappedBy = "buyers")
    private Set<Buyer> buyers = new HashSet<>();
}
