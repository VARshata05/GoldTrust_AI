-- V6: Claims Management, Guidewire-style Workflow, Repair, Replacement, and Settlement

CREATE TABLE claims (
    id BIGSERIAL PRIMARY KEY,
    claim_number VARCHAR(50) NOT NULL UNIQUE,
    policy_id BIGINT NOT NULL REFERENCES insurance_policies(id) ON DELETE RESTRICT,
    jewellery_asset_id BIGINT NOT NULL REFERENCES jewellery_assets(id) ON DELETE RESTRICT,
    customer_id BIGINT NOT NULL REFERENCES customers(id) ON DELETE RESTRICT,
    incident_type VARCHAR(50) NOT NULL, -- LOSS, THEFT, DAMAGE, MYSTERIOUS_DISAPPEARANCE, NATURAL_DISASTER
    incident_date DATE NOT NULL,
    incident_location VARCHAR(255) NOT NULL,
    incident_description TEXT NOT NULL,
    police_report_number VARCHAR(100),
    police_station VARCHAR(150),
    preferred_jeweller_id BIGINT REFERENCES jewellers(id),
    settlement_preference VARCHAR(50) DEFAULT 'REPAIR', -- REPAIR, REPLACEMENT, CASH_SETTLEMENT
    claimed_amount NUMERIC(15,2) NOT NULL,
    approved_amount NUMERIC(15,2) DEFAULT 0.00,
    deductible_amount NUMERIC(15,2) DEFAULT 0.00,
    net_settlement_amount NUMERIC(15,2) DEFAULT 0.00,
    status VARCHAR(50) NOT NULL DEFAULT 'SUBMITTED', 
    -- Workflow: DRAFT, SUBMITTED, VALIDATION, COVERAGE_CHECK, AI_ASSESSMENT, UNDER_REVIEW, ADDITIONAL_INFORMATION_REQUIRED, APPROVED, REJECTED, SETTLEMENT_PENDING, SETTLED, CLOSED
    assigned_officer_id BIGINT REFERENCES users(id),
    submission_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    closure_date TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE claim_evidence (
    id BIGSERIAL PRIMARY KEY,
    claim_id BIGINT NOT NULL REFERENCES claims(id) ON DELETE CASCADE,
    evidence_type VARCHAR(50) NOT NULL, -- POLICE_REPORT, DAMAGE_PHOTO, APPRAISAL_DOC, RECEIPT, CCTV, AFFIDAVIT
    file_name VARCHAR(255) NOT NULL,
    file_url VARCHAR(500) NOT NULL,
    file_size_bytes BIGINT,
    mime_type VARCHAR(100),
    description TEXT,
    uploaded_by VARCHAR(100),
    is_verified BOOLEAN DEFAULT FALSE,
    uploaded_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE claim_assessments (
    id BIGSERIAL PRIMARY KEY,
    claim_id BIGINT NOT NULL REFERENCES claims(id) ON DELETE CASCADE,
    assessor_user_id BIGINT REFERENCES users(id),
    policy_validity_confirmed BOOLEAN NOT NULL DEFAULT TRUE,
    coverage_confirmed BOOLEAN NOT NULL DEFAULT TRUE,
    applicable_coverage_code VARCHAR(50),
    deductible_percentage_applied NUMERIC(5,2) DEFAULT 5.00,
    identity_consistency_status VARCHAR(50), -- CONSISTENT, VERIFIED_TRANSFORMATION, REVIEW_REQUIRED, IDENTITY_INCONSISTENCY
    anomaly_detected BOOLEAN DEFAULT FALSE,
    anomaly_score NUMERIC(5,2) DEFAULT 0.00,
    ai_confidence_score NUMERIC(5,2) DEFAULT 0.00,
    recommendation VARCHAR(50) NOT NULL, -- APPROVE_REPAIR, APPROVE_REPLACEMENT, APPROVE_CASH, REFER_TO_SENIOR, REQUEST_ADDITIONAL_EVIDENCE, DECLINE
    rationale TEXT NOT NULL,
    assessment_report_json TEXT, -- Complete TAGE & anomaly explainable details
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE claim_decisions (
    id BIGSERIAL PRIMARY KEY,
    claim_id BIGINT NOT NULL REFERENCES claims(id) ON DELETE CASCADE,
    officer_id BIGINT NOT NULL REFERENCES users(id),
    decision VARCHAR(50) NOT NULL, -- APPROVED, REJECTED, REQUEST_INFORMATION
    decision_rationale TEXT NOT NULL,
    settlement_method VARCHAR(50), -- REPAIR, REPLACEMENT, CASH
    final_settlement_amount NUMERIC(15,2),
    decision_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE claim_financials (
    id BIGSERIAL PRIMARY KEY,
    claim_id BIGINT NOT NULL UNIQUE REFERENCES claims(id) ON DELETE CASCADE,
    initial_reserve NUMERIC(15,2) NOT NULL DEFAULT 0.00,
    current_reserve NUMERIC(15,2) NOT NULL DEFAULT 0.00,
    incurred_total NUMERIC(15,2) NOT NULL DEFAULT 0.00,
    paid_total NUMERIC(15,2) NOT NULL DEFAULT 0.00,
    salvage_recovery NUMERIC(15,2) DEFAULT 0.00,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE claim_reserves (
    id BIGSERIAL PRIMARY KEY,
    claim_id BIGINT NOT NULL REFERENCES claims(id) ON DELETE CASCADE,
    reserve_type VARCHAR(50) NOT NULL, -- INITIAL, ADJUSTMENT, FINAL
    reserve_amount NUMERIC(15,2) NOT NULL,
    reason TEXT NOT NULL,
    created_by BIGINT REFERENCES users(id),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE repair_orders (
    id BIGSERIAL PRIMARY KEY,
    order_number VARCHAR(50) NOT NULL UNIQUE,
    claim_id BIGINT NOT NULL REFERENCES claims(id) ON DELETE CASCADE,
    jewellery_asset_id BIGINT NOT NULL REFERENCES jewellery_assets(id),
    jeweller_id BIGINT NOT NULL REFERENCES jewellers(id),
    damage_description TEXT NOT NULL,
    repair_scope TEXT,
    estimated_cost NUMERIC(15,2) DEFAULT 0.00,
    approved_cost NUMERIC(15,2) DEFAULT 0.00,
    status VARCHAR(50) DEFAULT 'ESTIMATE_PENDING', -- ESTIMATE_PENDING, ESTIMATE_SUBMITTED, ESTIMATE_APPROVED, IN_PROGRESS, COMPLETED, VERIFIED
    estimate_submitted_at TIMESTAMP WITH TIME ZONE,
    estimate_approved_at TIMESTAMP WITH TIME ZONE,
    completed_at TIMESTAMP WITH TIME ZONE,
    jeweller_notes TEXT,
    post_repair_weight NUMERIC(10,3),
    post_repair_image_url VARCHAR(500),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE replacement_orders (
    id BIGSERIAL PRIMARY KEY,
    order_number VARCHAR(50) NOT NULL UNIQUE,
    claim_id BIGINT NOT NULL REFERENCES claims(id) ON DELETE CASCADE,
    original_asset_id BIGINT NOT NULL REFERENCES jewellery_assets(id),
    replacement_asset_id BIGINT REFERENCES jewellery_assets(id),
    jeweller_id BIGINT NOT NULL REFERENCES jewellers(id),
    approved_replacement_value NUMERIC(15,2) NOT NULL,
    actual_replacement_value NUMERIC(15,2),
    replacement_specs TEXT,
    status VARCHAR(50) DEFAULT 'SOURCING', -- SOURCING, READY_FOR_INSPECTION, INSPECTED, COMPLETED
    completed_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE settlement_records (
    id BIGSERIAL PRIMARY KEY,
    settlement_reference VARCHAR(50) NOT NULL UNIQUE,
    claim_id BIGINT NOT NULL REFERENCES claims(id) ON DELETE CASCADE,
    settlement_type VARCHAR(50) NOT NULL, -- REPAIR_VENDOR_PAYMENT, REPLACEMENT_VENDOR_PAYMENT, CUSTOMER_CASH_SETTLEMENT
    payee_type VARCHAR(50) NOT NULL, -- JEWELLER, CUSTOMER
    payee_name VARCHAR(150) NOT NULL,
    settlement_amount NUMERIC(15,2) NOT NULL,
    simulated_payment_ref VARCHAR(100),
    status VARCHAR(50) DEFAULT 'SETTLED',
    settled_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    notes TEXT
);

CREATE INDEX idx_claims_number ON claims(claim_number);
CREATE INDEX idx_claims_policy ON claims(policy_id);
CREATE INDEX idx_claims_customer ON claims(customer_id);
CREATE INDEX idx_claims_status ON claims(status);
CREATE INDEX idx_repair_orders_claim ON repair_orders(claim_id);
CREATE INDEX idx_replacement_orders_claim ON replacement_orders(claim_id);

