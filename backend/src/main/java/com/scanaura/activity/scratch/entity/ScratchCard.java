package com.scanaura.activity.scratch.entity;

import com.scanaura.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "activity_scratch_cards", uniqueConstraints = @UniqueConstraint(
        name = "uq_activity_scratch_card_business", columnNames = "business_id"))
@Getter
@Setter
@NoArgsConstructor
public class ScratchCard extends BaseEntity {

    @Column(name = "business_id", nullable = false, unique = true)
    private UUID businessId;

    @Column(name = "card_name", nullable = false, length = 150)
    private String cardName = "Scratch & Win";

    @Column(nullable = false)
    private boolean enabled = false;
}
