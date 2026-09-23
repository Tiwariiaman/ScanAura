CREATE TABLE IF NOT EXISTS gallery_images (
                                              id UUID PRIMARY KEY,

                                              business_id UUID NOT NULL,

                                              image_url VARCHAR(1000) NOT NULL,

    image_public_id VARCHAR(500) NOT NULL,

    display_order INTEGER NOT NULL DEFAULT 0,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_gallery_images_business
    FOREIGN KEY (business_id)
    REFERENCES businesses(id)
    ON DELETE CASCADE
    );

CREATE INDEX IF NOT EXISTS idx_gallery_images_business_id
    ON gallery_images(business_id);

CREATE INDEX IF NOT EXISTS idx_gallery_images_business_order
    ON gallery_images(business_id, display_order);