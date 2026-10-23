ALTER TABLE businesses ADD COLUMN IF NOT EXISTS order_dine_in_enabled BOOLEAN NOT NULL DEFAULT TRUE;
ALTER TABLE businesses ADD COLUMN IF NOT EXISTS order_takeaway_enabled BOOLEAN NOT NULL DEFAULT TRUE;
ALTER TABLE businesses ADD COLUMN IF NOT EXISTS order_delivery_enabled BOOLEAN NOT NULL DEFAULT TRUE;
ALTER TABLE businesses ADD COLUMN IF NOT EXISTS order_room_service_enabled BOOLEAN NOT NULL DEFAULT TRUE;
ALTER TABLE businesses ADD COLUMN IF NOT EXISTS order_self_service_enabled BOOLEAN NOT NULL DEFAULT TRUE;
ALTER TABLE businesses ADD COLUMN IF NOT EXISTS order_pickup_enabled BOOLEAN NOT NULL DEFAULT TRUE;

-- Existing catalogue prices remain nullable; this explicitly removes any legacy NOT NULL constraint.
ALTER TABLE catalogs ALTER COLUMN price DROP NOT NULL;
