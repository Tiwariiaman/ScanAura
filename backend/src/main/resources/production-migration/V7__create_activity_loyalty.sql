-- ============================================================
-- ScanAura Activity - Loyalty System
-- V11
-- ============================================================


-- ============================================================
-- 1. BUSINESS LOYALTY SETTINGS
-- ============================================================

CREATE TABLE activity_loyalty_settings (

                                           id UUID PRIMARY KEY,

                                           business_id UUID NOT NULL,

                                           enabled BOOLEAN NOT NULL DEFAULT FALSE,

                                           points_per_visit INTEGER NOT NULL DEFAULT 10,

                                           created_at TIMESTAMP NOT NULL,

                                           updated_at TIMESTAMP NOT NULL,

                                           CONSTRAINT uq_activity_loyalty_settings_business
                                               UNIQUE (business_id),

                                           CONSTRAINT fk_activity_loyalty_settings_business
                                               FOREIGN KEY (business_id)
                                                   REFERENCES businesses(id)
                                                   ON DELETE CASCADE,

                                           CONSTRAINT chk_activity_loyalty_points_per_visit
                                               CHECK (points_per_visit > 0)

);


CREATE INDEX idx_activity_loyalty_settings_business_id
    ON activity_loyalty_settings (business_id);


-- ============================================================
-- 2. LOYALTY REWARDS
-- ============================================================

CREATE TABLE activity_loyalty_rewards (

                                          id UUID PRIMARY KEY,

                                          business_id UUID NOT NULL,

                                          title VARCHAR(150) NOT NULL,

                                          points_required INTEGER NOT NULL,

                                          reward_amount NUMERIC(10,2) NOT NULL,

                                          active BOOLEAN NOT NULL DEFAULT TRUE,

                                          created_at TIMESTAMP NOT NULL,

                                          updated_at TIMESTAMP NOT NULL,

                                          CONSTRAINT fk_activity_loyalty_rewards_business
                                              FOREIGN KEY (business_id)
                                                  REFERENCES businesses(id)
                                                  ON DELETE CASCADE,

                                          CONSTRAINT chk_activity_loyalty_reward_points
                                              CHECK (points_required > 0),

                                          CONSTRAINT chk_activity_loyalty_reward_amount
                                              CHECK (reward_amount >= 0)

);


CREATE INDEX idx_activity_loyalty_rewards_business_id
    ON activity_loyalty_rewards (business_id);

CREATE INDEX idx_activity_loyalty_rewards_active
    ON activity_loyalty_rewards (business_id, active);


-- ============================================================
-- 3. LOYALTY CUSTOMERS
-- ============================================================

CREATE TABLE activity_loyalty_customers (

                                            id UUID PRIMARY KEY,

                                            business_id UUID NOT NULL,

                                            mobile_number VARCHAR(20) NOT NULL,

                                            customer_name VARCHAR(120) NOT NULL,

                                            points_balance INTEGER NOT NULL DEFAULT 0,

                                            total_points_earned INTEGER NOT NULL DEFAULT 0,

                                            total_points_redeemed INTEGER NOT NULL DEFAULT 0,

                                            created_at TIMESTAMP NOT NULL,

                                            updated_at TIMESTAMP NOT NULL,

                                            CONSTRAINT fk_activity_loyalty_customers_business
                                                FOREIGN KEY (business_id)
                                                    REFERENCES businesses(id)
                                                    ON DELETE CASCADE,

                                            CONSTRAINT uq_activity_loyalty_customer_business_mobile
                                                UNIQUE (business_id, mobile_number),

                                            CONSTRAINT chk_activity_loyalty_customer_balance
                                                CHECK (points_balance >= 0),

                                            CONSTRAINT chk_activity_loyalty_customer_earned
                                                CHECK (total_points_earned >= 0),

                                            CONSTRAINT chk_activity_loyalty_customer_redeemed
                                                CHECK (total_points_redeemed >= 0)

);


CREATE INDEX idx_activity_loyalty_customers_business_id
    ON activity_loyalty_customers (business_id);

CREATE INDEX idx_activity_loyalty_customers_mobile
    ON activity_loyalty_customers (mobile_number);


-- ============================================================
-- 4. LOYALTY POINT TRANSACTIONS
-- ============================================================

CREATE TABLE activity_loyalty_transactions (

                                               id UUID PRIMARY KEY,

                                               business_id UUID NOT NULL,

                                               customer_id UUID NOT NULL,

                                               transaction_type VARCHAR(20) NOT NULL,

                                               points INTEGER NOT NULL,

                                               description VARCHAR(255),

                                               created_at TIMESTAMP NOT NULL,

                                               updated_at TIMESTAMP NOT NULL,

                                               CONSTRAINT fk_activity_loyalty_transactions_business
                                                   FOREIGN KEY (business_id)
                                                       REFERENCES businesses(id)
                                                       ON DELETE CASCADE,

                                               CONSTRAINT fk_activity_loyalty_transactions_customer
                                                   FOREIGN KEY (customer_id)
                                                       REFERENCES activity_loyalty_customers(id)
                                                       ON DELETE CASCADE,

                                               CONSTRAINT chk_activity_loyalty_transaction_type
                                                   CHECK (
                                                       transaction_type IN (
                                                                            'EARN',
                                                                            'REDEEM',
                                                                            'ADJUSTMENT'
                                                           )
                                                       ),

                                               CONSTRAINT chk_activity_loyalty_transaction_points
                                                   CHECK (points > 0)

);


CREATE INDEX idx_activity_loyalty_transactions_business_id
    ON activity_loyalty_transactions (business_id);

CREATE INDEX idx_activity_loyalty_transactions_customer_id
    ON activity_loyalty_transactions (customer_id);

CREATE INDEX idx_activity_loyalty_transactions_created_at
    ON activity_loyalty_transactions (created_at);


-- ============================================================
-- 5. LOYALTY REWARD CLAIMS
-- ============================================================

CREATE TABLE activity_loyalty_claims (

                                         id UUID PRIMARY KEY,

                                         business_id UUID NOT NULL,

                                         customer_id UUID NOT NULL,

                                         reward_id UUID NOT NULL,

                                         points_used INTEGER NOT NULL,

                                         reward_amount NUMERIC(10,2) NOT NULL,

                                         qr_token UUID NOT NULL,

                                         status VARCHAR(20) NOT NULL DEFAULT 'PENDING',

                                         granted_at TIMESTAMP,

                                         created_at TIMESTAMP NOT NULL,

                                         updated_at TIMESTAMP NOT NULL,

                                         CONSTRAINT fk_activity_loyalty_claims_business
                                             FOREIGN KEY (business_id)
                                                 REFERENCES businesses(id)
                                                 ON DELETE CASCADE,

                                         CONSTRAINT fk_activity_loyalty_claims_customer
                                             FOREIGN KEY (customer_id)
                                                 REFERENCES activity_loyalty_customers(id)
                                                 ON DELETE CASCADE,

                                         CONSTRAINT fk_activity_loyalty_claims_reward
                                             FOREIGN KEY (reward_id)
                                                 REFERENCES activity_loyalty_rewards(id)
                                                 ON DELETE RESTRICT,

                                         CONSTRAINT uq_activity_loyalty_claim_qr_token
                                             UNIQUE (qr_token),

                                         CONSTRAINT chk_activity_loyalty_claim_points
                                             CHECK (points_used > 0),

                                         CONSTRAINT chk_activity_loyalty_claim_amount
                                             CHECK (reward_amount >= 0),

                                         CONSTRAINT chk_activity_loyalty_claim_status
                                             CHECK (
                                                 status IN (
                                                            'PENDING',
                                                            'GRANTED',
                                                            'CANCELLED'
                                                     )
                                                 )

);


CREATE INDEX idx_activity_loyalty_claims_business_id
    ON activity_loyalty_claims (business_id);

CREATE INDEX idx_activity_loyalty_claims_customer_id
    ON activity_loyalty_claims (customer_id);

CREATE INDEX idx_activity_loyalty_claims_reward_id
    ON activity_loyalty_claims (reward_id);

CREATE INDEX idx_activity_loyalty_claims_status
    ON activity_loyalty_claims (status);

CREATE INDEX idx_activity_loyalty_claims_qr_token
    ON activity_loyalty_claims (qr_token);