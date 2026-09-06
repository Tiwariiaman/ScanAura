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