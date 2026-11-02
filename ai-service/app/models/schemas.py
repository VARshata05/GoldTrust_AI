from pydantic import BaseModel, Field
from typing import Optional, List, Dict, Any

class IdentityCompareRequest(BaseModel):
    asset_id: str
    original_image_url: Optional[str] = None
    current_image_url: Optional[str] = None
    original_image_base64: Optional[str] = None
    current_image_base64: Optional[str] = None

class IdentityCompareResponse(BaseModel):
    model_config = {'protected_namespaces': ()}
    asset_id: str
    similarity_score: float = Field(..., description="Visual contour and color histogram similarity [0.0 - 1.0]")
    confidence: str = Field(..., description="HIGH, MEDIUM, LOW")
    huid_detected: bool
    contour_match_score: float
    color_histogram_score: float
    model_version: str = "TAGE-Vision-v1.0"
    is_experimental: bool = True
    disclaimer: str = "Experimental OpenCV geometric contour & color histogram feature extraction. Not a certified biometric guarantee."

class TageAssessmentRequest(BaseModel):
    model_config = {'protected_namespaces': ()}
    asset_id: str
    claim_id: Optional[int] = None
    current_weight: float
    original_weight: float
    purity: str = "22K"
    huid: Optional[str] = None
    transformation_count: int = 0
    incident_type: Optional[str] = None
    customer_claims_count: int = 0
    policy_tenure_months: int = 12

class TageAssessmentResponse(BaseModel):
    model_config = {'protected_namespaces': ()}
    assessment_reference: str
    asset_id: str
    status: str  # CONSISTENT, VERIFIED_TRANSFORMATION, REVIEW_REQUIRED, IDENTITY_INCONSISTENCY, INSUFFICIENT_EVIDENCE
    visual_similarity: float
    physical_consistency: float
    transformation_consistency: float
    ownership_continuity: float
    evidence_completeness: float
    anomaly_score: float
    anomaly_detected: bool
    recommended_action: str
    explanation: str
    model_version: str = "1.0.0"
    is_experimental: bool = True
    feature_attributions: Dict[str, float]

class AnomalyDetectionRequest(BaseModel):
    model_config = {'protected_namespaces': ()}
    weight_difference_grams: float = 0.0
    transformation_frequency_per_year: float = 1.0
    ownership_changes_count: int = 0
    prior_claims_count: int = 0
    claimed_amount_ratio_to_sum_insured: float = 0.5
    policy_age_days: int = 90

class AnomalyDetectionResponse(BaseModel):
    model_config = {'protected_namespaces': ()}
    is_anomaly: bool
    anomaly_score: float
    status: str  # NORMAL, SUSPICIOUS, HIGH_RISK
    explainable_factors: List[str]
    model_name: str = "IsolationForest-Experimental"
    is_experimental: bool = True
