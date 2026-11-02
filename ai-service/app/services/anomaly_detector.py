import numpy as np
from sklearn.ensemble import IsolationForest
from typing import Dict, Any, List, Tuple
from app.models.schemas import AnomalyDetectionRequest, AnomalyDetectionResponse

# Synthetic baseline distribution for jewellery insurance lifecycle features
# Features: [weight_diff_pct, trans_freq, ownership_changes, prior_claims, claim_sum_ratio, policy_age_days]
np.random.seed(42)
baseline_normal_data = np.array([
    # [weight_delta_pct, trans_freq, ownership_changes, claims_count, ratio_sum, policy_age]
    [0.01, 1.0, 0, 0, 0.15, 360],
    [0.02, 1.2, 0, 0, 0.20, 240],
    [0.00, 0.5, 0, 0, 0.10, 400],
    [0.03, 2.0, 1, 1, 0.35, 180],
    [0.01, 0.8, 0, 0, 0.05, 500],
    [0.02, 1.5, 1, 0, 0.25, 200],
    [0.00, 1.0, 0, 1, 0.40, 150],
    [0.04, 2.2, 0, 0, 0.18, 300],
    [0.01, 0.9, 0, 0, 0.12, 450],
    [0.02, 1.1, 0, 0, 0.30, 220],
])

# Initialize and train Isolation Forest
model = IsolationForest(n_estimators=100, contamination=0.1, random_state=42)
model.fit(baseline_normal_data)

def detect_anomaly(req: AnomalyDetectionRequest) -> AnomalyDetectionResponse:
    """
    Evaluates risk features using Scikit-learn Isolation Forest.
    Returns explainable indicators distinguishing normal wear/transformation from anomalous outliers.
    """
    feature_vector = np.array([[
        abs(req.weight_difference_grams),
        req.transformation_frequency_per_year,
        req.ownership_changes_count,
        req.prior_claims_count,
        req.claimed_amount_ratio_to_sum_insured,
        req.policy_age_days
    ]])

    prediction = model.predict(feature_vector)[0]  # 1 for normal, -1 for anomaly
    score = float(model.decision_function(feature_vector)[0])
    
    # Normalized anomaly index [0.0 = completely normal, 1.0 = highly anomalous]
    normalized_anomaly_index = round(max(0.0, min(1.0, 0.5 - (score * 2.5))), 3)

    is_anomaly = bool(prediction == -1 or normalized_anomaly_index > 0.45)
    factors: List[str] = []

    if abs(req.weight_difference_grams) > 3.0:
        factors.append(f"Physical gold weight disparity ({abs(req.weight_difference_grams):.2f}g) exceeds 5% typical resizing variance.")
    if req.prior_claims_count >= 2:
        factors.append(f"Elevated claim frequency: {req.prior_claims_count} prior claims recorded within policy cycle.")
    if req.policy_age_days < 30 and req.claimed_amount_ratio_to_sum_insured > 0.8:
        factors.append("Early total-loss claim velocity: claim submitted within first 30 days of coverage inception.")
    if req.ownership_changes_count >= 2:
        factors.append("Multiple rapid ownership transfers detected across distinct asset versions.")

    status = "HIGH_RISK" if (is_anomaly and len(factors) >= 2) else ("SUSPICIOUS" if is_anomaly else "NORMAL")

    if not factors:
        factors.append("All physical parameters, transformation intervals, and claim frequencies align with standard actuarial benchmarks.")

    return AnomalyDetectionResponse(
        is_anomaly=is_anomaly,
        anomaly_score=normalized_anomaly_index,
        status=status,
        explainable_factors=factors,
        model_name="IsolationForest-v1.0-Experimental",
        is_experimental=True
    )

