ALTER TABLE businesses
    ADD COLUMN IF NOT EXISTS instagram_url VARCHAR(500);

ALTER TABLE businesses
    ADD COLUMN IF NOT EXISTS instagram_enabled BOOLEAN;

ALTER TABLE businesses
    ADD COLUMN IF NOT EXISTS facebook_url VARCHAR(500);

ALTER TABLE businesses
    ADD COLUMN IF NOT EXISTS facebook_enabled BOOLEAN;

ALTER TABLE businesses
    ADD COLUMN IF NOT EXISTS youtube_url VARCHAR(500);

ALTER TABLE businesses
    ADD COLUMN IF NOT EXISTS youtube_enabled BOOLEAN;


UPDATE businesses
SET instagram_enabled = FALSE
WHERE instagram_enabled IS NULL;

UPDATE businesses
SET facebook_enabled = FALSE
WHERE facebook_enabled IS NULL;

UPDATE businesses
SET youtube_enabled = FALSE
WHERE youtube_enabled IS NULL;


ALTER TABLE businesses
    ALTER COLUMN instagram_enabled SET DEFAULT FALSE;

ALTER TABLE businesses
    ALTER COLUMN facebook_enabled SET DEFAULT FALSE;

ALTER TABLE businesses
    ALTER COLUMN youtube_enabled SET DEFAULT FALSE;


ALTER TABLE businesses
    ALTER COLUMN instagram_enabled SET NOT NULL;

ALTER TABLE businesses
    ALTER COLUMN facebook_enabled SET NOT NULL;

ALTER TABLE businesses
    ALTER COLUMN youtube_enabled SET NOT NULL;