DROP TABLE IF EXISTS loan_application;
CREATE TABLE loan_application (
                                  application_id UUID PRIMARY KEY,
                                  customer_id UUID NOT NULL,
                                  loan_type VARCHAR(255) NOT NULL,
                                  amount DECIMAL(15,2) NOT NULL,
                                  loan_purpose VARCHAR(255) NOT NULL,
                                  tenure INT NOT NULL,
                                  status VARCHAR(20) NOT NULL
);