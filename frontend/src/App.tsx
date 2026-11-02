import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import { Layout } from './components/layout/Layout';
import { Login } from './pages/auth/Login';

// Customer Pages
import { CustomerDashboard } from './pages/customer/CustomerDashboard';
import { JewelleryList } from './pages/customer/JewelleryList';
import { JewelleryDetail } from './pages/customer/JewelleryDetail';
import { RegisterJewellery } from './pages/customer/RegisterJewellery';
import { QuotationCalculator } from './pages/customer/QuotationCalculator';
import { PolicyList } from './pages/customer/PolicyList';
import { BillingPayments } from './pages/customer/BillingPayments';
import { SubmitClaim } from './pages/customer/SubmitClaim';
import { ClaimsList } from './pages/customer/ClaimsList';
import { ClaimDetail } from './pages/customer/ClaimDetail';

// Jeweller Pages
import { JewellerDashboard } from './pages/jeweller/JewellerDashboard';
import { VerificationQueue } from './pages/jeweller/VerificationQueue';
import { RepairOrders } from './pages/jeweller/RepairOrders';

// Officer Pages
import { OfficerDashboard } from './pages/officer/OfficerDashboard';
import { UnderwritingQueue } from './pages/officer/UnderwritingQueue';
import { ClaimsAdjudication } from './pages/officer/ClaimsAdjudication';

// Admin Pages
import { AdminDashboard } from './pages/admin/AdminDashboard';
import { ProductsConfig } from './pages/admin/ProductsConfig';
import { AuditLogs } from './pages/admin/AuditLogs';

const RoleBasedRedirect: React.FC = () => {
  const { role, token, isLoading } = useAuth();

  if (isLoading) return null;
  if (!token) return <Navigate to="/login" replace />;

  if (role === 'ROLE_JEWELLER') return <Navigate to="/jeweller/dashboard" replace />;
  if (role === 'ROLE_INSURANCE_OFFICER') return <Navigate to="/officer/dashboard" replace />;
  if (role === 'ROLE_ADMIN') return <Navigate to="/admin/dashboard" replace />;
  return <Navigate to="/customer/dashboard" replace />;
};

const ProtectedRoute: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const { token, isLoading } = useAuth();
  if (isLoading) return null;
  if (!token) return <Navigate to="/login" replace />;
  return <>{children}</>;
};

export const App: React.FC = () => {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/login" element={<Login />} />

          <Route
            path="/"
            element={
              <ProtectedRoute>
                <Layout />
              </ProtectedRoute>
            }
          >
            <Route index element={<RoleBasedRedirect />} />

            {/* Customer Routes */}
            <Route path="customer/dashboard" element={<CustomerDashboard />} />
            <Route path="customer/jewellery" element={<JewelleryList />} />
            <Route path="customer/jewellery/:assetId" element={<JewelleryDetail />} />
            <Route path="customer/register-jewellery" element={<RegisterJewellery />} />
            <Route path="customer/quotation" element={<QuotationCalculator />} />
            <Route path="customer/policies" element={<PolicyList />} />
            <Route path="customer/billing" element={<BillingPayments />} />
            <Route path="customer/submit-claim" element={<SubmitClaim />} />
            <Route path="customer/claims" element={<ClaimsList />} />
            <Route path="customer/claims/:claimNumber" element={<ClaimDetail />} />

            {/* Jeweller Routes */}
            <Route path="jeweller/dashboard" element={<JewellerDashboard />} />
            <Route path="jeweller/verifications" element={<VerificationQueue />} />
            <Route path="jeweller/repairs" element={<RepairOrders />} />

            {/* Officer Routes */}
            <Route path="officer/dashboard" element={<OfficerDashboard />} />
            <Route path="officer/underwriting" element={<UnderwritingQueue />} />
            <Route path="officer/claims" element={<ClaimsAdjudication />} />
            <Route path="officer/policies" element={<PolicyList />} />

            {/* Admin Routes */}
            <Route path="admin/dashboard" element={<AdminDashboard />} />
            <Route path="admin/products" element={<ProductsConfig />} />
            <Route path="admin/rates" element={<AdminDashboard />} />
            <Route path="admin/audit" element={<AuditLogs />} />
          </Route>

          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
};

export default App;
