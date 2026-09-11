ALTER TABLE subscription_requests
DROP CONSTRAINT subscription_requests_billing_cycle_check;

ALTER TABLE subscription_requests
    ADD CONSTRAINT subscription_requests_billing_cycle_check
        CHECK (
            billing_cycle IN ('MONTHLY', 'HALF_YEARLY', 'YEARLY')
            );