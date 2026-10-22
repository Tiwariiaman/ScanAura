package com.scanaura.activity.scratch.entity;

import com.scanaura.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "activity_scratch_rewards")
@Getter
@Setter
@NoArgsConstructor
public class ScratchReward extends BaseEntity {

    @Column(name = "card_id", nullable = false)
    private UUID cardId;

    @Column(name = "business_id", nullable = false)
    private UUID businessId;

    @Column(name = "reward_name", nullable = false, length = 150)
    private String rewardName;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder = 0;
}
