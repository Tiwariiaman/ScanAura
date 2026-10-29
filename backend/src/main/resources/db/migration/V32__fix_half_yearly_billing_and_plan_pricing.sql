-- Keep subscription request and active subscription billing cycles aligned.
ALTER TABLE subscription_requests
    DROP CONSTRAINT IF EXISTS subscription_requests_billing_cycle_check;

ALTER TABLE subscription_requests
    ADD CONSTRAINT subscription_requests_billing_cycle_check
        CHECK (billing_cycle IN ('MONTHLY', 'HALF_YEARLY', 'YEARLY'));

ALTER TABLE subscriptions
    DROP CONSTRAINT IF EXISTS subscriptions_billing_cycle_check;

ALTER TABLE subscriptions
    ADD CONSTRAINT subscriptions_billing_cycle_check
        CHECK (billing_cycle IN ('MONTHLY', 'HALF_YEARLY', 'YEARLY'));

UPDATE plans
SET monthly_price = 99,
    half_yearly_price = 499,
    yearly_price = 999,
    ai_import_limit = 3,
    updated_at = now()
WHERE lower(name) = 'basic';

UPDATE plans
SET monthly_price = 199,
    half_yearly_price = 999,
    yearly_price = 1999,
    ai_import_limit = 5,
    updated_at = now()
WHERE lower(name) = 'plus';
