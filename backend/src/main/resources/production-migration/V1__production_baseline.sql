-- ============================================================
-- ScanAura Production Baseline V1
-- Generated from the current working PostgreSQL schema.
-- ============================================================

CREATE TABLE users (
                       id UUID NOT NULL,
                       created_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
                       updated_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
                       active BOOLEAN NOT NULL,
                       email VARCHAR(255) NOT NULL,
                       full_name VARCHAR(255) NOT NULL,
                       mobile VARCHAR(255) NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       role VARCHAR(255) NOT NULL,
                       verified BOOLEAN NOT NULL,
                       deleted BOOLEAN NOT NULL,

                       CONSTRAINT users_pkey PRIMARY KEY (id),
                       CONSTRAINT uk_users_mobile UNIQUE (mobile),
                       CONSTRAINT uk_users_email UNIQUE (email),

                       CONSTRAINT users_role_check
                           CHECK (
                               role IN (
                                        'ADMIN',
                                        'BUSINESS_OWNER'
                                   )
                               )
);

CREATE TABLE businesses (
                            id UUID NOT NULL,
                            created_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
                            updated_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
                            active BOOLEAN NOT NULL,
                            address VARCHAR(255),
                            business_name VARCHAR(255) NOT NULL,
                            business_type VARCHAR(255) NOT NULL,
                            city VARCHAR(255),
                            country VARCHAR(255),
                            description VARCHAR(1000),
                            email VARCHAR(255),
                            logo_url VARCHAR(255),
                            phone VARCHAR(255) NOT NULL,
                            pincode VARCHAR(255),
                            qr_slug VARCHAR(255) NOT NULL,
                            state VARCHAR(255),
                            upi_id VARCHAR(255),
                            website VARCHAR(255),
                            whatsapp VARCHAR(255),
                            owner_id UUID NOT NULL,

                            CONSTRAINT businesses_pkey PRIMARY KEY (id),
                            CONSTRAINT uk_businesses_qr_slug UNIQUE (qr_slug),

                            CONSTRAINT businesses_business_type_check
                                CHECK (
                                    business_type IN (
                                                      'FOOD',
                                                      'RETAIL',
                                                      'ECOMMERCE',
                                                      'SERVICES',
                                                      'PERSONAL_BRAND',
                                                      'OTHER'
                                        )
                                    )
);

CREATE TABLE categories (
                            id UUID NOT NULL,
                            created_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
                            updated_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
                            active BOOLEAN NOT NULL,
                            display_order INTEGER NOT NULL,
                            name VARCHAR(255) NOT NULL,
                            business_id UUID NOT NULL,

                            CONSTRAINT categories_pkey PRIMARY KEY (id)
);

CREATE TABLE catalogs (
                          id UUID NOT NULL,
                          created_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
                          updated_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
                          active BOOLEAN NOT NULL,
                          available BOOLEAN NOT NULL,
                          best_seller BOOLEAN NOT NULL,
                          description VARCHAR(1000),
                          display_order INTEGER NOT NULL,
                          image_url VARCHAR(255),
                          name VARCHAR(255) NOT NULL,
                          price NUMERIC(10,2) NOT NULL,
                          recommended BOOLEAN NOT NULL,
                          veg BOOLEAN NOT NULL,
                          business_id UUID NOT NULL,
                          category_id UUID,

                          CONSTRAINT catalogs_pkey PRIMARY KEY (id)
);

CREATE TABLE email_verification_tokens (
                                           id UUID NOT NULL,
                                           created_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
                                           updated_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
                                           expires_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
                                           token VARCHAR(100) NOT NULL,
                                           used BOOLEAN NOT NULL,
                                           user_id UUID NOT NULL,

                                           CONSTRAINT email_verification_tokens_pkey
                                               PRIMARY KEY (id),

                                           CONSTRAINT idx_email_verification_token
                                               UNIQUE (token),

                                           CONSTRAINT uks3mje1c85ftmp2uld6dt1bffs
                                               UNIQUE (user_id)
);

CREATE TABLE password_reset_tokens (
                                       id UUID NOT NULL,
                                       created_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
                                       updated_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
                                       expires_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
                                       token VARCHAR(100) NOT NULL,
                                       used BOOLEAN NOT NULL,
                                       user_id UUID NOT NULL,

                                       CONSTRAINT password_reset_tokens_pkey
                                           PRIMARY KEY (id),

                                       CONSTRAINT idx_password_reset_token
                                           UNIQUE (token),

                                       CONSTRAINT uk_password_reset_user
                                           UNIQUE (user_id)
);

CREATE TABLE plans (
                       id UUID NOT NULL,
                       created_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
                       updated_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
                       active BOOLEAN NOT NULL,
                       ai_import_limit INTEGER NOT NULL,
                       branded_qr BOOLEAN NOT NULL,
                       monthly_price NUMERIC(10,2) NOT NULL,
                       name VARCHAR(255) NOT NULL,
                       priority_support BOOLEAN NOT NULL,
                       trial_days INTEGER NOT NULL,
                       yearly_price NUMERIC(10,2) NOT NULL,

                       CONSTRAINT plans_pkey PRIMARY KEY (id),
                       CONSTRAINT uk_plans_name UNIQUE (name)
);

CREATE TABLE qr_codes (
                          id UUID NOT NULL,
                          created_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
                          updated_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
                          active BOOLEAN NOT NULL,
                          assigned BOOLEAN NOT NULL,
                          qr_code VARCHAR(30) NOT NULL,
                          type VARCHAR(255) NOT NULL,
                          business_id UUID,

                          CONSTRAINT qr_codes_pkey PRIMARY KEY (id),
                          CONSTRAINT uk_qr_codes_code UNIQUE (qr_code),

                          CONSTRAINT qr_codes_type_check
                              CHECK (
                                  type IN (
                                           'DIGITAL',
                                           'PHYSICAL'
                                      )
                                  )
);

CREATE TABLE subscription_requests (
                                       id UUID NOT NULL,
                                       created_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
                                       updated_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
                                       admin_remark VARCHAR(500),
                                       billing_cycle VARCHAR(255) NOT NULL,
                                       payment_screenshot_url VARCHAR(500) NOT NULL,
                                       status VARCHAR(255) NOT NULL,
                                       transaction_id VARCHAR(100) NOT NULL,
                                       business_id UUID NOT NULL,
                                       plan_id UUID NOT NULL,

                                       CONSTRAINT subscription_requests_pkey
                                           PRIMARY KEY (id),

                                       CONSTRAINT subscription_requests_billing_cycle_check
                                           CHECK (
                                               billing_cycle IN (
                                                                 'MONTHLY',
                                                                 'YEARLY'
                                                   )
                                               ),

                                       CONSTRAINT subscription_requests_status_check
                                           CHECK (
                                               status IN (
                                                          'PENDING',
                                                          'APPROVED',
                                                          'REJECTED'
                                                   )
                                               )
);

CREATE TABLE subscriptions (
                               id UUID NOT NULL,
                               created_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
                               updated_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
                               admin_remark VARCHAR(500),
                               ai_import_used INTEGER NOT NULL,
                               approved_at TIMESTAMP(6) WITHOUT TIME ZONE,
                               billing_cycle VARCHAR(255) NOT NULL,
                               end_date DATE NOT NULL,
                               payment_screenshot_url VARCHAR(500),
                               start_date DATE NOT NULL,
                               status VARCHAR(255) NOT NULL,
                               transaction_id VARCHAR(100),
                               business_id UUID NOT NULL,
                               plan_id UUID NOT NULL,

                               CONSTRAINT subscriptions_pkey
                                   PRIMARY KEY (id),

                               CONSTRAINT uk_subscriptions_business
                                   UNIQUE (business_id),

                               CONSTRAINT subscriptions_billing_cycle_check
                                   CHECK (
                                       billing_cycle IN (
                                                         'MONTHLY',
                                                         'YEARLY'
                                           )
                                       ),

                               CONSTRAINT subscriptions_status_check
                                   CHECK (
                                       status IN (
                                                  'TRIAL',
                                                  'PENDING',
                                                  'ACTIVE',
                                                  'EXPIRED',
                                                  'CANCELLED',
                                                  'REJECTED'
                                           )
                                       )
);

-- ============================================================
-- FOREIGN KEYS
-- ============================================================

ALTER TABLE ONLY businesses
    ADD CONSTRAINT fk_businesses_owner
    FOREIGN KEY (owner_id)
    REFERENCES users(id);

ALTER TABLE ONLY categories
    ADD CONSTRAINT fk_categories_business
    FOREIGN KEY (business_id)
    REFERENCES businesses(id);

ALTER TABLE ONLY catalogs
    ADD CONSTRAINT fk_catalogs_category
    FOREIGN KEY (category_id)
    REFERENCES categories(id);

ALTER TABLE ONLY catalogs
    ADD CONSTRAINT fk_catalogs_business
    FOREIGN KEY (business_id)
    REFERENCES businesses(id);

ALTER TABLE ONLY email_verification_tokens
    ADD CONSTRAINT fk_email_verification_user
    FOREIGN KEY (user_id)
    REFERENCES users(id);

ALTER TABLE ONLY password_reset_tokens
    ADD CONSTRAINT fk_password_reset_user
    FOREIGN KEY (user_id)
    REFERENCES users(id);

ALTER TABLE ONLY qr_codes
    ADD CONSTRAINT fk_qr_codes_business
    FOREIGN KEY (business_id)
    REFERENCES businesses(id);

ALTER TABLE ONLY subscription_requests
    ADD CONSTRAINT fk_subscription_requests_business
    FOREIGN KEY (business_id)
    REFERENCES businesses(id);

ALTER TABLE ONLY subscription_requests
    ADD CONSTRAINT fk_subscription_requests_plan
    FOREIGN KEY (plan_id)
    REFERENCES plans(id);

ALTER TABLE ONLY subscriptions
    ADD CONSTRAINT fk_subscriptions_business
    FOREIGN KEY (business_id)
    REFERENCES businesses(id);

ALTER TABLE ONLY subscriptions
    ADD CONSTRAINT fk_subscriptions_plan
    FOREIGN KEY (plan_id)
    REFERENCES plans(id);

-- ============================================================
-- DEFAULT PLANS
-- ============================================================

INSERT INTO plans (
    id,
    name,
    monthly_price,
    yearly_price,
    trial_days,
    ai_import_limit,
    branded_qr,
    priority_support,
    active,
    created_at,
    updated_at
)
VALUES (
           gen_random_uuid(),
           'Basic',
           99,
           999,
           0,
           3,
           false,
           false,
           true,
           now(),
           now()
       );

INSERT INTO plans (
    id,
    name,
    monthly_price,
    yearly_price,
    trial_days,
    ai_import_limit,
    branded_qr,
    priority_support,
    active,
    created_at,
    updated_at
)
VALUES (
           gen_random_uuid(),
           'Plus',
           199,
           1999,
           0,
           -1,
           true,
           true,
           true,
           now(),
           now()
       );

INSERT INTO plans (
    id,
    name,
    monthly_price,
    yearly_price,
    trial_days,
    ai_import_limit,
    branded_qr,
    priority_support,
    active,
    created_at,
    updated_at
)
VALUES (
           gen_random_uuid(),
           'Trial',
           0,
           0,
           7,
           3,
           false,
           false,
           true,
           now(),
           now()
       );