package com.petros.bibernate.entity;

import com.petros.bibernate.annotation.Entity;
import com.petros.bibernate.annotation.Id;
import com.petros.bibernate.annotation.JoinColumn;
import com.petros.bibernate.annotation.OneToOne;
import com.petros.bibernate.annotation.Table;
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
