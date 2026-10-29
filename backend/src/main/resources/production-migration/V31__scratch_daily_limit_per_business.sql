-- Scratch Card cooldown is scoped per business and IST calendar day.
-- The card row is locked during play; this composite index makes the per-business/day lookup efficient.
CREATE INDEX IF NOT EXISTS idx_scratch_plays_business_mobile_played
    ON activity_scratch_plays (business_id, mobile_number, played_at);
