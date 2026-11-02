-- V2: Jewellery Assets and Transformation-Aware Lifecycle Management

CREATE TABLE jewellery_assets (
    id BIGSERIAL PRIMARY KEY,
    asset_id VARCHAR(50) NOT NULL UNIQUE,
    customer_id BIGINT NOT NULL REFERENCES customers(id) ON DELETE RESTRICT,
    name VARCHAR(200) NOT NULL,
    category VARCHAR(50) NOT NULL,
    purity VARCHAR(20) NOT NULL, -- 24K, 22K, 18K, 14K
    net_gold_weight NUMERIC(10,3) NOT NULL, -- grams
    gross_weight NUMERIC(10,3) NOT NULL, -- grams
    huid VARCHAR(50), -- Hallmark Unique Identification (6-digit alphanumeric)
    hallmark_status VARCHAR(50) DEFAULT 'VERIFIED',
    purchase_date DATE,
    purchase_price NUMERIC(15,2),
    appraisal_value NUMERIC(15,2),
    current_estimated_value NUMERIC(15,2),
    primary_image_url VARCHAR(500),
    ownership_status VARCHAR(50) DEFAULT 'OWNED',
    verification_status VARCHAR(50) DEFAULT 'PENDING_VERIFICATION', -- PENDING_VERIFICATION, VERIFIED, REJECTED
    verified_by_jeweller_id BIGINT REFERENCES jewellers(id),
    verified_at TIMESTAMP WITH TIME ZONE,
    current_version INT NOT NULL DEFAULT 1,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    registration_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE asset_versions (
    id BIGSERIAL PRIMARY KEY,
    jewellery_asset_id BIGINT NOT NULL REFERENCES jewellery_assets(id) ON DELETE CASCADE,
    version_number INT NOT NULL,
    name VARCHAR(200) NOT NULL,
    category VARCHAR(50) NOT NULL,
    purity VARCHAR(20) NOT NULL,
    net_gold_weight NUMERIC(10,3) NOT NULL,
    gross_weight NUMERIC(10,3) NOT NULL,
    huid VARCHAR(50),
    hallmark_status VARCHAR(50),
    estimated_value NUMERIC(15,2),
    primary_image_url VARCHAR(500),
    change_reason VARCHAR(255),
    created_by VARCHAR(100),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_asset_version UNIQUE (jewellery_asset_id, version_number)
);

CREATE TABLE jewellery_images (
    id BIGSERIAL PRIMARY KEY,
    jewellery_asset_id BIGINT NOT NULL REFERENCES jewellery_assets(id) ON DELETE CASCADE,
    version_number INT NOT NULL DEFAULT 1,
    image_type VARCHAR(50) DEFAULT 'PRIMARY', -- PRIMARY, FRONT, BACK, HALLMARK, MACRO, DAMAGE
    file_path VARCHAR(500) NOT NULL,
    file_url VARCHAR(500) NOT NULL,
    original_filename VARCHAR(255),
    file_size_bytes BIGINT,
    mime_type VARCHAR(100),
    ai_features_extracted BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE transformation_events (
    id BIGSERIAL PRIMARY KEY,
    jewellery_asset_id BIGINT NOT NULL REFERENCES jewellery_assets(id) ON DELETE CASCADE,
    transformation_type VARCHAR(50) NOT NULL, -- PURCHASE, REGISTRATION, VERIFICATION, REPAIR, RESIZING, REDESIGN, WEIGHT_MODIFICATION, STONE_REPLACEMENT, COMPONENT_REPLACEMENT, OWNERSHIP_TRANSFER, INHERITANCE, APPRAISAL, INSURANCE_UPDATE
    previous_version INT NOT NULL,
    new_version INT NOT NULL,
    previous_weight NUMERIC(10,3),
    new_weight NUMERIC(10,3),
    previous_purity VARCHAR(20),
    new_purity VARCHAR(20),
    jeweller_id BIGINT REFERENCES jewellers(id),
    verification_status VARCHAR(50) DEFAULT 'VERIFIED',
    description TEXT,
    evidence_document_url VARCHAR(500),
    evidence_image_urls TEXT, -- JSON array of URLs
    created_by VARCHAR(100),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE ownership_events (
    id BIGSERIAL PRIMARY KEY,
    jewellery_asset_id BIGINT NOT NULL REFERENCES jewellery_assets(id) ON DELETE CASCADE,
    previous_customer_id BIGINT REFERENCES customers(id),
    new_customer_id BIGINT NOT NULL REFERENCES customers(id),
    transfer_type VARCHAR(50) NOT NULL, -- PURCHASE, GIFT, INHERITANCE, PRIVATE_SALE
    legal_document_ref VARCHAR(100),
    transfer_date DATE NOT NULL,
    notes TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_jewellery_asset_id ON jewellery_assets(asset_id);
CREATE INDEX idx_jewellery_customer_id ON jewellery_assets(customer_id);
CREATE INDEX idx_jewellery_huid ON jewellery_assets(huid);
CREATE INDEX idx_asset_versions_asset ON asset_versions(jewellery_asset_id);
CREATE INDEX idx_transformation_events_asset ON transformation_events(jewellery_asset_id);

