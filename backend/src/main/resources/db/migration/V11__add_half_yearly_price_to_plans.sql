ALTER TABLE plans
    ADD COLUMN half_yearly_price NUMERIC(10,2) NOT NULL DEFAULT 0;

UPDATE plans
SET half_yearly_price = 499,
    updated_at = now()
WHERE name = 'Basic';

UPDATE plans
SET half_yearly_price = 0,
    updated_at = now()
WHERE name = 'Plus';

UPDATE plans
SET half_yearly_price = 0,
    updated_at = now()
WHERE name = 'Trial';