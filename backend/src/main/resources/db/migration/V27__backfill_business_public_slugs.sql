-- Existing businesses created before public links were enabled may not have a slug.
-- Preserve existing custom slugs and generate a readable, unique slug only where missing.
UPDATE businesses
SET public_slug =
    COALESCE(
        NULLIF(
            LEFT(
                TRIM(BOTH '-' FROM REGEXP_REPLACE(LOWER(COALESCE(business_name, '')), '[^a-z0-9]+', '-', 'g')),
                60
            ),
            ''
        ),
        'business'
    )
    || '-'
    || REPLACE(id::text, '-', '')
WHERE public_slug IS NULL OR BTRIM(public_slug) = '';

CREATE INDEX IF NOT EXISTS idx_businesses_public_slug_active
    ON businesses (public_slug)
    WHERE active = TRUE AND public_slug IS NOT NULL;
