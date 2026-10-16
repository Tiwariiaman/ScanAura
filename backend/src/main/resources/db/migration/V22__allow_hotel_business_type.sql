
ALTER TABLE businesses
DROP CONSTRAINT businesses_business_type_check;

ALTER TABLE businesses
    ADD CONSTRAINT businesses_business_type_check
        CHECK (
            business_type IN (
                              'FOOD',
                              'RETAIL',
                              'ECOMMERCE',
                              'SERVICES',
                              'PERSONAL_BRAND',
                              'OTHER',
                              'HOTEL'
                )
            );
