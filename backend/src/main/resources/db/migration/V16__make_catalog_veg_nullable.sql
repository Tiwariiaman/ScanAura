ALTER TABLE catalogs
    ALTER COLUMN veg DROP NOT NULL;

UPDATE catalogs c
SET veg = NULL
    FROM businesses b
WHERE c.business_id = b.id
  AND b.business_type <> 'FOOD';