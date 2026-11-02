import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import {
  Gem,
  ShieldCheck,
  FileCheck2,
  AlertTriangle,
  Receipt,
  ArrowUpRight,
  TrendingUp,
  PlusCircle,
  Calculator,
  ShieldAlert,
} from 'lucide-react';
import apiClient from '../../api/client';
import { JewelleryAsset, InsurancePolicy, Claim, BillingAccount } from '../../types';

export const CustomerDashboard: React.FC = () => {
  const [assets, setAssets] = useState<JewelleryAsset[]>([]);
  const [policies, setPolicies] = useState<InsurancePolicy[]>([]);
  const [claims, setClaims] = useState<Claim[]>([]);
  const [billing, setBilling] = useState<BillingAccount | null>(null);
  const [loading, setLoading] = useState<boolean>(true);

  useEffect(() => {
    loadDashboardData();
  }, []);

  const loadDashboardData = async () => {
    try {
      const [assetsRes, policiesRes, claimsRes, billingRes] = await Promise.allSettled([
        apiClient.get('/jewellery'),
        apiClient.get('/policies'),
        apiClient.get('/claims'),
        apiClient.get('/billing/accounts/me'),
      ]);

      if (assetsRes.status === 'fulfilled' && assetsRes.value.data?.data) {
        setAssets(assetsRes.value.data.data);
      }
      if (policiesRes.status === 'fulfilled' && policiesRes.value.data?.data) {
        setPolicies(policiesRes.value.data.data);
      }
      if (claimsRes.status === 'fulfilled' && claimsRes.value.data?.data) {
        setClaims(claimsRes.value.data.data);
      }
      if (billingRes.status === 'fulfilled' && billingRes.value.data?.data) {
        setBilling(billingRes.value.data.data);
      }
    } catch (e) {
      console.error(e);
    } finally {
      setLoading(false);
    }
  };

  const totalPortfolioValue = assets.reduce((sum, a) => sum + (a.currentEstimatedValue || 0), 0);
  const totalInsuredSum = policies.reduce((sum, p) => sum + (p.totalSumInsured || 0), 0);
  const coverageGap = Math.max(0, totalPortfolioValue - totalInsuredSum);

  return (
    <div className="space-y-6">
      {/* Welcome Banner */}
      <div className="bg-gradient-to-r from-slate-900 via-slate-800 to-amber-950/40 p-6 rounded-2xl border border-amber-500/20 shadow-xl relative overflow-hidden">
        <div className="relative z-10 flex flex-col md:flex-row md:items-center justify-between gap-4">
          <div>
            <div className="inline-flex items-center space-x-2 px-2.5 py-1 rounded-full bg-amber-500/10 border border-amber-500/30 text-amber-400 text-xs font-semibold mb-2">
              <ShieldCheck className="w-3.5 h-3.5" />
              <span>Transformation-Aware Asset Protection</span>
            </div>
            <h1 className="text-2xl lg:text-3xl font-bold text-white tracking-tight">
              Customer Jewellery Portfolio
            </h1>
            <p className="text-slate-400 text-xs mt-1 max-w-xl">
              Real-time bullion-indexed gold valuation, digital passport identity verification, and Guidewire-aligned comprehensive policy management.
            </p>
          </div>
          <div className="flex items-center space-x-3">
            <Link
              to="/customer/register-jewellery"
              className="inline-flex items-center space-x-2 bg-gradient-to-r from-amber-500 to-yellow-500 hover:from-amber-600 hover:to-yellow-600 text-slate-950 px-4 py-2.5 rounded-xl text-xs font-bold shadow-lg shadow-amber-500/20 transition-all"
            >
              <PlusCircle className="w-4 h-4" />
              <span>Register Jewellery</span>
            </Link>
            <Link
              to="/customer/quotation"
              className="inline-flex items-center space-x-2 bg-slate-800 hover:bg-slate-700 text-white border border-slate-700 px-4 py-2.5 rounded-xl text-xs font-semibold transition-all"
            >
              <Calculator className="w-4 h-4 text-amber-400" />
              <span>Get Quotation</span>
            </Link>
          </div>
        </div>
      </div>

      {/* Coverage Gap Warning Card (Requirement Section 12) */}
      {coverageGap > 0 && (
        <div className="bg-gradient-to-r from-amber-950/30 via-slate-900 to-slate-900 border border-amber-500/40 p-4 rounded-xl flex items-start justify-between gap-4">
          <div className="flex items-start space-x-3">
            <AlertTriangle className="w-5 h-5 text-amber-400 shrink-0 mt-0.5" />
            <div>
              <h4 className="text-xs font-bold text-amber-400 uppercase tracking-wider">Dynamic Valuation Surge & Coverage Gap</h4>
              <p className="text-xs text-slate-300 mt-1">
                Live gold rates have increased your portfolio replacement value by <strong className="text-white">₹{coverageGap.toLocaleString()}</strong> above your active policy limits.
              </p>
              <p className="text-[11px] text-slate-400 mt-0.5">
                Underinsurance protection notice: Limits require your formal consent before any policy modification.
              </p>
            </div>
          </div>
          <Link
            to="/customer/quotation"
            className="text-xs font-semibold px-3 py-1.5 bg-amber-500/20 hover:bg-amber-500/30 text-amber-300 border border-amber-500/40 rounded-lg whitespace-nowrap"
          >
            Review Limits
          </Link>
        </div>
      )}

      {/* Top Portfolio Metrics Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        {/* Total Assets Value */}
        <div className="bg-slate-900/90 border border-slate-800 p-5 rounded-xl">
          <div className="flex items-center justify-between">
            <span className="text-xs font-semibold text-slate-400">Total Portfolio Value</span>
            <div className="p-2 rounded-lg bg-amber-500/10 text-amber-400">
              <Gem className="w-4 h-4" />
            </div>
          </div>
          <p className="text-2xl font-bold text-white mt-2">₹{totalPortfolioValue.toLocaleString()}</p>
          <div className="flex items-center space-x-1.5 text-[11px] text-emerald-400 mt-1">
            <TrendingUp className="w-3.5 h-3.5" />
            <span>{assets.length} registered gold items</span>
          </div>
        </div>

        {/* Sum Insured */}
        <div className="bg-slate-900/90 border border-slate-800 p-5 rounded-xl">
          <div className="flex items-center justify-between">
            <span className="text-xs font-semibold text-slate-400">Active Sum Insured</span>
            <div className="p-2 rounded-lg bg-emerald-500/10 text-emerald-400">
              <FileCheck2 className="w-4 h-4" />
            </div>
          </div>
          <p className="text-2xl font-bold text-white mt-2">₹{totalInsuredSum.toLocaleString()}</p>
          <p className="text-[11px] text-slate-400 mt-1">{policies.length} active policy contracts</p>
        </div>

        {/* Outstanding Billing */}
        <div className="bg-slate-900/90 border border-slate-800 p-5 rounded-xl">
          <div className="flex items-center justify-between">
            <span className="text-xs font-semibold text-slate-400">Outstanding Balance</span>
            <div className="p-2 rounded-lg bg-blue-500/10 text-blue-400">
              <Receipt className="w-4 h-4" />
            </div>
          </div>
          <p className="text-2xl font-bold text-white mt-2">
            ₹{billing ? billing.currentBalance.toLocaleString() : '0'}
          </p>
          <p className="text-[11px] text-slate-400 mt-1">Account: {billing?.accountNumber || 'GT-BIL-2026-000001'}</p>
        </div>

        {/* Claims Under Adjudication */}
        <div className="bg-slate-900/90 border border-slate-800 p-5 rounded-xl">
          <div className="flex items-center justify-between">
            <span className="text-xs font-semibold text-slate-400">Claims Registered</span>
            <div className="p-2 rounded-lg bg-purple-500/10 text-purple-400">
              <ShieldAlert className="w-4 h-4" />
            </div>
          </div>
          <p className="text-2xl font-bold text-white mt-2">{claims.length}</p>
          <p className="text-[11px] text-slate-400 mt-1">
            {claims.filter(c => c.status === 'UNDER_REVIEW' || c.status === 'APPROVED').length} active workflows
          </p>
        </div>
      </div>

      {/* Main Sections: Registered Assets & Recent Claims */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Jewellery Passport Overview */}
        <div className="lg:col-span-2 bg-slate-900/90 border border-slate-800 rounded-2xl p-6">
          <div className="flex items-center justify-between mb-4">
            <div>
              <h3 className="text-base font-bold text-white">Registered Jewellery Assets</h3>
              <p className="text-xs text-slate-400">Digital identities with version-controlled transformation histories</p>
            </div>
            <Link to="/customer/jewellery" className="text-xs font-semibold text-amber-400 hover:text-amber-300 flex items-center space-x-1">
              <span>View All</span>
              <ArrowUpRight className="w-3.5 h-3.5" />
            </Link>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            {assets.slice(0, 4).map((asset) => (
              <Link
                key={asset.id}
                to={`/customer/jewellery/${asset.assetId}`}
                className="bg-slate-950/60 border border-slate-800/80 rounded-xl p-3.5 hover:border-amber-500/50 hover:bg-slate-950/90 transition-all group flex flex-col justify-between"
              >
                <div>
                  <div className="relative h-36 w-full rounded-lg overflow-hidden mb-3 bg-slate-900">
                    <img
                      src={asset.primaryImageUrl}
                      alt={asset.name}
                      className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300"
                    />
                    <div className="absolute top-2 right-2 bg-slate-950/80 backdrop-blur px-2 py-0.5 rounded text-[10px] font-mono font-bold text-amber-400 border border-amber-500/30">
                      v{asset.currentVersion}
                    </div>
                    {asset.huid && (
                      <div className="absolute bottom-2 left-2 bg-slate-950/80 backdrop-blur px-2 py-0.5 rounded text-[10px] font-mono text-emerald-400 border border-emerald-500/30">
                        HUID: {asset.huid}
                      </div>
                    )}
                  </div>

                  <h4 className="text-xs font-bold text-white group-hover:text-amber-400 transition-colors line-clamp-1">
                    {asset.name}
                  </h4>
                  <div className="flex items-center justify-between text-[11px] text-slate-400 mt-1">
                    <span>{asset.category} • {asset.purity}</span>
                    <span>{asset.netGoldWeight}g Net</span>
                  </div>
                </div>

                <div className="mt-3 pt-2 border-t border-slate-800/60 flex items-center justify-between">
                  <span className="text-[10px] text-slate-500 uppercase tracking-wider font-mono">{asset.assetId}</span>
                  <span className="text-xs font-bold text-amber-300">₹{asset.currentEstimatedValue?.toLocaleString()}</span>
                </div>
              </Link>
            ))}
          </div>
        </div>

        {/* Recent Policies and Claims Sidebar Card */}
        <div className="space-y-6">
          {/* Active Policies */}
          <div className="bg-slate-900/90 border border-slate-800 rounded-2xl p-5">
            <div className="flex items-center justify-between mb-3">
              <h3 className="text-sm font-bold text-white">Active Insurance Policies</h3>
              <Link to="/customer/policies" className="text-[11px] text-amber-400 font-semibold hover:underline">
                Manage
              </Link>
            </div>
            <div className="space-y-3">
              {policies.length === 0 ? (
                <p className="text-xs text-slate-400 py-4 text-center">No active policies yet.</p>
              ) : (
                policies.slice(0, 3).map((policy) => (
                  <div key={policy.id} className="bg-slate-950/60 border border-slate-800 p-3 rounded-xl">
                    <div className="flex items-center justify-between">
                      <span className="text-xs font-mono font-bold text-white">{policy.policyNumber}</span>
                      <span className="text-[10px] px-2 py-0.5 rounded-full font-semibold bg-emerald-500/10 text-emerald-400 border border-emerald-500/30">
                        {policy.status}
                      </span>
                    </div>
                    <p className="text-xs text-slate-300 mt-1">{policy.productName}</p>
                    <div className="flex items-center justify-between text-[11px] text-slate-400 mt-2 pt-2 border-t border-slate-800/60">
                      <span>Sum Insured: ₹{policy.totalSumInsured.toLocaleString()}</span>
                      <span className="text-amber-400 font-medium">₹{policy.annualPremium.toLocaleString()}/yr</span>
                    </div>
                  </div>
                ))
              )}
            </div>
          </div>

          {/* Quick Actions Panel */}
          <div className="bg-gradient-to-br from-slate-900 to-slate-950 border border-slate-800 rounded-2xl p-5">
            <h3 className="text-xs font-bold text-slate-300 uppercase tracking-wider mb-3">Quick Actions</h3>
            <div className="grid grid-cols-2 gap-2">
              <Link
                to="/customer/quotation"
                className="p-3 bg-slate-800/50 hover:bg-slate-800 border border-slate-700/60 rounded-xl text-center transition-all group"
              >
                <Calculator className="w-5 h-5 mx-auto text-amber-400 group-hover:scale-110 transition-transform mb-1" />
                <span className="text-xs font-semibold text-slate-200 block">Calculate Quote</span>
              </Link>
              <Link
                to="/customer/submit-claim"
                className="p-3 bg-slate-800/50 hover:bg-slate-800 border border-slate-700/60 rounded-xl text-center transition-all group"
              >
                <AlertTriangle className="w-5 h-5 mx-auto text-red-400 group-hover:scale-110 transition-transform mb-1" />
                <span className="text-xs font-semibold text-slate-200 block">File Claim</span>
              </Link>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

