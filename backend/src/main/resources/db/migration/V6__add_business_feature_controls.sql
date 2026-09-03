-- ============================================================
-- ScanAura V6
-- Add business feature controls
-- ============================================================

ALTER TABLE businesses
    ADD COLUMN google_review_url VARCHAR(500),
    ADD COLUMN google_review_enabled BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN payment_enabled BOOLEAN NOT NULL DEFAULT TRUE;