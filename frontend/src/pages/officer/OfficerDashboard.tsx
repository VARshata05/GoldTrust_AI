import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { ClipboardList, ShieldAlert, FileCheck2, UserCheck, TrendingUp, Sparkles, CheckCircle2 } from 'lucide-react';
import apiClient from '../../api/client';
import { InsuranceApplication, Claim, InsurancePolicy } from '../../types';

export const OfficerDashboard: React.FC = () => {
  const [applications, setApplications] = useState<InsuranceApplication[]>([]);
  const [claims, setClaims] = useState<Claim[]>([]);
  const [policies, setPolicies] = useState<InsurancePolicy[]>([]);
  const [loading, setLoading] = useState<boolean>(true);

  useEffect(() => {
    loadOfficerData();
  }, []);

  const loadOfficerData = async () => {
    try {
      const [appRes, claimRes, polRes] = await Promise.allSettled([
        apiClient.get('/applications'),
        apiClient.get('/claims'),
        apiClient.get('/policies'),
      ]);

      if (appRes.status === 'fulfilled' && appRes.value.data?.data) {
        setApplications(appRes.value.data.data);
      }
      if (claimRes.status === 'fulfilled' && claimRes.value.data?.data) {
        setClaims(claimRes.value.data.data);
      }
      if (polRes.status === 'fulfilled' && polRes.value.data?.data) {
        setPolicies(polRes.value.data.data);
      }
    } catch (e) {
      console.error(e);
    } finally {
      setLoading(false);
    }
  };

  const pendingApps = applications.filter(a => a.status === 'UNDERWRITING' || a.status === 'SUBMITTED');
  const pendingClaims = claims.filter(c => c.status === 'UNDER_REVIEW' || c.status === 'SUBMITTED');

  return (
    <div className="space-y-6">
      {/* Welcome Banner */}
      <div className="bg-gradient-to-r from-slate-900 via-slate-900 to-indigo-950/40 p-6 rounded-2xl border border-indigo-500/30 shadow-xl flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <div className="inline-flex items-center space-x-1.5 px-2.5 py-0.5 rounded-full bg-indigo-500/10 border border-indigo-500/30 text-indigo-400 text-xs font-semibold mb-2">
            <UserCheck className="w-3.5 h-3.5" />
            <span>Underwriting & Adjudication Authority</span>
          </div>
          <h1 className="text-2xl font-bold text-white tracking-tight">Insurance Officer Console</h1>
          <p className="text-xs text-slate-400 mt-1">
            Guidewire-aligned PolicyCenter underwriting rules, explainable TAGE AI adjudication, and binding policy issuance.
          </p>
        </div>

        <div className="flex items-center space-x-3">
          <Link
            to="/officer/underwriting"
            className="px-4 py-2.5 bg-gradient-to-r from-amber-500 to-yellow-500 text-slate-950 font-bold rounded-xl text-xs shadow-lg shadow-amber-500/20"
          >
            Review Applications ({pendingApps.length})
          </Link>
          <Link
            to="/officer/claims"
            className="px-4 py-2.5 bg-slate-800 hover:bg-slate-700 text-white font-semibold rounded-xl text-xs border border-slate-700"
          >
            Adjudicate Claims ({pendingClaims.length})
          </Link>
        </div>
      </div>

      {/* Metrics Grid */}
      <div className="grid grid-cols-1 sm:grid-cols-4 gap-4">
        <div className="bg-slate-900/90 border border-slate-800 p-5 rounded-xl">
          <div className="flex items-center justify-between">
            <span className="text-xs font-semibold text-slate-400">Underwriting Queue</span>
            <ClipboardList className="w-4 h-4 text-amber-400" />
          </div>
          <p className="text-2xl font-bold text-white mt-2">{pendingApps.length}</p>
          <p className="text-[11px] text-amber-400 mt-1">Awaiting binding decision</p>
        </div>

        <div className="bg-slate-900/90 border border-slate-800 p-5 rounded-xl">
          <div className="flex items-center justify-between">
            <span className="text-xs font-semibold text-slate-400">Claims in Adjudication</span>
            <ShieldAlert className="w-4 h-4 text-red-400" />
          </div>
          <p className="text-2xl font-bold text-white mt-2">{pendingClaims.length}</p>
          <p className="text-[11px] text-slate-400 mt-1">AI assessment completed</p>
        </div>

        <div className="bg-slate-900/90 border border-slate-800 p-5 rounded-xl">
          <div className="flex items-center justify-between">
            <span className="text-xs font-semibold text-slate-400">Total Active Policies</span>
            <FileCheck2 className="w-4 h-4 text-emerald-400" />
          </div>
          <p className="text-2xl font-bold text-white mt-2">{policies.length}</p>
          <p className="text-[11px] text-emerald-400 mt-1">In active coverage</p>
        </div>

        <div className="bg-slate-900/90 border border-slate-800 p-5 rounded-xl">
          <div className="flex items-center justify-between">
            <span className="text-xs font-semibold text-slate-400">Underwritten Book</span>
            <TrendingUp className="w-4 h-4 text-purple-400" />
          </div>
          <p className="text-2xl font-bold text-white mt-2">
            ₹{(policies.reduce((sum, p) => sum + p.totalSumInsured, 0) / 100000).toFixed(1)}L
          </p>
          <p className="text-[11px] text-slate-400 mt-1">Total liability managed</p>
        </div>
      </div>

      {/* Main Dual Queues */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Underwriting Applications Queue */}
        <div className="bg-slate-900/90 border border-slate-800 p-5 rounded-2xl">
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-sm font-bold text-white uppercase tracking-wider">
              Pending Underwriting Applications
            </h3>
            <Link to="/officer/underwriting" className="text-xs text-amber-400 hover:underline">
              View All
            </Link>
          </div>

          <div className="space-y-3">
            {pendingApps.length === 0 ? (
              <p className="text-xs text-slate-400 text-center py-6">Underwriting queue is empty.</p>
            ) : (
              pendingApps.slice(0, 3).map((app) => (
                <div key={app.id} className="p-3.5 bg-slate-950/60 border border-slate-800 rounded-xl">
                  <div className="flex items-center justify-between">
                    <span className="font-mono text-xs font-bold text-white">{app.applicationNumber}</span>
                    <span className="text-xs font-bold text-amber-300 font-mono">₹{app.totalInsuredValue.toLocaleString()}</span>
                  </div>
                  <p className="text-xs text-slate-300 mt-1">{app.productName} • {app.customerName}</p>
                  <div className="flex items-center justify-between text-[11px] text-slate-500 mt-2 pt-2 border-t border-slate-800/60">
                    <span>Proposed Premium: ₹{app.proposedPremium.toLocaleString()}</span>
                    <Link
                      to="/officer/underwriting"
                      className="text-amber-400 font-semibold hover:underline"
                    >
                      Review & Decide →
                    </Link>
                  </div>
                </div>
              ))
            )}
          </div>
        </div>

        {/* Claims Adjudication Queue */}
        <div className="bg-slate-900/90 border border-slate-800 p-5 rounded-2xl">
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-sm font-bold text-white uppercase tracking-wider">
              Claims Adjudication Queue
            </h3>
            <Link to="/officer/claims" className="text-xs text-amber-400 hover:underline">
              View All
            </Link>
          </div>

          <div className="space-y-3">
            {pendingClaims.length === 0 ? (
              <p className="text-xs text-slate-400 text-center py-6">No claims pending adjudication.</p>
            ) : (
              pendingClaims.slice(0, 3).map((claim) => (
                <div key={claim.id} className="p-3.5 bg-slate-950/60 border border-slate-800 rounded-xl">
                  <div className="flex items-center justify-between">
                    <span className="font-mono text-xs font-bold text-white">{claim.claimNumber}</span>
                    <span className="text-xs font-bold text-red-400 font-mono">₹{claim.claimedAmount.toLocaleString()}</span>
                  </div>
                  <p className="text-xs text-slate-300 mt-1">{claim.assetName} • Incident: {claim.incidentType}</p>
                  <div className="flex items-center justify-between text-[11px] text-slate-500 mt-2 pt-2 border-t border-slate-800/60">
                    <span>Customer: {claim.customerName}</span>
                    <Link
                      to="/officer/claims"
                      className="text-red-400 font-semibold hover:underline"
                    >
                      Adjudicate →
                    </Link>
                  </div>
                </div>
              ))
            )}
          </div>
        </div>
      </div>
    </div>
  );
};
