-- V4: Quotation Engine and Guidewire-Aligned Underwriting Engine

CREATE TABLE quotations (
    id BIGSERIAL PRIMARY KEY,
    quotation_number VARCHAR(50) NOT NULL UNIQUE,
    customer_id BIGINT NOT NULL REFERENCES customers(id) ON DELETE CASCADE,
    product_id BIGINT NOT NULL REFERENCES insurance_products(id) ON DELETE RESTRICT,
    insured_value NUMERIC(15,2) NOT NULL,
    deductible_percentage NUMERIC(5,2) NOT NULL DEFAULT 5.00,
    base_premium NUMERIC(15,2) NOT NULL,
    risk_factor NUMERIC(6,4) NOT NULL DEFAULT 1.0000,
    coverage_factor NUMERIC(6,4) NOT NULL DEFAULT 1.0000,
    location_factor NUMERIC(6,4) NOT NULL DEFAULT 1.0000,
    total_annual_premium NUMERIC(15,2) NOT NULL,
    monthly_equivalent NUMERIC(15,2) NOT NULL,
    selected_coverages_json TEXT, -- JSON array of coverage codes and factors
    status VARCHAR(50) DEFAULT 'ACTIVE', -- ACTIVE, CONVERTED_TO_APPLICATION, EXPIRED
    valid_until TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE quotation_items (
    id BIGSERIAL PRIMARY KEY,
    quotation_id BIGINT NOT NULL REFERENCES quotations(id) ON DELETE CASCADE,
    jewellery_asset_id BIGINT NOT NULL REFERENCES jewellery_assets(id) ON DELETE RESTRICT,
    insured_value NUMERIC(15,2) NOT NULL,
    allocated_premium NUMERIC(15,2) NOT NULL
);

CREATE TABLE insurance_applications (
    id BIGSERIAL PRIMARY KEY,
    application_number VARCHAR(50) NOT NULL UNIQUE,
    customer_id BIGINT NOT NULL REFERENCES customers(id) ON DELETE CASCADE,
    quotation_id BIGINT REFERENCES quotations(id),
    product_id BIGINT NOT NULL REFERENCES insurance_products(id),
    total_insured_value NUMERIC(15,2) NOT NULL,
    proposed_premium NUMERIC(15,2) NOT NULL,
    status VARCHAR(50) DEFAULT 'SUBMITTED', -- SUBMITTED, UNDERWRITING, APPROVED, REFERRED, DECLINED, REQUEST_INFORMATION
    customer_notes TEXT,
    supporting_docs_json TEXT,
    underwriting_decision VARCHAR(50), -- APPROVE, REFER, DECLINE, REQUEST_INFORMATION
    assigned_officer_id BIGINT REFERENCES users(id),
    decision_date TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE underwriting_assessments (
    id BIGSERIAL PRIMARY KEY,
    application_id BIGINT NOT NULL REFERENCES insurance_applications(id) ON DELETE CASCADE,
    officer_id BIGINT REFERENCES users(id),
    decision VARCHAR(50) NOT NULL, -- APPROVE, REFER, DECLINE, REQUEST_INFORMATION
    jewellery_verification_score NUMERIC(5,2),
    appraisal_validity_score NUMERIC(5,2),
    customer_risk_score NUMERIC(5,2),
    ai_risk_assessment_score NUMERIC(5,2),
    overall_risk_score NUMERIC(5,2) NOT NULL,
    decision_reasons TEXT NOT NULL,
    risk_indicators_json TEXT,
    stipulations TEXT,
    assessed_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_quotations_number ON quotations(quotation_number);
CREATE INDEX idx_applications_number ON insurance_applications(application_number);
CREATE INDEX idx_applications_customer ON insurance_applications(customer_id);
CREATE INDEX idx_applications_status ON insurance_applications(status);

