MERGE INTO customer KEY (customer_id) VALUES (
  '3fa85f64-5717-4562-b3fc-2c963f66afa6',
  'alice@example.com',
  '$2y$12$examplehashalice',
  'Alice',
  'Anderson',
  DATE '1988-02-10',
  '+1-555-0100',
  '123 Maple St, Springfield, IL',
  'Active',
  TRUE,
  'verify-token-alice-1234',
  NULL,
  TIMESTAMP '2026-05-21 09:00:00',
  TIMESTAMP '2026-05-21 09:00:00'
);

MERGE INTO loan_application KEY (application_id) VALUES ('bbbbbbbb-bbbb-4bbb-bbbb-bbbbbbbbbbbb', '3fa85f64-5717-4562-b3fc-2c963f66afa6', 25000.00, 'Home Improvement', 60, 'Submitted', TIMESTAMP '2026-05-21 09:00:00', TIMESTAMP '2026-05-21 09:00:00');
