CREATE TABLE plans (

                       id UUID PRIMARY KEY,

                       name VARCHAR(50) NOT NULL UNIQUE,

                       monthly_price NUMERIC(10,2) NOT NULL,

                       yearly_price NUMERIC(10,2) NOT NULL,

                       trial_days INTEGER NOT NULL,

                       ai_import_limit INTEGER NOT NULL,

                       branded_qr BOOLEAN NOT NULL,

                       priority_support BOOLEAN NOT NULL,

                       active BOOLEAN NOT NULL,

                       created_at TIMESTAMP,

                       updated_at TIMESTAMP

);