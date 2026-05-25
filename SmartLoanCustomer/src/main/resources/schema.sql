-- Drop existing tables in reverse order of creation (handle foreign key constraints)
DROP TABLE IF EXISTS loan_application;
DROP TABLE IF EXISTS session_token;
DROP TABLE IF EXISTS login_history;
DROP TABLE IF EXISTS customer;

-- Create tables
CREATE TABLE customer (
  customer_id UUID PRIMARY KEY,
  email VARCHAR(255) NOT NULL UNIQUE,
  password_hash VARCHAR(512) NOT NULL,
  first_name VARCHAR(50) NOT NULL,
  last_name VARCHAR(50) NOT NULL,
  dob DATE NOT NULL,
  phone_number VARCHAR(20) NOT NULL,
  address VARCHAR(255) NOT NULL,
  account_status VARCHAR(10) NOT NULL,
  email_verified BOOLEAN NOT NULL DEFAULT FALSE,
  verification_token VARCHAR(128),
  reset_token VARCHAR(128),
  last_login_at TIMESTAMP,
  created_date TIMESTAMP NOT NULL
);

CREATE TABLE login_history (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  customer_id UUID NOT NULL,
  login_at TIMESTAMP NOT NULL,
  success BOOLEAN NOT NULL,
  CONSTRAINT fk_login_history_customer FOREIGN KEY (customer_id) REFERENCES customer(customer_id)
);

CREATE TABLE session_token (
  token VARCHAR(128) PRIMARY KEY,
  customer_id UUID NOT NULL,
  created_at TIMESTAMP NOT NULL,
  last_active_at TIMESTAMP NOT NULL,
  expires_at TIMESTAMP NOT NULL,
  remember_me BOOLEAN NOT NULL,
  CONSTRAINT fk_session_token_customer FOREIGN KEY (customer_id) REFERENCES customer(customer_id)
);

CREATE TABLE loan_application (
  application_id UUID PRIMARY KEY,
  customer_id UUID NOT NULL,
  loan_amount DECIMAL(15,2) NOT NULL,
  loan_purpose VARCHAR(255) NOT NULL,
  tenure_months INT NOT NULL,
  status VARCHAR(20) NOT NULL,
  created_date TIMESTAMP NOT NULL,
  updated_date TIMESTAMP NOT NULL,
  CONSTRAINT fk_loan_customer FOREIGN KEY (customer_id) REFERENCES customer(customer_id)
);
