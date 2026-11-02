export type UserRole = 'ROLE_CUSTOMER' | 'ROLE_JEWELLER' | 'ROLE_INSURANCE_OFFICER' | 'ROLE_ADMIN';

export interface User {
  id: number;
  userId: number;
  username: string;
  email: string;
  fullName: string;
  roles: UserRole[];
  customerId?: number;
  jewellerId?: number;
}

export interface Jeweller {
  id: number;
  userId: number;
  jewellerCode: string;
  businessName: string;
  licenseNumber: string;
  bisHallmarkCenterId?: string;
  certified: boolean;
  contactPerson?: string;
  phone?: string;
  email?: string;
  address?: string;
  city?: string;
  state?: string;
  rating?: number;
  verificationStatus: string;
}

export interface JewelleryAsset {
  id: number;
  assetId: string;
  customerId: number;
  customerName: string;
  name: string;
  category: string;
  purity: string;
  netGoldWeight: number;
  grossWeight: number;
  huid?: string;
  hallmarkStatus: string;
  purchaseDate?: string;
  purchasePrice?: number;
  appraisalValue?: number;
  currentEstimatedValue: number;
  primaryImageUrl: string;
  ownershipStatus: string;
  verificationStatus: 'PENDING_VERIFICATION' | 'VERIFIED' | 'REJECTED';
  verifiedByJewellerId?: number;
  verifiedByJewellerName?: string;
  verifiedAt?: string;
  currentVersion: number;
  registrationDate: string;
  versions?: AssetVersion[];
  transformations?: TransformationEvent[];
}

export interface AssetVersion {
  id: number;
  versionNumber: number;
  name: string;
  category: string;
  purity: string;
  netGoldWeight: number;
  grossWeight: number;
  huid?: string;
  hallmarkStatus?: string;
  estimatedValue?: number;
  primaryImageUrl?: string;
  changeReason?: string;
  createdBy?: string;
  createdAt: string;
}

export interface TransformationEvent {
  id: number;
  transformationType: string;
  previousVersion: number;
  newVersion: number;
  previousWeight: number;
  newWeight: number;
  previousPurity: string;
  newPurity: string;
  jewellerId?: number;
  jewellerName?: string;
  verificationStatus: string;
  description: string;
  evidenceDocumentUrl?: string;
  evidenceImageUrls?: string;
  createdBy: string;
  createdAt: string;
}

export interface CoverageDefinition {
  id: number;
  coverageCode: string;
  coverageName: string;
  description: string;
  coverageLimitPercentage: number;
  defaultDeductiblePercentage: number;
  premiumFactor: number;
  effectiveDate: string;
  enabled: boolean;
  mandatory?: boolean;
  defaultSelected?: boolean;
}

export interface InsuranceProduct {
  id: number;
  productCode: string;
  productName: string;
  description: string;
  baseRatePercentage: number;
  minSumInsured: number;
  maxSumInsured: number;
  tenureMonths: number;
  active: boolean;
  coverages: CoverageDefinition[];
}

export interface Quotation {
  id: number;
  quotationNumber: string;
  productId: number;
  productName: string;
  totalInsuredValue: number;
  deductiblePercentage: number;
  baseRate: number;
  basePremium: number;
  riskFactor: number;
  coverageFactor: number;
  locationFactor: number;
  annualPremium: number;
  monthlyEquivalent: number;
  selectedCoverages: string[];
  items: Array<{
    assetId: string;
    assetName: string;
    category: string;
    netGoldWeight: number;
    insuredValue: number;
    allocatedPremium: number;
  }>;
  validUntil: string;
  disclaimer: string;
}

export interface InsuranceApplication {
  id: number;
  applicationNumber: string;
  customerId: number;
  customerName: string;
  customerEmail: string;
  quotationNumber: string;
  productId: number;
  productName: string;
  totalInsuredValue: number;
  proposedPremium: number;
  status: 'SUBMITTED' | 'UNDERWRITING' | 'APPROVED' | 'REFERRED' | 'DECLINED' | 'REQUEST_INFORMATION';
  customerNotes?: string;
  underwritingDecision?: string;
  assignedOfficerId?: number;
  assignedOfficerName?: string;
  decisionDate?: string;
  createdAt: string;
  assessments?: UnderwritingAssessment[];
}

export interface UnderwritingAssessment {
  id: number;
  decision: string;
  jewelleryVerificationScore: number;
  appraisalValidityScore: number;
  customerRiskScore: number;
  aiRiskAssessmentScore: number;
  overallRiskScore: number;
  decisionReasons: string;
  stipulations?: string;
  assessedAt: string;
}

export interface InsurancePolicy {
  id: number;
  policyNumber: string;
  customerId: number;
  customerName: string;
  customerEmail: string;
  productId: number;
  productName: string;
  status: 'DRAFT' | 'QUOTED' | 'APPLICATION_SUBMITTED' | 'UNDERWRITING' | 'APPROVED' | 'ISSUED' | 'ACTIVE' | 'RENEWAL_PENDING' | 'RENEWED' | 'EXPIRED' | 'CANCELLED';
  totalSumInsured: number;
  annualPremium: number;
  paymentFrequency: string;
  effectiveDate: string;
  expirationDate: string;
  renewalCount: number;
  policyDocumentUrl: string;
  issuedAt: string;
  coveredAssets: Array<{
    assetId: string;
    assetName: string;
    category: string;
    purity: string;
    netGoldWeight: number;
    versionNumber: number;
    sumInsured: number;
    allocatedPremium: number;
    imageUrl?: string;
  }>;
  coverages: Array<{
    coverageCode: string;
    coverageName: string;
    coverageLimit: number;
    deductibleAmount: number;
    premiumCharge: number;
  }>;
}

export interface BillingAccount {
  id: number;
  accountNumber: string;
  customerId: number;
  customerName: string;
  currentBalance: number;
  totalBilled: number;
  totalPaid: number;
  status: string;
  invoices: Invoice[];
}

export interface Invoice {
  id: number;
  invoiceNumber: string;
  policyId?: number;
  policyNumber?: string;
  invoiceType: string;
  amount: number;
  taxAmount: number;
  totalAmount: number;
  dueDate: string;
  status: 'PENDING' | 'PAID' | 'OVERDUE' | 'CANCELLED';
  notes?: string;
  createdAt: string;
  payments?: Payment[];
}

export interface Payment {
  id: number;
  paymentReference: string;
  amountPaid: number;
  paymentMethod: string;
  transactionStatus: string;
  receiptUrl?: string;
  paymentDate: string;
}

export interface GoldRate {
  id: number;
  currency: string;
  rate24k: number;
  rate22k: number;
  rate18k: number;
  rate14k: number;
  source: string;
  effectiveDate: string;
  active: boolean;
}

export interface ValuationRecord {
  id: number;
  assetId: string;
  assetName: string;
  netGoldWeight: number;
  purity: string;
  applicableGoldRate: number;
  goldValue: number;
  eligibleStoneValue: number;
  eligibleMakingCharges: number;
  currentEstimatedValue: number;
  insuredValue: number;
  coverageGap: number;
  gapPercentage: number;
  inflationAdjustmentRate: number;
  gapIdentified: boolean;
  recommendation: string;
  calculatedAt: string;
}

export interface Claim {
  id: number;
  claimNumber: string;
  policyId: number;
  policyNumber: string;
  assetId: string;
  assetName: string;
  assetCategory: string;
  customerId: number;
  customerName: string;
  customerEmail: string;
  incidentType: string;
  incidentDate: string;
  incidentLocation: string;
  incidentDescription: string;
  policeReportNumber?: string;
  policeStation?: string;
  preferredJewellerId?: number;
  preferredJewellerName?: string;
  settlementPreference: string;
  claimedAmount: number;
  approvedAmount: number;
  deductibleAmount: number;
  netSettlementAmount: number;
  status: 'DRAFT' | 'SUBMITTED' | 'VALIDATION' | 'COVERAGE_CHECK' | 'AI_ASSESSMENT' | 'UNDER_REVIEW' | 'ADDITIONAL_INFORMATION_REQUIRED' | 'APPROVED' | 'REJECTED' | 'SETTLEMENT_PENDING' | 'SETTLED' | 'CLOSED';
  assignedOfficerId?: number;
  assignedOfficerName?: string;
  submissionDate: string;
  closureDate?: string;
  evidenceList: Array<{
    id: number;
    evidenceType: string;
    fileName: string;
    fileUrl: string;
    description?: string;
    uploadedBy: string;
    verified: boolean;
    uploadedAt: string;
  }>;
  assessment?: {
    id: number;
    policyValidityConfirmed: boolean;
    coverageConfirmed: boolean;
    applicableCoverageCode: string;
    deductiblePercentageApplied: number;
    identityConsistencyStatus: string;
    anomalyDetected: boolean;
    anomalyScore: number;
    aiConfidenceScore: number;
    recommendation: string;
    rationale: string;
    assessmentReportJson?: string;
    createdAt: string;
  };
  decision?: {
    id: number;
    decision: string;
    decisionRationale: string;
    settlementMethod?: string;
    finalSettlementAmount?: number;
    decisionDate: string;
    officerName?: string;
  };
}

export interface RepairOrder {
  id: number;
  orderNumber: string;
  claimId: number;
  claimNumber: string;
  assetId: string;
  assetName: string;
  jewellerId: number;
  jewellerName: string;
  damageDescription: string;
  repairScope: string;
  estimatedCost: number;
  approvedCost: number;
  status: 'ESTIMATE_PENDING' | 'ESTIMATE_SUBMITTED' | 'ESTIMATE_APPROVED' | 'IN_PROGRESS' | 'COMPLETED' | 'VERIFIED';
  jewellerNotes?: string;
  postRepairWeight?: number;
  postRepairImageUrl?: string;
  completedAt?: string;
  createdAt: string;
}

export interface NotificationItem {
  id: number;
  title: string;
  message: string;
  notificationType: string;
  read: boolean;
  actionUrl?: string;
  createdAt: string;
}

export interface AuditEvent {
  id: number;
  username: string;
  action: string;
  entityName: string;
  entityId: string;
  oldValue?: string;
  newValue?: string;
  ipAddress?: string;
  createdAt: string;
}
