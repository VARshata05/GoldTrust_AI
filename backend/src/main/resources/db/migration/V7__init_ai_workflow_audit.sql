-- V7: AI Assessments, Guidewire Activities/Workflow, Notifications, and Audit Trails

CREATE TABLE ai_assessments (
    id BIGSERIAL PRIMARY KEY,
    assessment_reference VARCHAR(50) NOT NULL UNIQUE,
    target_entity_type VARCHAR(50) NOT NULL, -- JEWELLERY, CLAIM, UNDERWRITING
    target_entity_id BIGINT NOT NULL,
    model_name VARCHAR(100) NOT NULL DEFAULT 'TAGE-v1.0-Experimental',
    model_version VARCHAR(50) NOT NULL DEFAULT '1.0.0',
    is_experimental BOOLEAN NOT NULL DEFAULT TRUE,
    visual_similarity_score NUMERIC(5,2),
    physical_consistency_score NUMERIC(5,2),
    transformation_consistency_score NUMERIC(5,2),
    ownership_continuity_score NUMERIC(5,2),
    evidence_completeness_score NUMERIC(5,2),
    anomaly_score NUMERIC(5,2),
    anomaly_detected BOOLEAN DEFAULT FALSE,
    recommendation VARCHAR(50) NOT NULL, -- CONSISTENT, VERIFIED_TRANSFORMATION, REVIEW_REQUIRED, IDENTITY_INCONSISTENCY, INSUFFICIENT_EVIDENCE
    explanation TEXT NOT NULL,
    features_breakdown_json TEXT,
    shap_contributions_json TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE activities (
    id BIGSERIAL PRIMARY KEY,
    activity_number VARCHAR(50) NOT NULL UNIQUE,
    subject VARCHAR(200) NOT NULL,
    description TEXT,
    activity_type VARCHAR(50) NOT NULL, -- UNDERWRITING_TASK, CLAIM_TASK, VALUATION_ALERT, REPAIR_APPROVAL, POLICY_RENEWAL
    priority VARCHAR(20) NOT NULL DEFAULT 'MEDIUM', -- LOW, MEDIUM, HIGH, URGENT
    status VARCHAR(30) NOT NULL DEFAULT 'OPEN', -- OPEN, IN_PROGRESS, COMPLETED, CANCELLED
    assigned_role VARCHAR(50), -- INSURANCE_OFFICER, JEWELLER, ADMIN
    assigned_user_id BIGINT REFERENCES users(id),
    related_entity_type VARCHAR(50), -- CLAIM, POLICY, JEWELLERY, APPLICATION
    related_entity_id BIGINT,
    due_date TIMESTAMP WITH TIME ZONE,
    completed_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE notifications (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title VARCHAR(200) NOT NULL,
    message TEXT NOT NULL,
    notification_type VARCHAR(50) NOT NULL, -- COVERAGE_GAP, UNDERWRITING_UPDATE, CLAIM_STATUS, PAYMENT_DUE, REPAIR_UPDATE, SYSTEM
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    action_url VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE audit_events (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    username VARCHAR(100),
    action VARCHAR(100) NOT NULL, -- CREATE, UPDATE, DELETE, APPROVE, REJECT, LOGIN
    entity_name VARCHAR(100) NOT NULL,
    entity_id VARCHAR(100),
    old_value TEXT,
    new_value TEXT,
    ip_address VARCHAR(50),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_ai_assessments_target ON ai_assessments(target_entity_type, target_entity_id);
CREATE INDEX idx_activities_status ON activities(status);
CREATE INDEX idx_activities_assigned ON activities(assigned_user_id);
CREATE INDEX idx_notifications_user ON notifications(user_id, is_read);
CREATE INDEX idx_audit_events_entity ON audit_events(entity_name, entity_id);

