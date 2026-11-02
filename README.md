# GoldTrust AI – Guidewire-Aligned, Transformation-Aware Gold Jewellery Insurance Platform

**GoldTrust AI** is a standalone, enterprise-grade, full-stack gold jewellery insurance platform designed around core architectural concepts from Guidewire InsuranceSuite (PolicyCenter, ClaimCenter, BillingCenter), featuring:
- **Transformation-Aware Gold Evidence Engine (TAGE)**: Multi-evidence AI fusion for visual similarity, physical gold mass continuity, BIS hallmark (HUID) validation, and explainable claims management.
- **Dynamic Bullion Valuation & Inflation Protection**: Automated real-time valuation based on live bullion market gold rates (24K, 22K, 18K, 14K), with coverage gap detection and customer consent workflows for policy limit modifications.
- **Persistent Digital Asset Passports & Lifecycle Versioning**: Immutable version-controlled digital identities (e.g. `GT-JWL-2026-000001` v1, v2...) that preserve historical specs through resizing, redesign, repairs, and stone replacements without overwriting records.
- **Four Integrated Portals**: Customer Portal, Certified Jeweller Verification Workbench, Insurance Underwriter/Officer Console, and Platform Administrator Console.
- **Complete End-to-End Adjudication & Restoration Orchestration**: Claim submission, policy validity confirmation, coverage check, AI assessment, human officer authority, certified jeweller repair orders, and simulated cashless billing.

---

## 🏛️ Guidewire Architectural Alignment (Standalone)

GoldTrust AI is built from the ground up as a **completely standalone enterprise application** using **Java 21, Spring Boot 3, PostgreSQL, Python FastAPI, and React + TypeScript**. It does **NOT** require any Guidewire proprietary software, VM, or Cloud, but adopts Guidewire domain concepts:

| Guidewire Concept | GoldTrust AI Implementation |
|---|---|
| **PolicyCenter (PC)** | Products & Coverages Engine (`InsuranceProduct`, `CoverageDefinition`, 9 Core Coverages), Configurable Quotation Engine (`BaseRate * InsuredValue * RiskFactor * CoverageFactor * LocationFactor`), Underwriting Review Queue with decision authority (`APPROVE`, `REFER`, `DECLINE`, `REQUEST_INFORMATION`), Policy Lifecycle (`DRAFT` → `QUOTED` → `UNDERWRITING` → `APPROVED` → `ACTIVE` → `RENEWAL`), Endorsements & Multi-Asset Mapping. |
| **ClaimCenter (CC)** | Claim Lifecycle Workflow (`SUBMITTED` → `VALIDATION` → `COVERAGE_CHECK` → `AI_ASSESSMENT` → `UNDER_REVIEW` → `APPROVED` → `REPAIR/REPLACEMENT` → `SETTLED`), Financial Reserves & Incurred Total Tracking (`ClaimFinancials`, `ClaimReserves`), Dedicated Certified Repair Orders (`RepairOrder`) & Asset Restitution. |
| **BillingCenter (BC)** | Billing Accounts (`BillingAccount`), Invoice Schedules (`Invoice`), Simulated Cashless Clearing (`Payment`), Refund Tracking (`Refund`). |
| **Activities & Workflow** | System-wide Activity task dispatching (`Activity`) and user alert notification triggers (`Notification`). |
| **Audit & Governance** | Regulatory audit trail ledger (`AuditEvent`) logging user, action, entity, previous values, and IP addresses. |

---

## 💻 Technology Stack

- **Frontend**: React 18, TypeScript, Vite, Tailwind CSS, Lucide React, Recharts, Axios, React Router v6.
- **Backend**: Java 21, Spring Boot 3.3.4, Spring Security, JWT (JJWT 0.12.6), Spring Data JPA, Hibernate, PostgreSQL 18/16, Flyway Migrations (8 versioned SQL scripts), Bean Validation, SpringDoc OpenAPI (Swagger UI).
- **AI Service**: Python 3.12 / 3.13, FastAPI, Uvicorn, OpenCV (Structural Similarity & Contour Profiling), Scikit-learn (Isolation Forest Anomaly Detector), Pydantic v2.
- **Database**: PostgreSQL with relational foreign keys, unique asset identifiers, version history, and Flyway baseline migrations.
- **Infrastructure**: Docker & Docker Compose (`docker-compose.yml`), Multi-stage Dockerfiles.

---

## 🔐 Demo User Credentials (1-Click Switch Available)

GoldTrust AI includes pre-seeded demo accounts. In the UI top navigation bar, use the **1-Click Role Switcher** or log in with:

| Portal / Role | Username | Password | Role Description |
|---|---|---|---|
| **Customer Portal** | `customer` | `password123` | Priya Mehta – Holds registered gold assets (`GT-JWL-2026-000001`), active comprehensive policy (`GT-POL-2026-000001`), and sample damage claim. |
| **Jeweller Portal** | `jeweller` | `password123` | Suresh Verma (Tanishq Hallmark & Care Centre) – Verifies gold weight, BIS hallmark HUIDs, records physical transformations, and manages repair orders. |
| **Officer Portal** | `officer` | `password123` | Vikram Aditya – Underwriting Officer evaluating applications and adjudicating claims with explainable TAGE AI assistance. |
| **Admin Console** | `admin` | `password123` | Rajesh Sharma – Manages coverage definitions, bullion gold rates, products, and regulatory audit trails. |

---

## 🚀 Running the Platform Locally

### Prerequisites
- Java 21+ (`java -version`)
- Maven 3.8+ (`mvn -v`)
- Node.js 18+ & npm (`node -v`)
- PostgreSQL 14+ running on port 5432 (or Docker)
- Python 3.10+ (for AI service)

### 1. Database Setup
PostgreSQL database `goldtrust_db` on `localhost:5432` with username `postgres` and password `postgres`.
Spring Boot automatically runs the 8 Flyway migrations on startup:
```bash
# PostgreSQL CLI (if creating manually):
psql -U postgres -c "CREATE DATABASE goldtrust_db;"
```

### 2. Start Python AI Service
```bash
cd ai-service
pip install -r requirements.txt
python main.py
# Runs on http://localhost:8000 (Healthcheck: http://localhost:8000/health)
```

### 3. Start Spring Boot Backend
```bash
cd backend
mvn spring-boot:run
# Runs on http://localhost:8080 (Swagger UI: http://localhost:8080/swagger-ui.html)
```

### 4. Start React Frontend
```bash
cd frontend
npm install
npm run dev
# Open in browser: http://localhost:5173
```

---

## 🐳 Running with Docker Compose

To spin up the entire full-stack ecosystem (PostgreSQL, AI Service, Spring Boot Backend, and React Frontend) in one command:
```bash
docker-compose up --build
```
Access points:
- Frontend: `http://localhost:5173`
- Backend API & Swagger: `http://localhost:8080/swagger-ui.html`
- AI Microservice: `http://localhost:8000/docs`
- PostgreSQL: `localhost:5432` (database `goldtrust_db`)

---

## 🛡️ The 9 Configurable Jewellery Coverages

GoldTrust AI implements the 9 standard gold insurance coverages without hardcoding claims logic:
1. **Accidental Loss (`COV_ACC_LOSS`)**: Unexplained disappearance or accidental loss.
2. **Theft & Burglary (`COV_THEFT`)**: Full indemnity against theft supported by formal law enforcement incident reports.
3. **Accidental Damage (`COV_ACC_DAMAGE`)**: Physical deformation, fractures, clasp breaks, or stone dislodgement.
4. **Mysterious Disappearance (`COV_MYST_DIS`)**: Specialized coverage subject to TAGE evidence timeline analysis.
5. **Natural Disaster (`COV_NAT_DIS`)**: Flood, earthquake, storm, or catastrophic environmental damage.
6. **Worldwide Travel (`COV_WW_TRAVEL`)**: Extended worldwide coverage for up to 90 consecutive days.
7. **Preventive Maintenance (`COV_PREV_MAINT`)**: Complimentary ultrasonic cleaning and prong inspection at network jewellers.
8. **Certified Repair Service (`COV_REPAIR`)**: Cashless restoration and weight restitution at verified jeweller facilities.
9. **Equivalent Asset Replacement (`COV_REPLACEMENT`)**: Sourcing and delivery of identical purity and weight replacement assets.

---

## 🧠 TAGE (Transformation-Aware Gold Evidence Engine) Architecture

```
[ Customer Claim Evidence + Registered Jewellery Baseline ]
                         │
                         ▼
┌─────────────────────────────────────────────────────────┐
│               TAGE Multi-Evidence Fusion                │
├─────────────────────────┬───────────────────────────────┤
│ 1. Visual Similarity    │ OpenCV Contour Matching       │
│ 2. Physical Consistency │ Metallurgical Weight & Purity │
│ 3. Hallmark Integrity   │ BIS 6-Character HUID Registry │
│ 4. Lineage Continuity   │ Version Chain (v1 -> v2)      │
│ 5. Anomaly Detector     │ Scikit-learn Isolation Forest │
└─────────────────────────┴───────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────┐
│              Explainable Claim Assessment               │
│ - Status: CONSISTENT / VERIFIED_TRANSFORMATION          │
│ - Visual Match: 94.5%                                   │
│ - Physical Consistency: 98.2%                           │
│ - Anomaly Index: 0.04 (Normal)                          │
│ - AI Recommendation: APPROVE_REPAIR                     │
└─────────────────────────────────────────────────────────┘
                         │
                         ▼
      [ Insurance Officer Retains Full Authority ]
               (APPROVE / REJECT / REQUEST INFO)
```

---

## 📄 License & Academic Note
Developed as an enterprise software architecture demonstration of modern Guidewire-aligned insurance systems, featuring real-world gold assay tracking and explainable AI adjudication.
