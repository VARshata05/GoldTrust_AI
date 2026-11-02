-- V3: Insurance Products, Configurable Coverages, Gold Rates, and Valuation

CREATE TABLE insurance_products (
    id BIGSERIAL PRIMARY KEY,
    product_code VARCHAR(50) NOT NULL UNIQUE,
    product_name VARCHAR(150) NOT NULL,
    description TEXT,
    base_rate_percentage NUMERIC(6,4) NOT NULL DEFAULT 0.0150, -- 1.5%
    min_sum_insured NUMERIC(15,2) DEFAULT 10000.00,
    max_sum_insured NUMERIC(15,2) DEFAULT 10000000.00,
    tenure_months INT DEFAULT 12,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE coverage_definitions (
    id BIGSERIAL PRIMARY KEY,
    coverage_code VARCHAR(50) NOT NULL UNIQUE,
    coverage_name VARCHAR(100) NOT NULL,
    description TEXT,
    coverage_limit_percentage NUMERIC(5,2) NOT NULL DEFAULT 100.00, -- e.g. 100% of sum insured
    default_deductible_percentage NUMERIC(5,2) NOT NULL DEFAULT 5.00, -- e.g. 5% deductible
    premium_factor NUMERIC(6,4) NOT NULL DEFAULT 0.0020, -- additional factor
    eligibility_rules TEXT, -- e.g. JSON rules
    effective_date DATE NOT NULL DEFAULT CURRENT_DATE,
    expiration_date DATE,
    is_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE product_coverages (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL REFERENCES insurance_products(id) ON DELETE CASCADE,
    coverage_id BIGINT NOT NULL REFERENCES coverage_definitions(id) ON DELETE CASCADE,
    is_mandatory BOOLEAN NOT NULL DEFAULT FALSE,
    is_default_selected BOOLEAN NOT NULL DEFAULT TRUE,
    display_order INT DEFAULT 1,
    CONSTRAINT uq_product_coverage UNIQUE (product_id, coverage_id)
);

CREATE TABLE gold_rates (
    id BIGSERIAL PRIMARY KEY,
    currency VARCHAR(10) NOT NULL DEFAULT 'INR',
    rate_24k NUMERIC(10,2) NOT NULL, -- per gram
    rate_22k NUMERIC(10,2) NOT NULL,
    rate_18k NUMERIC(10,2) NOT NULL,
    rate_14k NUMERIC(10,2) NOT NULL,
    source VARCHAR(100) DEFAULT 'BULLION_EXCHANGE_LIVE',
    effective_date DATE NOT NULL DEFAULT CURRENT_DATE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE appraisals (
    id BIGSERIAL PRIMARY KEY,
    jewellery_asset_id BIGINT NOT NULL REFERENCES jewellery_assets(id) ON DELETE CASCADE,
    jeweller_id BIGINT REFERENCES jewellers(id),
    appraiser_name VARCHAR(150),
    appraised_gold_value NUMERIC(15,2) NOT NULL,
    appraised_stones_value NUMERIC(15,2) DEFAULT 0.00,
    appraised_making_charges NUMERIC(15,2) DEFAULT 0.00,
    total_appraisal_value NUMERIC(15,2) NOT NULL,
    certificate_number VARCHAR(100),
    appraisal_date DATE NOT NULL,
    expiry_date DATE,
    document_url VARCHAR(500),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE valuation_records (
    id BIGSERIAL PRIMARY KEY,
    jewellery_asset_id BIGINT NOT NULL REFERENCES jewellery_assets(id) ON DELETE CASCADE,
    gold_rate_id BIGINT REFERENCES gold_rates(id),
    net_gold_weight NUMERIC(10,3) NOT NULL,
    purity VARCHAR(20) NOT NULL,
    applicable_gold_rate NUMERIC(10,2) NOT NULL,
    gold_value NUMERIC(15,2) NOT NULL,
    eligible_stone_value NUMERIC(15,2) DEFAULT 0.00,
    eligible_making_charges NUMERIC(15,2) DEFAULT 0.00,
    current_estimated_value NUMERIC(15,2) NOT NULL,
    insured_value NUMERIC(15,2) DEFAULT 0.00,
    coverage_gap NUMERIC(15,2) DEFAULT 0.00, -- current_estimated_value - insured_value (if positive)
    gap_percentage NUMERIC(5,2) DEFAULT 0.00,
    inflation_adjustment_rate NUMERIC(5,2) DEFAULT 0.00,
    calculated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_gold_rates_date ON gold_rates(effective_date);
CREATE INDEX idx_valuation_asset ON valuation_records(jewellery_asset_id);

