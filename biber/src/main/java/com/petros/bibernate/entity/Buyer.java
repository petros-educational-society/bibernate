package com.petros.bibernate.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.petros.bibernate.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table("buyers")
@ToString(exclude = "users")
@EqualsAndHashCode(exclude = "users")
public class Buyer {
    @Id
    private Long id;
    private String name;
    private String phone;

    @JsonIgnore
    @ManyToMany
    @JoinTable(value = "buyers_users", joinColumns = @JoinColumn(value = "buyer_id"),
            inverseJoinColumns = @JoinColumn(value = "user_id"))
    private Set<User> users = new HashSet<>();
}
