INSERT INTO customer (customer_id, email, password_hash, first_name, last_name, dob, phone_number, address, account_status, email_verified, verification_token, reset_token, last_login_at, created_date) VALUES (
  '3fa85f64-5717-4562-b3fc-2c963f66afa6',
  'alice@example.com',
  '$2y$12$examplehashalice',
  'Alice',
  'Anderson',
  CAST('1988-02-10' AS DATE),
  '+1-555-0100',
  '123 Maple St, Springfield, IL',
  'Active',
  true,
  'verify-token-alice-1234',
  NULL,
  NULL,
  CAST('2026-05-21 09:00:00' AS TIMESTAMP)
);

INSERT INTO loan_application (application_id, customer_id, loan_amount, loan_purpose, tenure_months, status, created_date, updated_date) VALUES ('bbbbbbbb-bbbb-4bbb-bbbb-bbbbbbbbbbbb', '3fa85f64-5717-4562-b3fc-2c963f66afa6', 25000.00, 'Home Improvement', 60, 'Submitted', CAST('2026-05-21 09:00:00' AS TIMESTAMP), CAST('2026-05-21 09:00:00' AS TIMESTAMP));
