
-- Store the bill amount associated with each loyalty transaction.
ALTER TABLE activity_loyalty_transactions
    ADD COLUMN IF NOT EXISTS bill_amount NUMERIC(10, 2);

-- Allow multiple legitimate purchases by the same customer
-- on the same day. Each temporary QR will be verified only once.
DROP INDEX IF EXISTS ux_activity_loyalty_daily_earn;
