import pytest
from fastapi.testclient import TestClient
from main import app

client = TestClient(app)

def test_health_check():
    response = client.get("/health")
    assert response.status_code == 200
    data = response.json()
    assert data["status"] == "UP"
    assert data["service"] == "GoldTrust AI Engine"
    assert data["experimental"] is True

def test_tage_assessment_consistent():
    payload = {
        "asset_id": "GT-JWL-2026-000001",
        "claim_id": "GT-CLM-2026-000001",
        "original_weight": 24.50,
        "current_weight": 24.45,
        "original_purity": "22K",
        "current_purity": "22K",
        "huid": "HUID982341",
        "transformation_count": 1,
        "policy_tenure_months": 12,
        "customer_claims_count": 0
    }
    response = client.post("/ai/tage/assess", json=payload)
    assert response.status_code == 200
    data = response.json()
    assert data["asset_id"] == "GT-JWL-2026-000001"
    assert data["status"] in ["CONSISTENT", "VERIFIED_TRANSFORMATION"]
    assert data["physical_consistency"] >= 90.0
    assert data["is_experimental"] is True
    assert "hallmark_huid_validity" in data["feature_attributions"]

def test_tage_assessment_anomaly():
    payload = {
        "asset_id": "GT-JWL-2026-000002",
        "claim_id": "GT-CLM-2026-000002",
        "original_weight": 50.00,
        "current_weight": 25.00,  # 50% mass discrepancy
        "original_purity": "22K",
        "current_purity": "18K",
        "huid": "",  # missing HUID
        "transformation_count": 15,
        "policy_tenure_months": 1,
        "customer_claims_count": 5
    }
    response = client.post("/ai/tage/assess", json=payload)
    assert response.status_code == 200
    data = response.json()
    assert data["status"] in ["IDENTITY_INCONSISTENCY", "REVIEW_REQUIRED"]
    assert data["physical_consistency"] < 75.0

def test_anomaly_detection():
    payload = {
        "weight_difference_grams": 0.05,
        "transformation_frequency_per_year": 1.0,
        "ownership_changes_count": 0,
        "prior_claims_count": 0,
        "claimed_amount_ratio_to_sum_insured": 0.2,
        "policy_age_days": 180
    }
    response = client.post("/ai/anomaly/detect", json=payload)
    assert response.status_code == 200
    data = response.json()
    assert "anomaly_score" in data
    assert "is_anomaly" in data

def test_explainability():
    response = client.get("/ai/explain/TAGE-TEST1234")
    assert response.status_code == 200
    data = response.json()
    assert data["assessment_reference"] == "TAGE-TEST1234"
    assert "feature_attributions" in data

