CREATE TABLE activity_scratch_cards (
    id UUID PRIMARY KEY,
    business_id UUID NOT NULL UNIQUE,
    card_name VARCHAR(150) NOT NULL DEFAULT 'Scratch & Win',
    enabled BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE activity_scratch_rewards (
    id UUID PRIMARY KEY,
    card_id UUID NOT NULL REFERENCES activity_scratch_cards(id) ON DELETE CASCADE,
    business_id UUID NOT NULL,
    reward_name VARCHAR(150) NOT NULL,
    display_order INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_scratch_rewards_card ON activity_scratch_rewards(card_id);

CREATE TABLE activity_scratch_plays (
    id UUID PRIMARY KEY,
    card_id UUID NOT NULL REFERENCES activity_scratch_cards(id),
    business_id UUID NOT NULL,
    reward_id UUID,
    customer_name VARCHAR(120) NOT NULL,
    mobile_number VARCHAR(20) NOT NULL,
    reward_name_snapshot VARCHAR(150) NOT NULL,
    coupon_code VARCHAR(40) NOT NULL UNIQUE,
    coupon_token UUID NOT NULL UNIQUE,
    status VARCHAR(20) NOT NULL DEFAULT 'WON',
    played_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    redeemed_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_scratch_play_status CHECK (status IN ('WON', 'REDEEMED'))
);
CREATE INDEX idx_scratch_plays_business_played ON activity_scratch_plays(business_id, played_at DESC);
CREATE INDEX idx_scratch_plays_business_status ON activity_scratch_plays(business_id, status);
