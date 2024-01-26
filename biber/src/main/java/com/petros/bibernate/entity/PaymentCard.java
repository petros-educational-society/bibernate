package com.petros.bibernate.entity;

import com.petros.bibernate.annotation.*;
import lombok.Data;

@Data
@Entity
@Table("cards")
public class PaymentCard {

    @Id
    private Long id;
    private String number;
    private String type;

    @OneToOne
    @JoinColumn(value = "user_id")
    private User user;
}
