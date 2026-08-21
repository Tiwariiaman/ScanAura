-- Add the dedicated Trial plan.
INSERT INTO plans (
    id,
    name,
    monthly_price,
    yearly_price,
    trial_days,
    ai_import_limit,
    branded_qr,
    priority_support,
    active,
    created_at,
    updated_at
)
VALUES (
           gen_random_uuid(),
           'Trial',
           0,
           0,
           7,
           3,
           false,
           false,
           true,
           now(),
           now()
       );

-- Paid plans do not own the trial period.
UPDATE plans
SET
    trial_days = 0,
    updated_at = now()
WHERE name IN ('Basic', 'Plus');