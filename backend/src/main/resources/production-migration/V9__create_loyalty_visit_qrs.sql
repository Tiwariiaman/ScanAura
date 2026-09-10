-- ============================================================
-- ScanAura Activity - Loyalty Visit QR
-- V14
-- ============================================================


-- ============================================================
-- 1. TEMPORARY CUSTOMER VISIT QR TOKENS
-- ============================================================

CREATE TABLE activity_loyalty_visit_qrs (

                                            id UUID PRIMARY KEY,

                                            business_id UUID NOT NULL,

                                            customer_id UUID NOT NULL,

                                            qr_token UUID NOT NULL,

                                            expires_at TIMESTAMP NOT NULL,

                                            used_at TIMESTAMP,

                                            created_at TIMESTAMP NOT NULL,

                                            updated_at TIMESTAMP NOT NULL,

                                            CONSTRAINT fk_activity_loyalty_visit_qrs_business
                                                FOREIGN KEY (business_id)
                                                    REFERENCES businesses(id)
                                                    ON DELETE CASCADE,

                                            CONSTRAINT fk_activity_loyalty_visit_qrs_customer
                                                FOREIGN KEY (customer_id)
                                                    REFERENCES activity_loyalty_customers(id)
                                                    ON DELETE CASCADE,

                                            CONSTRAINT uq_activity_loyalty_visit_qr_token
                                                UNIQUE (qr_token)

);


-- ============================================================
-- 2. INDEXES
-- ============================================================

CREATE INDEX idx_activity_loyalty_visit_qrs_business_id
    ON activity_loyalty_visit_qrs (business_id);

CREATE INDEX idx_activity_loyalty_visit_qrs_customer_id
    ON activity_loyalty_visit_qrs (customer_id);

CREATE INDEX idx_activity_loyalty_visit_qrs_expires_at
    ON activity_loyalty_visit_qrs (expires_at);

CREATE INDEX idx_activity_loyalty_visit_qrs_used_at
    ON activity_loyalty_visit_qrs (used_at);