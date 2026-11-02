-- V5: Policy Administration, Endorsements, Renewals, Billing, and Simulated Payments

CREATE TABLE insurance_policies (
    id BIGSERIAL PRIMARY KEY,
    policy_number VARCHAR(50) NOT NULL UNIQUE,
    application_id BIGINT REFERENCES insurance_applications(id),
    customer_id BIGINT NOT NULL REFERENCES customers(id) ON DELETE RESTRICT,
    product_id BIGINT NOT NULL REFERENCES insurance_products(id) ON DELETE RESTRICT,
    status VARCHAR(50) NOT NULL DEFAULT 'ISSUED', -- DRAFT, QUOTED, APPLICATION_SUBMITTED, UNDERWRITING, APPROVED, ISSUED, ACTIVE, RENEWAL_PENDING, RENEWED, EXPIRED, CANCELLED
    total_sum_insured NUMERIC(15,2) NOT NULL,
    annual_premium NUMERIC(15,2) NOT NULL,
    payment_frequency VARCHAR(20) DEFAULT 'ANNUAL', -- ANNUAL, MONTHLY
    effective_date DATE NOT NULL,
    expiration_date DATE NOT NULL,
    policy_document_url VARCHAR(500),
    renewal_count INT NOT NULL DEFAULT 0,
    issued_by_user_id BIGINT REFERENCES users(id),
    issued_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE policy_assets (
    id BIGSERIAL PRIMARY KEY,
    policy_id BIGINT NOT NULL REFERENCES insurance_policies(id) ON DELETE CASCADE,
    jewellery_asset_id BIGINT NOT NULL REFERENCES jewellery_assets(id) ON DELETE RESTRICT,
    asset_version_number INT NOT NULL DEFAULT 1,
    sum_insured NUMERIC(15,2) NOT NULL,
    allocated_premium NUMERIC(15,2) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_policy_asset UNIQUE (policy_id, jewellery_asset_id)
);

CREATE TABLE policy_coverages (
    id BIGSERIAL PRIMARY KEY,
    policy_id BIGINT NOT NULL REFERENCES insurance_policies(id) ON DELETE CASCADE,
    coverage_id BIGINT NOT NULL REFERENCES coverage_definitions(id) ON DELETE RESTRICT,
    coverage_limit NUMERIC(15,2) NOT NULL,
    deductible_amount NUMERIC(15,2) NOT NULL,
    premium_charge NUMERIC(15,2) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_policy_coverage UNIQUE (policy_id, coverage_id)
);

CREATE TABLE policy_endorsements (
    id BIGSERIAL PRIMARY KEY,
    endorsement_number VARCHAR(50) NOT NULL UNIQUE,
    policy_id BIGINT NOT NULL REFERENCES insurance_policies(id) ON DELETE CASCADE,
    endorsement_type VARCHAR(50) NOT NULL, -- VALUE_UPDATE, ASSET_ADDED, ASSET_REMOVED, COVERAGE_CHANGE, ADDRESS_CHANGE
    description TEXT NOT NULL,
    previous_values_json TEXT,
    new_values_json TEXT,
    premium_delta NUMERIC(15,2) DEFAULT 0.00,
    status VARCHAR(50) DEFAULT 'APPROVED', -- REQUESTED, UNDER_REVIEW, APPROVED, REJECTED
    approved_by_user_id BIGINT REFERENCES users(id),
    effective_date DATE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE policy_renewals (
    id BIGSERIAL PRIMARY KEY,
    policy_id BIGINT NOT NULL REFERENCES insurance_policies(id) ON DELETE CASCADE,
    renewal_cycle INT NOT NULL DEFAULT 1,
    previous_sum_insured NUMERIC(15,2) NOT NULL,
    new_sum_insured NUMERIC(15,2) NOT NULL,
    renewal_premium NUMERIC(15,2) NOT NULL,
    renewal_status VARCHAR(50) DEFAULT 'PENDING', -- PENDING, ACCEPTED, DECLINED, EXPIRED
    renewal_effective_date DATE NOT NULL,
    renewal_expiry_date DATE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE billing_accounts (
    id BIGSERIAL PRIMARY KEY,
    account_number VARCHAR(50) NOT NULL UNIQUE,
    customer_id BIGINT NOT NULL UNIQUE REFERENCES customers(id) ON DELETE CASCADE,
    current_balance NUMERIC(15,2) NOT NULL DEFAULT 0.00,
    total_billed NUMERIC(15,2) NOT NULL DEFAULT 0.00,
    total_paid NUMERIC(15,2) NOT NULL DEFAULT 0.00,
    status VARCHAR(50) DEFAULT 'CURRENT', -- CURRENT, OVERDUE, IN_DEFAULT
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE invoices (
    id BIGSERIAL PRIMARY KEY,
    invoice_number VARCHAR(50) NOT NULL UNIQUE,
    billing_account_id BIGINT NOT NULL REFERENCES billing_accounts(id) ON DELETE CASCADE,
    policy_id BIGINT REFERENCES insurance_policies(id) ON DELETE SET NULL,
    invoice_type VARCHAR(50) NOT NULL, -- NEW_POLICY, RENEWAL, ENDORSEMENT
    amount NUMERIC(15,2) NOT NULL,
    tax_amount NUMERIC(15,2) DEFAULT 0.00,
    total_amount NUMERIC(15,2) NOT NULL,
    due_date DATE NOT NULL,
    status VARCHAR(50) DEFAULT 'PENDING', -- PENDING, PAID, OVERDUE, CANCELLED
    notes TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE payments (
    id BIGSERIAL PRIMARY KEY,
    payment_reference VARCHAR(50) NOT NULL UNIQUE,
    invoice_id BIGINT NOT NULL REFERENCES invoices(id) ON DELETE CASCADE,
    billing_account_id BIGINT NOT NULL REFERENCES billing_accounts(id) ON DELETE CASCADE,
    amount_paid NUMERIC(15,2) NOT NULL,
    payment_method VARCHAR(50) NOT NULL, -- SIMULATED_UPI, SIMULATED_CARD, SIMULATED_NET_BANKING
    gateway_reference VARCHAR(100),
    transaction_status VARCHAR(50) NOT NULL DEFAULT 'COMPLETED', -- COMPLETED, FAILED, REFUNDED
    receipt_url VARCHAR(500),
    payment_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    notes TEXT
);

CREATE TABLE refunds (
    id BIGSERIAL PRIMARY KEY,
    refund_reference VARCHAR(50) NOT NULL UNIQUE,
    payment_id BIGINT NOT NULL REFERENCES payments(id) ON DELETE RESTRICT,
    billing_account_id BIGINT NOT NULL REFERENCES billing_accounts(id) ON DELETE CASCADE,
    amount NUMERIC(15,2) NOT NULL,
    reason TEXT NOT NULL,
    status VARCHAR(50) DEFAULT 'PROCESSED',
    processed_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_policies_number ON insurance_policies(policy_number);
CREATE INDEX idx_policies_customer ON insurance_policies(customer_id);
CREATE INDEX idx_policies_status ON insurance_policies(status);
CREATE INDEX idx_invoices_account ON invoices(billing_account_id);
CREATE INDEX idx_payments_invoice ON payments(invoice_id);

