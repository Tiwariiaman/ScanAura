-- Speeds up the 24-hour per-mobile Scratch Card cooldown lookup.
-- V24__create_scratch_card.sql must have been applied before this migration.
CREATE INDEX IF NOT EXISTS idx_scratch_plays_mobile_played
    ON activity_scratch_plays (mobile_number, played_at DESC);
