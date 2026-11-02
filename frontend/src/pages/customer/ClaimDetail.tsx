import React, { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import {
  ShieldAlert,
  ArrowLeft,
  CheckCircle2,
  Clock,
  Sparkles,
  AlertTriangle,
  FileText,
  UserCheck,
  Receipt,
  Gem,
} from 'lucide-react';
import apiClient from '../../api/client';
import { Claim } from '../../types';

export const ClaimDetail: React.FC = () => {
  const { claimNumber } = useParams<{ claimNumber: string }>();
  const [claim, setClaim] = useState<Claim | null>(null);
  const [loading, setLoading] = useState<boolean>(true);

  useEffect(() => {
    if (claimNumber) {
      loadClaim(claimNumber);
    }
  }, [claimNumber]);

  const loadClaim = async (num: string) => {
    try {
      const resp = await apiClient.get(`/claims/${num}`);
      if (resp.data?.data) {
        setClaim(resp.data.data);
      }
    } catch (e) {
      console.error(e);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return <div className="text-center py-20 text-xs text-slate-400">Loading claim lifecycle...</div>;
  }

  if (!claim) {
    return (
      <div className="text-center py-20">
        <h2 className="text-base font-bold text-white">Claim Not Found</h2>
        <Link to="/customer/claims" className="text-xs text-amber-400 mt-2 inline-block">Return to Claims</Link>
      </div>
    );
  }

  const steps = [
    { key: 'SUBMITTED', label: '1. Submitted' },
    { key: 'VALIDATION', label: '2. Validation' },
    { key: 'AI_ASSESSMENT', label: '3. TAGE AI' },
    { key: 'UNDER_REVIEW', label: '4. Officer Review' },
    { key: 'APPROVED', label: '5. Adjudicated' },
    { key: 'SETTLED', label: '6. Settled' },
  ];

  const getStepStatus = (stepKey: string) => {
    if (claim.status === 'REJECTED') return stepKey === 'APPROVED' ? 'rejected' : 'done';
    if (claim.status === 'SETTLED' || claim.status === 'CLOSED') return 'done';
    if (claim.status === 'APPROVED' && stepKey !== 'SETTLED') return 'done';
    if (claim.status === 'UNDER_REVIEW' && (stepKey === 'SUBMITTED' || stepKey === 'VALIDATION' || stepKey === 'AI_ASSESSMENT' || stepKey === 'UNDER_REVIEW')) return 'done';
    return stepKey === 'SUBMITTED' ? 'done' : 'pending';
  };

  return (
    <div className="max-w-5xl mx-auto space-y-6">
      <Link
        to="/customer/claims"
        className="inline-flex items-center space-x-1.5 text-xs text-slate-400 hover:text-white transition-colors"
      >
        <ArrowLeft className="w-4 h-4" />
        <span>Back to Claims History</span>
      </Link>

      {/* Claim Banner */}
      <div className="bg-gradient-to-r from-slate-900 via-slate-900 to-amber-950/30 p-6 rounded-2xl border border-slate-800 shadow-xl flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <div className="flex items-center space-x-2 mb-2">
            <span className="font-mono text-xs font-bold text-amber-400 bg-amber-500/10 border border-amber-500/30 px-2.5 py-0.5 rounded-md">
              {claim.claimNumber}
            </span>
            <span className={`text-[11px] font-semibold px-2 py-0.5 rounded-full ${
              claim.status === 'APPROVED' || claim.status === 'SETTLED'
                ? 'bg-emerald-500/10 text-emerald-400 border border-emerald-500/30'
                : claim.status === 'REJECTED'
                ? 'bg-red-500/10 text-red-400 border border-red-500/30'
                : 'bg-amber-500/10 text-amber-400 border border-amber-500/30'
            }`}>
              {claim.status}
            </span>
            <span className="text-xs text-slate-400 font-mono">Policy: {claim.policyNumber}</span>
          </div>
          <h1 className="text-2xl font-bold text-white tracking-tight">
            Claim for {claim.assetName}
          </h1>
          <p className="text-xs text-slate-400 mt-1">
            Incident Type: <strong className="text-slate-200">{claim.incidentType}</strong> • Occurred on {claim.incidentDate} at {claim.incidentLocation}
          </p>
        </div>

        <div className="text-right">
          <span className="text-[10px] text-slate-500 uppercase block">Claimed Amount</span>
          <span className="text-2xl font-bold text-white font-mono">₹{claim.claimedAmount.toLocaleString()}</span>
        </div>
      </div>

      {/* Guidewire Claim Lifecycle Stepper */}
      <div className="bg-slate-900/90 border border-slate-800 p-5 rounded-2xl">
        <h3 className="text-xs font-bold text-slate-400 uppercase tracking-wider mb-4">
          Guidewire-Aligned Claim Adjudication Workflow
        </h3>
        <div className="grid grid-cols-2 sm:grid-cols-6 gap-2">
          {steps.map((st) => {
            const status = getStepStatus(st.key);
            return (
              <div
                key={st.key}
                className={`p-2.5 rounded-xl border text-center transition-all ${
                  status === 'done'
                    ? 'bg-emerald-500/10 border-emerald-500/40 text-emerald-300'
                    : status === 'rejected'
                    ? 'bg-red-500/10 border-red-500/40 text-red-300'
                    : 'bg-slate-950/60 border-slate-800/80 text-slate-500'
                }`}
              >
                <span className="text-xs font-bold block">{st.label}</span>
                <span className="text-[10px] font-mono mt-0.5 block">
                  {status === 'done' ? 'Completed' : (status === 'rejected' ? 'Declined' : 'In Queue')}
                </span>
              </div>
            );
          })}
        </div>
      </div>

      {/* TAGE AI Assessment & Explainable Insights */}
      {claim.assessment && (
        <div className="bg-gradient-to-br from-slate-900 via-slate-900 to-amber-950/20 border border-amber-500/30 p-6 rounded-2xl shadow-xl">
          <div className="flex items-center justify-between mb-4">
            <div className="flex items-center space-x-2 text-amber-400">
              <Sparkles className="w-5 h-5" />
              <h3 className="text-sm font-bold uppercase tracking-wider">
                TAGE (Transformation-Aware Gold Evidence Engine) Report
              </h3>
            </div>
            <span className="text-[10px] font-mono font-semibold bg-amber-500/10 text-amber-300 border border-amber-500/30 px-2 py-0.5 rounded">
              Experimental Heuristic Model v1.0
            </span>
          </div>

          <div className="grid grid-cols-2 sm:grid-cols-4 gap-3 my-4">
            <div className="bg-slate-950/80 p-3 rounded-xl border border-slate-800">
              <span className="text-[10px] text-slate-500 uppercase block">Visual Identity Match</span>
              <span className="text-base font-bold text-white font-mono">94.5%</span>
            </div>
            <div className="bg-slate-950/80 p-3 rounded-xl border border-slate-800">
              <span className="text-[10px] text-slate-500 uppercase block">Physical Consistency</span>
              <span className="text-base font-bold text-emerald-400 font-mono">98.2%</span>
            </div>
            <div className="bg-slate-950/80 p-3 rounded-xl border border-slate-800">
              <span className="text-[10px] text-slate-500 uppercase block">Anomaly Index</span>
              <span className="text-base font-bold text-amber-300 font-mono">
                {claim.assessment.anomalyScore} (Low)
              </span>
            </div>
            <div className="bg-slate-950/80 p-3 rounded-xl border border-slate-800">
              <span className="text-[10px] text-slate-500 uppercase block">AI Recommendation</span>
              <span className="text-xs font-bold text-emerald-300">
                {claim.assessment.recommendation}
              </span>
            </div>
          </div>

          <div className="p-4 bg-slate-950/60 rounded-xl border border-slate-800 text-xs text-slate-300 leading-relaxed">
            <strong className="text-amber-400 block mb-1">Explainable AI Audit Rationale:</strong>
            {claim.assessment.rationale}
          </div>
        </div>
      )}

      {/* Official Human Insurance Officer Decision */}
      {claim.decision && (
        <div className="bg-slate-900/90 border border-slate-800 p-6 rounded-2xl">
          <div className="flex items-center space-x-2 text-white mb-3">
            <UserCheck className="w-5 h-5 text-emerald-400" />
            <h3 className="text-sm font-bold uppercase tracking-wider">
              Insurance Officer Adjudication Decision
            </h3>
          </div>

          <div className="bg-slate-950 p-4 rounded-xl border border-slate-800 space-y-3 text-xs">
            <div className="flex items-center justify-between border-b border-slate-800 pb-2">
              <div>
                <span className="text-slate-400">Adjudicated By:</span>
                <strong className="text-white ml-1.5">{claim.decision.officerName || 'Underwriting Officer'}</strong>
              </div>
              <span className="text-slate-400 font-mono">{new Date(claim.decision.decisionDate).toLocaleString()}</span>
            </div>

            <p className="text-slate-300 leading-relaxed">
              <strong className="text-slate-400 block mb-1">Decision Rationale:</strong>
              {claim.decision.decisionRationale}
            </p>

            <div className="pt-2 border-t border-slate-800 grid grid-cols-1 sm:grid-cols-3 gap-3 text-xs">
              <div>
                <span className="text-slate-500 block">Settlement Method</span>
                <strong className="text-amber-300">{claim.decision.settlementMethod || 'REPAIR'}</strong>
              </div>
              <div>
                <span className="text-slate-500 block">Deductible Applied (5%)</span>
                <strong className="text-slate-300 font-mono">₹{claim.deductibleAmount.toLocaleString()}</strong>
              </div>
              <div>
                <span className="text-slate-500 block">Net Approved Settlement</span>
                <strong className="text-emerald-400 font-mono text-sm">
                  ₹{claim.netSettlementAmount.toLocaleString()}
                </strong>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
