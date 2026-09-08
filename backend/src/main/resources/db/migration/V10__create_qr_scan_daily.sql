CREATE TABLE qr_scan_daily (

                               id UUID PRIMARY KEY,

                               business_id UUID NOT NULL,

                               scan_date DATE NOT NULL,

                               scan_count BIGINT NOT NULL DEFAULT 0,

                               created_at TIMESTAMP NOT NULL,

                               updated_at TIMESTAMP NOT NULL,

                               CONSTRAINT fk_qr_scan_daily_business
                                   FOREIGN KEY (business_id)
                                       REFERENCES businesses(id)
                                       ON DELETE CASCADE,

                               CONSTRAINT uq_qr_scan_daily_business_date
                                   UNIQUE (business_id, scan_date)

);

CREATE INDEX idx_qr_scan_daily_business_id
    ON qr_scan_daily (business_id);

CREATE INDEX idx_qr_scan_daily_scan_date
    ON qr_scan_daily (scan_date);