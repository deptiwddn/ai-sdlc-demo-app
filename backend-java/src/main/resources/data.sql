INSERT INTO invoices (id, customer_name, amount, status, created_at, decided_by, decided_at, decision_note) VALUES
('INV-1001', 'Lakeside School District', 4250.00, 'PendingApproval', CURRENT_TIMESTAMP(), NULL, NULL, NULL),
('INV-1002', 'Hilltop Academy', 980.50, 'PendingApproval', CURRENT_TIMESTAMP(), NULL, NULL, NULL),
('INV-1003', 'Riverside Charter', 12300.00, 'Approved', CURRENT_TIMESTAMP(), 'demo.approver', CURRENT_TIMESTAMP(), NULL),
('INV-1004', 'Oakwood Independent', 615.75, 'PendingApproval', CURRENT_TIMESTAMP(), NULL, NULL, NULL),
('INV-1005', 'Maple Grove Prep', 310.00, 'Draft', CURRENT_TIMESTAMP(), NULL, NULL, NULL);

