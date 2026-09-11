-- ============================================================
-- ScanAura Loyalty
-- Prevent duplicate daily EARN transactions per business/customer
-- Daily boundary is calculated using Asia/Kolkata.
-- ============================================================

ALTER TABLE activity_loyalty_transactions
    ADD COLUMN IF NOT EXISTS earn_date_ist DATE;

-- Backfill existing transactions
UPDATE activity_loyalty_transactions
SET earn_date_ist = (created_at AT TIME ZONE 'Asia/Kolkata')::date
WHERE earn_date_ist IS NULL;

-- Keep the IST date synchronized for new/updated transactions
CREATE OR REPLACE FUNCTION set_loyalty_earn_date_ist()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
    NEW.earn_date_ist :=
        (NEW.created_at AT TIME ZONE 'Asia/Kolkata')::date;

    RETURN NEW;
END;
$$;

DROP TRIGGER IF EXISTS trg_set_loyalty_earn_date_ist
ON activity_loyalty_transactions;

CREATE TRIGGER trg_set_loyalty_earn_date_ist
    BEFORE INSERT OR UPDATE OF created_at
    ON activity_loyalty_transactions
    FOR EACH ROW
    EXECUTE FUNCTION set_loyalty_earn_date_ist();

-- Prevent duplicate daily loyalty transactions
CREATE UNIQUE INDEX IF NOT EXISTS
    ux_activity_loyalty_daily_earn
    ON activity_loyalty_transactions (
    business_id,
    customer_id,
    earn_date_ist
    );
