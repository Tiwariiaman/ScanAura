-- Allow catalog items to be saved when price is not available.
ALTER TABLE catalogs ALTER COLUMN price DROP NOT NULL;
