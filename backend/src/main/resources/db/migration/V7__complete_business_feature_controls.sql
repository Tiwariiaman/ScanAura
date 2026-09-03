-- ============================================================
-- ScanAura V7
-- Complete business feature controls
-- ============================================================

ALTER TABLE businesses
    ADD COLUMN IF NOT EXISTS google_review_enabled BOOLEAN;

ALTER TABLE businesses
    ADD COLUMN IF NOT EXISTS payment_enabled BOOLEAN;

UPDATE businesses
SET google_review_enabled = FALSE
WHERE google_review_enabled IS NULL;

UPDATE businesses
SET payment_enabled = TRUE
WHERE payment_enabled IS NULL;

ALTER TABLE businesses
    ALTER COLUMN google_review_enabled SET DEFAULT FALSE;

ALTER TABLE businesses
    ALTER COLUMN payment_enabled SET DEFAULT TRUE;

ALTER TABLE businesses
    ALTER COLUMN google_review_enabled SET NOT NULL;

ALTER TABLE businesses
    ALTER COLUMN payment_enabled SET NOT NULL;