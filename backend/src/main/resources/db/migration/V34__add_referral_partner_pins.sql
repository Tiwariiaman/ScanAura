CREATE TABLE referral_partners (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(10) NOT NULL,
    pin VARCHAR(6) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_referral_partner_pin UNIQUE (pin),
    CONSTRAINT chk_referral_partner_pin_format CHECK (pin ~ '^SA[0-9]{4}$')
);

ALTER TABLE users ADD COLUMN IF NOT EXISTS registration_pin VARCHAR(6);
ALTER TABLE users ADD COLUMN IF NOT EXISTS referral_partner_id UUID;
ALTER TABLE users ADD COLUMN IF NOT EXISTS super_admin_pin_used BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE users ADD CONSTRAINT fk_users_referral_partner
    FOREIGN KEY (referral_partner_id) REFERENCES referral_partners(id);
CREATE INDEX idx_users_referral_partner_id ON users(referral_partner_id);
