-- ============================================================
-- ScanAura Production V3
-- Add Cloudinary public IDs for image cleanup
-- ============================================================

ALTER TABLE businesses
    ADD COLUMN IF NOT EXISTS logo_public_id VARCHAR(500);

ALTER TABLE catalogs
    ADD COLUMN IF NOT EXISTS image_public_id VARCHAR(500);

ALTER TABLE subscription_requests
    ADD COLUMN IF NOT EXISTS payment_screenshot_public_id VARCHAR(500);
