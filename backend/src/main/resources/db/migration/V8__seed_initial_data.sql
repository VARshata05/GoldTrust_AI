-- V8: Seed System Master Reference Data (Roles, Products, Coverages, Gold Rates)

-- 1. Roles
INSERT INTO roles (name, description) VALUES
('ROLE_CUSTOMER', 'Individual customer holding jewellery assets and insurance policies'),
('ROLE_JEWELLER', 'Certified jewellery appraiser, verification centre, and repair specialist'),
('ROLE_INSURANCE_OFFICER', 'Underwriter and claims assessor with adjudication authority'),
('ROLE_ADMIN', 'Platform administrator managing products, rates, users, and audit')
ON CONFLICT (name) DO NOTHING;

-- 2. Insurance Products
INSERT INTO insurance_products (product_code, product_name, description, base_rate_percentage, min_sum_insured, max_sum_insured, tenure_months, is_active) VALUES
('GSC-01', 'GoldShield Comprehensive All-Risk', 'Enterprise comprehensive protection covering loss, theft, damage, travel, and certified repairs with transformation tracking.', 0.0150, 25000.00, 10000000.00, 12, TRUE),
('GTH-02', 'GoldTrust Heirloom & High-Value Heritage', 'Specialized high-value jewellery coverage featuring dynamic inflation adjustment, stone preservation, and priority claims appraisal.', 0.0185, 100000.00, 50000000.00, 12, TRUE)
ON CONFLICT (product_code) DO NOTHING;

-- 3. The 9 Required Coverages (Section 5 of specification)
INSERT INTO coverage_definitions (coverage_code, coverage_name, description, coverage_limit_percentage, default_deductible_percentage, premium_factor, is_enabled) VALUES
('COV_ACC_LOSS', 'Accidental Loss', 'Covers unexplained disappearance or misplacement under accidental circumstances.', 100.00, 5.00, 0.0030, TRUE),
('COV_THEFT', 'Theft & Burglary', 'Full indemnity against theft, robbery, or burglary supported by formal law enforcement incident report.', 100.00, 5.00, 0.0035, TRUE),
('COV_ACC_DAMAGE', 'Accidental Damage', 'Covers physical deformation, fractures, stone dislodgement, or clasps failure.', 100.00, 2.50, 0.0020, TRUE),
('COV_MYST_DIS', 'Mysterious Disappearance', 'Specialized coverage for sudden unexplainable loss subject to TAGE evidence review.', 80.00, 10.00, 0.0025, TRUE),
('COV_NAT_DIS', 'Natural Disaster', 'Protects against flood, earthquake, storm, or catastrophic environmental damage.', 100.00, 5.00, 0.0015, TRUE),
('COV_WW_TRAVEL', 'Worldwide Travel Extension', 'Extends coverage globally for up to 90 consecutive days outside primary residence.', 100.00, 5.00, 0.0020, TRUE),
('COV_PREV_MAINT', 'Preventive Maintenance', 'Annual complimentary cleaning, prong inspection, and ultrasonic restoration at certified jewellers.', 10.00, 0.00, 0.0005, TRUE),
('COV_REPAIR', 'Certified Repair Service', 'Cashless restoration and weight restitution at verified network jeweller facilities.', 100.00, 2.50, 0.0020, TRUE),
('COV_REPLACEMENT', 'Equivalent Asset Replacement', 'Provides identical purity, weight, and design replacement if damaged beyond repair.', 100.00, 5.00, 0.0030, TRUE)
ON CONFLICT (coverage_code) DO NOTHING;

-- 4. Map Coverages to Products
INSERT INTO product_coverages (product_id, coverage_id, is_mandatory, is_default_selected, display_order)
SELECT p.id, c.id, 
       CASE WHEN c.coverage_code IN ('COV_ACC_LOSS', 'COV_THEFT', 'COV_ACC_DAMAGE') THEN TRUE ELSE FALSE END,
       TRUE,
       ROW_NUMBER() OVER (ORDER BY c.id)
FROM insurance_products p
CROSS JOIN coverage_definitions c
ON CONFLICT DO NOTHING;

-- 5. Live Gold Rates Baseline
INSERT INTO gold_rates (currency, rate_24k, rate_22k, rate_18k, rate_14k, source, effective_date, is_active) VALUES
('INR', 7850.00, 7195.00, 5888.00, 4580.00, 'INDIA_BULLION_AND_JEWELLERS_ASSN', CURRENT_DATE, TRUE);

