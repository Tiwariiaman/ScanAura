-- ============================================================
-- ScanAura Loyalty
-- V13 - Prevent duplicate daily EARN transactions
-- ============================================================

CREATE UNIQUE INDEX IF NOT EXISTS
    ux_activity_loyalty_daily_earn
    ON activity_loyalty_transactions (
    business_id,
    customer_id,
    ((created_at AT TIME ZONE 'Asia/Kolkata')::date)
    )
    WHERE transaction_type = 'EARN';