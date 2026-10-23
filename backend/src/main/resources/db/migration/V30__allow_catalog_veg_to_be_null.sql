-- Veg classification applies only to FOOD businesses. Non-food catalogue rows must allow NULL.
ALTER TABLE catalogs ALTER COLUMN veg DROP NOT NULL;
