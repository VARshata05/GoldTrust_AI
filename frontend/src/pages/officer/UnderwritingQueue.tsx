import React, { useState, useEffect } from 'react';
import { ClipboardList, ShieldCheck, Check, X, AlertCircle, FileText, ArrowRight } from 'lucide-react';
import apiClient from '../../api/client';
import { InsuranceApplication } from '../../types';

export const UnderwritingQueue: React.FC = () => {
  const [applications, setApplications] = useState<InsuranceApplication[]>([]);
  const [selectedApp, setSelectedApp] = useState<InsuranceApplication | null>(null);
  const [decision, setDecision] = useState<string>('APPROVE');
  const [reasons, setReasons] = useState<string>('All asset hallmarks, weight assays, and customer KYC parameters validated. Acceptable actuarial risk.');
  const [stipulations, setStipulations] = useState<string>('Standard 5% deductible applied across all accidental loss and theft incidents.');
  const [loading, setLoading] = useState<boolean>(true);
  const [msg, setMsg] = useState<string | null>(null);

  useEffect(() => {
    loadApplications();
  }, []);

  const loadApplications = async () => {
    try {
      const resp = await apiClient.get('/applications');
      if (resp.data?.data) {
        setApplications(resp.data.data);
      }
    } catch (e) {
      console.error(e);
    } finally {
      setLoading(false);
    }
  };

  const handleSubmitDecision = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedApp) return;

    try {
      const resp = await apiClient.post(`/applications/${selectedApp.id}/underwriting`, {
        decision,
        reasons,
        stipulations,
      });

      if (resp.data?.data) {
        setMsg(`Application ${selectedApp.applicationNumber} adjudicated: ${decision}. Policy status updated.`);
        setSelectedApp(null);
        await loadApplications();
        setTimeout(() => setMsg(null), 3000);
      }
    } catch (e) {
      console.error(e);
    }
  };

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-white">Underwriting Authority Queue</h1>
        <p className="text-xs text-slate-400 mt-1">
          Guidewire PolicyCenter rule alignment: Evaluate risk scores, hallmark integrity, and execute binding underwriting decisions.
        </p>
      </div>

      {msg && (
        <div className="p-4 bg-emerald-950/60 border border-emerald-500/50 rounded-xl text-emerald-300 text-xs">
          {msg}
        </div>
      )}

      {/* Applications Table */}
      <div className="bg-slate-900/90 border border-slate-800 rounded-2xl p-6">
        <h3 className="text-sm font-bold text-white uppercase tracking-wider mb-4">
          Incoming Insurance Applications
        </h3>

        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs">
            <thead className="bg-slate-950 text-slate-400 border-b border-slate-800">
              <tr>
                <th className="py-3 px-3 font-semibold uppercase">Application #</th>
                <th className="py-3 px-3 font-semibold uppercase">Customer</th>
                <th className="py-3 px-3 font-semibold uppercase">Plan</th>
                <th className="py-3 px-3 font-semibold uppercase">Insured Value</th>
                <th className="py-3 px-3 font-semibold uppercase">Premium</th>
                <th className="py-3 px-3 font-semibold uppercase">Status</th>
                <th className="py-3 px-3 font-semibold uppercase text-right">Adjudication</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-800/60 text-slate-300">
              {applications.map((app) => (
                <tr key={app.id} className="hover:bg-slate-800/30">
                  <td className="py-3 px-3 font-mono font-bold text-white">{app.applicationNumber}</td>
                  <td className="py-3 px-3">
                    <strong className="text-white block">{app.customerName}</strong>
                    <span className="text-[10px] text-slate-500">{app.customerEmail}</span>
                  </td>
                  <td className="py-3 px-3">{app.productName}</td>
                  <td className="py-3 px-3 font-mono font-bold text-amber-300">₹{app.totalInsuredValue.toLocaleString()}</td>
                  <td className="py-3 px-3 font-mono font-semibold text-white">₹{app.proposedPremium.toLocaleString()}</td>
                  <td className="py-3 px-3">
                    <span className={`px-2 py-0.5 rounded text-[10px] font-bold ${
                      app.status === 'APPROVED'
                        ? 'bg-emerald-500/10 text-emerald-400 border border-emerald-500/30'
                        : app.status === 'DECLINED'
                        ? 'bg-red-500/10 text-red-400 border border-red-500/30'
                        : 'bg-amber-500/10 text-amber-400 border border-amber-500/30'
                    }`}>
                      {app.status}
                    </span>
                  </td>
                  <td className="py-3 px-3 text-right">
                    {app.status === 'UNDERWRITING' || app.status === 'SUBMITTED' ? (
                      <button
                        onClick={() => setSelectedApp(app)}
                        className="px-3 py-1.5 bg-amber-500 hover:bg-amber-600 text-slate-950 font-bold rounded-lg text-xs transition-colors"
                      >
                        Underwrite
                      </button>
                    ) : (
                      <span className="text-[11px] text-slate-500 font-mono">
                        {app.underwritingDecision || 'Finalized'}
                      </span>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {/* Underwriting Decision Modal */}
      {selectedApp && (
        <div className="fixed inset-0 bg-slate-950/80 backdrop-blur-sm z-50 flex items-center justify-center p-4">
          <div className="bg-slate-900 border border-slate-800 rounded-2xl max-w-lg w-full p-6 shadow-2xl">
            <h3 className="text-base font-bold text-white mb-1">Underwriting Decision Form</h3>
            <p className="text-xs text-slate-400 mb-4 font-mono">{selectedApp.applicationNumber} • {selectedApp.customerName}</p>

            <form onSubmit={handleSubmitDecision} className="space-y-4">
              <div className="grid grid-cols-2 gap-3 p-3 bg-slate-950 rounded-xl border border-slate-800 text-xs">
                <div>
                  <span className="text-slate-500 block">Proposed Sum Insured:</span>
                  <strong className="text-amber-300 font-mono">₹{selectedApp.totalInsuredValue.toLocaleString()}</strong>
                </div>
                <div>
                  <span className="text-slate-500 block">Proposed Annual Premium:</span>
                  <strong className="text-white font-mono">₹{selectedApp.proposedPremium.toLocaleString()}</strong>
                </div>
              </div>

              <div>
                <label className="text-xs font-semibold text-slate-300 block mb-1">Underwriting Decision *</label>
                <div className="grid grid-cols-2 sm:grid-cols-4 gap-2">
                  {['APPROVE', 'REFER', 'REQUEST_INFORMATION', 'DECLINE'].map((d) => (
                    <button
                      key={d}
                      type="button"
                      onClick={() => setDecision(d)}
                      className={`p-2 rounded-lg border text-xs font-bold transition-all ${
                        decision === d
                          ? d === 'APPROVE'
                            ? 'bg-emerald-500 text-slate-950 border-emerald-500'
                            : d === 'DECLINE'
                            ? 'bg-red-500 text-white border-red-500'
                            : 'bg-amber-500 text-slate-950 border-amber-500'
                          : 'bg-slate-950 border-slate-800 text-slate-400'
                      }`}
                    >
                      {d}
                    </button>
                  ))}
                </div>
              </div>

              <div>
                <label className="text-xs font-semibold text-slate-300 block mb-1">Mandatory Decision Reasons *</label>
                <textarea
                  rows={3}
                  required
                  placeholder="Record underwriting rationale and risk evaluation findings..."
                  value={reasons}
                  onChange={(e) => setReasons(e.target.value)}
                  className="w-full bg-slate-950 border border-slate-800 rounded-lg p-2.5 text-xs text-white focus:outline-none focus:border-amber-500"
                />
              </div>

              <div>
                <label className="text-xs font-semibold text-slate-300 block mb-1">Stipulations & Policy Endorsement Notes</label>
                <input
                  type="text"
                  value={stipulations}
                  onChange={(e) => setStipulations(e.target.value)}
                  className="w-full bg-slate-950 border border-slate-800 rounded-lg p-2.5 text-xs text-white focus:outline-none focus:border-amber-500"
                />
              </div>

              <div className="flex items-center space-x-3 pt-2">
                <button
                  type="button"
                  onClick={() => setSelectedApp(null)}
                  className="flex-1 py-2.5 bg-slate-800 hover:bg-slate-700 text-slate-300 text-xs font-semibold rounded-xl"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="flex-1 py-2.5 bg-gradient-to-r from-amber-500 to-yellow-500 hover:from-amber-600 hover:to-yellow-600 text-slate-950 text-xs font-bold rounded-xl shadow-lg shadow-amber-500/20"
                >
                  Confirm & Bind Decision
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

