-- ============================================================
-- ScanAura Loyalty
-- Fix daily EARN constraint so REDEEM transactions are allowed
-- ============================================================

DROP INDEX IF EXISTS ux_activity_loyalty_daily_earn;

CREATE UNIQUE INDEX ux_activity_loyalty_daily_earn
    ON activity_loyalty_transactions (
                                      business_id,
                                      customer_id,
                                      earn_date_ist
        )
    WHERE transaction_type = 'EARN';