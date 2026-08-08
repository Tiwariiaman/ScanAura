CREATE TABLE subscription_requests (

                                       id UUID PRIMARY KEY,

                                       business_id UUID NOT NULL,

                                       plan_id UUID NOT NULL,

                                       billing_cycle VARCHAR(20) NOT NULL,

                                       payment_screenshot_url VARCHAR(500) NOT NULL,

                                       transaction_id VARCHAR(100) NOT NULL,

                                       status VARCHAR(30) NOT NULL,

                                       admin_remark VARCHAR(500),

                                       created_at TIMESTAMP,

                                       updated_at TIMESTAMP,

                                       CONSTRAINT fk_request_business
                                           FOREIGN KEY (business_id)
                                               REFERENCES businesses(id),

                                       CONSTRAINT fk_request_plan
                                           FOREIGN KEY (plan_id)
                                               REFERENCES plans(id)

);