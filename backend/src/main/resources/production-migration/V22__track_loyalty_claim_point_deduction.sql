
ALTER TABLE activity_loyalty_claims
    ADD COLUMN IF NOT EXISTS points_deducted BOOLEAN;

UPDATE activity_loyalty_claims
SET points_deducted = TRUE
WHERE points_deducted IS NULL;

ALTER TABLE activity_loyalty_claims
    ALTER COLUMN points_deducted SET DEFAULT TRUE;

ALTER TABLE activity_loyalty_claims
    ALTER COLUMN points_deducted SET NOT NULL;
