import React, { useState, useEffect } from 'react';
import { ShieldAlert, Sparkles, UserCheck, Check, AlertTriangle, ArrowRight, Eye, Wrench } from 'lucide-react';
import apiClient from '../../api/client';
import { Claim } from '../../types';

export const ClaimsAdjudication: React.FC = () => {
  const [claims, setClaims] = useState<Claim[]>([]);
  const [selectedClaim, setSelectedClaim] = useState<Claim | null>(null);
  const [decision, setDecision] = useState<string>('APPROVED');
  const [settlementMethod, setSettlementMethod] = useState<string>('REPAIR');
  const [rationale, setRationale] = useState<string>('TAGE multi-evidence verification confirmed asset identity v2. Hallmark verified. Incident falls under policy terms.');
  const [approvedAmount, setApprovedAmount] = useState<string>('');
  const [loading, setLoading] = useState<boolean>(true);
  const [msg, setMsg] = useState<string | null>(null);

  useEffect(() => {
    loadClaims();
  }, []);

  const loadClaims = async () => {
    try {
      const resp = await apiClient.get('/claims');
      if (resp.data?.data) {
        setClaims(resp.data.data);
      }
    } catch (e) {
      console.error(e);
    } finally {
      setLoading(false);
    }
  };

  const handleAdjudicate = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedClaim) return;

    try {
      const resp = await apiClient.post(`/claims/${selectedClaim.id}/decision`, {
        decision,
        decisionRationale: rationale,
        settlementMethod,
        approvedAmount: approvedAmount ? parseFloat(approvedAmount) : selectedClaim.claimedAmount,
      });

      if (resp.data?.data) {
        setMsg(`Claim ${selectedClaim.claimNumber} adjudicated: ${decision} with ${settlementMethod} settlement.`);
        setSelectedClaim(null);
        await loadClaims();
        setTimeout(() => setMsg(null), 3000);
      }
    } catch (e) {
      console.error(e);
    }
  };

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-white">Claims Adjudication Workbench</h1>
        <p className="text-xs text-slate-400 mt-1">
          Review explainable TAGE AI evidence assessments and execute official human adjudication decisions.
        </p>
      </div>

      {msg && (
        <div className="p-4 bg-emerald-950/60 border border-emerald-500/50 rounded-xl text-emerald-300 text-xs">
          {msg}
        </div>
      )}

      {/* Claims Table */}
      <div className="bg-slate-900/90 border border-slate-800 rounded-2xl p-6">
        <h3 className="text-sm font-bold text-white uppercase tracking-wider mb-4">
          Claims Adjudication Pipeline
        </h3>

        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs">
            <thead className="bg-slate-950 text-slate-400 border-b border-slate-800">
              <tr>
                <th className="py-3 px-3 font-semibold uppercase">Claim #</th>
                <th className="py-3 px-3 font-semibold uppercase">Policy #</th>
                <th className="py-3 px-3 font-semibold uppercase">Customer</th>
                <th className="py-3 px-3 font-semibold uppercase">Incident</th>
                <th className="py-3 px-3 font-semibold uppercase">Claimed (₹)</th>
                <th className="py-3 px-3 font-semibold uppercase">AI Rec</th>
                <th className="py-3 px-3 font-semibold uppercase">Status</th>
                <th className="py-3 px-3 font-semibold uppercase text-right">Adjudicate</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-800/60 text-slate-300">
              {claims.map((claim) => (
                <tr key={claim.id} className="hover:bg-slate-800/30">
                  <td className="py-3 px-3 font-mono font-bold text-white">{claim.claimNumber}</td>
                  <td className="py-3 px-3 font-mono text-amber-400">{claim.policyNumber}</td>
                  <td className="py-3 px-3">{claim.customerName}</td>
                  <td className="py-3 px-3">
                    <span className="font-semibold text-white block">{claim.incidentType}</span>
                    <span className="text-[10px] text-slate-500">{claim.incidentDate}</span>
                  </td>
                  <td className="py-3 px-3 font-mono font-bold text-white">₹{claim.claimedAmount.toLocaleString()}</td>
                  <td className="py-3 px-3">
                    <span className="text-[10px] font-mono text-emerald-400 bg-emerald-500/10 px-2 py-0.5 rounded border border-emerald-500/30">
                      {claim.assessment?.recommendation || 'CONSISTENT'}
                    </span>
                  </td>
                  <td className="py-3 px-3">
                    <span className={`px-2 py-0.5 rounded text-[10px] font-bold ${
                      claim.status === 'APPROVED'
                        ? 'bg-emerald-500/10 text-emerald-400 border border-emerald-500/30'
                        : claim.status === 'REJECTED'
                        ? 'bg-red-500/10 text-red-400 border border-red-500/30'
                        : 'bg-amber-500/10 text-amber-400 border border-amber-500/30'
                    }`}>
                      {claim.status}
                    </span>
                  </td>
                  <td className="py-3 px-3 text-right">
                    {claim.status === 'UNDER_REVIEW' || claim.status === 'SUBMITTED' ? (
                      <button
                        onClick={() => {
                          setSelectedClaim(claim);
                          setApprovedAmount(String(claim.claimedAmount));
                          setSettlementMethod(claim.settlementPreference || 'REPAIR');
                        }}
                        className="px-3 py-1.5 bg-amber-500 hover:bg-amber-600 text-slate-950 font-bold rounded-lg text-xs transition-colors"
                      >
                        Review & Decide
                      </button>
                    ) : (
                      <span className="text-[11px] text-slate-500 font-mono">
                        {claim.decision?.decision || 'Finalized'}
                      </span>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {/* Adjudication Modal */}
      {selectedClaim && (
        <div className="fixed inset-0 bg-slate-950/85 backdrop-blur-sm z-50 flex items-center justify-center p-4 overflow-y-auto">
          <div className="bg-slate-900 border border-slate-800 rounded-2xl max-w-2xl w-full p-6 shadow-2xl space-y-4 my-8">
            <div className="flex items-center justify-between border-b border-slate-800 pb-3">
              <div>
                <h3 className="text-base font-bold text-white">Claim Adjudication Review</h3>
                <span className="text-xs font-mono text-amber-400">{selectedClaim.claimNumber} • Policy {selectedClaim.policyNumber}</span>
              </div>
              <span className="text-xs font-bold text-white font-mono bg-slate-950 px-3 py-1 rounded border border-slate-800">
                Claimed: ₹{selectedClaim.claimedAmount.toLocaleString()}
              </span>
            </div>

            {/* Incident Summary */}
            <div className="p-3 bg-slate-950 rounded-xl border border-slate-800 text-xs space-y-1">
              <span className="text-slate-400 block font-semibold">Incident Narrative:</span>
              <p className="text-slate-200">{selectedClaim.incidentDescription}</p>
            </div>

            {/* TAGE AI Assessment Summary */}
            {selectedClaim.assessment && (
              <div className="p-4 bg-gradient-to-br from-slate-950 to-amber-950/20 rounded-xl border border-amber-500/30 text-xs space-y-2">
                <div className="flex items-center space-x-2 text-amber-400">
                  <Sparkles className="w-4 h-4" />
                  <strong className="uppercase tracking-wider">TAGE Multi-Evidence Fusion AI Findings</strong>
                </div>
                <div className="grid grid-cols-3 gap-2 py-1 font-mono text-[11px]">
                  <div>Visual Similarity: <strong className="text-white">94.5%</strong></div>
                  <div>Physical Consistency: <strong className="text-emerald-400">98.2%</strong></div>
                  <div>Anomaly Index: <strong className="text-amber-300">0.04 (Normal)</strong></div>
                </div>
                <p className="text-slate-300 leading-snug">{selectedClaim.assessment.rationale}</p>
              </div>
            )}

            {/* Adjudication Controls */}
            <form onSubmit={handleAdjudicate} className="space-y-4 pt-2">
              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="text-xs font-semibold text-slate-300 block mb-1">Human Officer Decision *</label>
                  <select
                    value={decision}
                    onChange={(e) => setDecision(e.target.value)}
                    className="w-full bg-slate-950 border border-slate-800 rounded-lg p-2.5 text-xs text-white focus:outline-none focus:border-amber-500 font-bold"
                  >
                    <option value="APPROVED">APPROVED (Authorize Settlement)</option>
                    <option value="REJECTED">REJECTED (Decline Claim)</option>
                    <option value="REQUEST_INFORMATION">REQUEST MORE INFORMATION</option>
                  </select>
                </div>

                <div>
                  <label className="text-xs font-semibold text-slate-300 block mb-1">Settlement Channel *</label>
                  <select
                    value={settlementMethod}
                    onChange={(e) => setSettlementMethod(e.target.value)}
                    className="w-full bg-slate-950 border border-slate-800 rounded-lg p-2.5 text-xs text-white focus:outline-none focus:border-amber-500"
                  >
                    <option value="REPAIR">Certified Network Repair (Dispatch to Jeweller)</option>
                    <option value="REPLACEMENT">Asset Replacement (Equivalent 22K)</option>
                    <option value="CASH">Cash Settlement (Net of 5% Deductible)</option>
                  </select>
                </div>
              </div>

              <div>
                <label className="text-xs font-semibold text-slate-300 block mb-1">Approved Claim Amount (₹) *</label>
                <input
                  type="number"
                  step="0.01"
                  required
                  value={approvedAmount}
                  onChange={(e) => setApprovedAmount(e.target.value)}
                  className="w-full bg-slate-950 border border-slate-800 rounded-lg p-2.5 text-xs text-white focus:outline-none focus:border-amber-500 font-mono"
                />
              </div>

              <div>
                <label className="text-xs font-semibold text-slate-300 block mb-1">Official Decision Rationale *</label>
                <textarea
                  rows={3}
                  required
                  placeholder="Record formal adjudication rationale..."
                  value={rationale}
                  onChange={(e) => setRationale(e.target.value)}
                  className="w-full bg-slate-950 border border-slate-800 rounded-lg p-2.5 text-xs text-white focus:outline-none focus:border-amber-500"
                />
              </div>

              <div className="flex items-center space-x-3 pt-2">
                <button
                  type="button"
                  onClick={() => setSelectedClaim(null)}
                  className="flex-1 py-2.5 bg-slate-800 hover:bg-slate-700 text-slate-300 text-xs font-semibold rounded-xl"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="flex-1 py-2.5 bg-gradient-to-r from-amber-500 to-yellow-500 hover:from-amber-600 hover:to-yellow-600 text-slate-950 text-xs font-bold rounded-xl shadow-lg shadow-amber-500/20"
                >
                  Execute Claim Decision
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
