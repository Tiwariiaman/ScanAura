ALTER TABLE businesses
    ADD COLUMN public_slug VARCHAR(100);

ALTER TABLE businesses
    ADD CONSTRAINT uq_businesses_public_slug
        UNIQUE (public_slug);