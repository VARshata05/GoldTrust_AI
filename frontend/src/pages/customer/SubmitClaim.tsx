import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { AlertTriangle, ShieldAlert, ArrowLeft, Upload, FileText, CheckCircle2 } from 'lucide-react';
import apiClient from '../../api/client';
import { InsurancePolicy, Jeweller } from '../../types';

export const SubmitClaim: React.FC = () => {
  const navigate = useNavigate();
  const [policies, setPolicies] = useState<InsurancePolicy[]>([]);
  const [jewellers, setJewellers] = useState<Jeweller[]>([]);
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);

  const [form, setForm] = useState({
    policyId: '',
    assetId: '',
    incidentType: 'DAMAGE',
    incidentDate: new Date().toISOString().split('T')[0],
    incidentLocation: 'Worli Sea Face, Mumbai',
    incidentDescription: '',
    policeReportNumber: '',
    policeStation: '',
    preferredJewellerId: '',
    settlementPreference: 'REPAIR',
    claimedAmount: '',
    evidencePhotoUrl: 'https://images.unsplash.com/photo-1599643478518-a784e5dc4c8f?auto=format&fit=crop&w=600&q=80',
  });

  useEffect(() => {
    fetchInitial();
  }, []);

  const fetchInitial = async () => {
    try {
      const [policiesRes, jewellersRes] = await Promise.allSettled([
        apiClient.get('/policies'),
        apiClient.get('/jewellers'),
      ]);

      if (policiesRes.status === 'fulfilled' && policiesRes.value.data?.data) {
        const pList: InsurancePolicy[] = policiesRes.value.data.data;
        setPolicies(pList);
        if (pList.length > 0) {
          setForm(prev => ({
            ...prev,
            policyId: String(pList[0].id),
            assetId: pList[0].coveredAssets?.[0]?.assetId || '',
          }));
        }
      }

      if (jewellersRes.status === 'fulfilled' && jewellersRes.value.data?.data) {
        setJewellers(jewellersRes.value.data.data);
      }
    } catch (e) {
      console.error(e);
    }
  };

  const selectedPolicy = policies.find(p => String(p.id) === form.policyId);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError(null);

    try {
      const payload = {
        policyId: parseInt(form.policyId),
        assetId: form.assetId,
        incidentType: form.incidentType,
        incidentDate: form.incidentDate,
        incidentLocation: form.incidentLocation,
        incidentDescription: form.incidentDescription,
        policeReportNumber: form.policeReportNumber || undefined,
        policeStation: form.policeStation || undefined,
        preferredJewellerId: form.preferredJewellerId ? parseInt(form.preferredJewellerId) : undefined,
        settlementPreference: form.settlementPreference,
        claimedAmount: parseFloat(form.claimedAmount),
        evidence: [
          {
            evidenceType: form.incidentType === 'DAMAGE' ? 'DAMAGE_PHOTO' : 'POLICE_REPORT',
            fileName: 'claim_evidence_primary.jpg',
            fileUrl: form.evidencePhotoUrl,
            description: 'Incident evidence submitted by insured customer.',
          },
        ],
      };

      const resp = await apiClient.post('/claims', payload);
      if (resp.data?.data) {
        navigate(`/customer/claims`);
      }
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to submit claim');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-3xl mx-auto space-y-6">
      <button
        onClick={() => navigate(-1)}
        className="inline-flex items-center space-x-1.5 text-xs text-slate-400 hover:text-white transition-colors"
      >
        <ArrowLeft className="w-4 h-4" />
        <span>Back</span>
      </button>

      <div>
        <h1 className="text-2xl font-bold text-white">File Insurance Claim</h1>
        <p className="text-xs text-slate-400 mt-1">
          Submit claim report for TAGE automated evidence verification and Insurance Officer adjudication.
        </p>
      </div>

      {error && (
        <div className="p-4 bg-red-950/50 border border-red-500/50 rounded-xl text-red-300 text-xs">
          {error}
        </div>
      )}

      <form onSubmit={handleSubmit} className="bg-slate-900/90 border border-slate-800 p-6 rounded-2xl space-y-5">
        {/* Policy and Asset Selector */}
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div>
            <label className="text-xs font-semibold text-slate-300 block mb-1.5">Active Insurance Policy *</label>
            <select
              value={form.policyId}
              onChange={(e) => {
                const pid = e.target.value;
                const pol = policies.find(p => String(p.id) === pid);
                setForm({
                  ...form,
                  policyId: pid,
                  assetId: pol?.coveredAssets?.[0]?.assetId || '',
                });
              }}
              className="w-full bg-slate-950 border border-slate-800 rounded-lg p-2.5 text-xs text-white focus:outline-none focus:border-amber-500 font-mono"
            >
              {policies.map((p) => (
                <option key={p.id} value={p.id}>
                  {p.policyNumber} – {p.productName}
                </option>
              ))}
            </select>
          </div>

          <div>
            <label className="text-xs font-semibold text-slate-300 block mb-1.5">Covered Jewellery Asset *</label>
            <select
              value={form.assetId}
              onChange={(e) => setForm({ ...form, assetId: e.target.value })}
              className="w-full bg-slate-950 border border-slate-800 rounded-lg p-2.5 text-xs text-white focus:outline-none focus:border-amber-500 font-mono"
            >
              {selectedPolicy?.coveredAssets?.map((a) => (
                <option key={a.assetId} value={a.assetId}>
                  {a.assetId} – {a.assetName} ({a.netGoldWeight}g)
                </option>
              ))}
            </select>
          </div>
        </div>

        {/* Incident Type & Date */}
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div>
            <label className="text-xs font-semibold text-slate-300 block mb-1.5">Incident Type *</label>
            <select
              value={form.incidentType}
              onChange={(e) => setForm({ ...form, incidentType: e.target.value })}
              className="w-full bg-slate-950 border border-slate-800 rounded-lg p-2.5 text-xs text-white focus:outline-none focus:border-amber-500"
            >
              <option value="DAMAGE">Accidental Damage (Deformation, Fracture, Stone Dislodged)</option>
              <option value="LOSS">Accidental Loss</option>
              <option value="THEFT">Theft & Burglary</option>
              <option value="MYSTERIOUS_DISAPPEARANCE">Mysterious Disappearance</option>
              <option value="NATURAL_DISASTER">Natural Disaster</option>
            </select>
          </div>

          <div>
            <label className="text-xs font-semibold text-slate-300 block mb-1.5">Date of Occurrence *</label>
            <input
              type="date"
              required
              value={form.incidentDate}
              onChange={(e) => setForm({ ...form, incidentDate: e.target.value })}
              className="w-full bg-slate-950 border border-slate-800 rounded-lg p-2.5 text-xs text-white focus:outline-none focus:border-amber-500"
            />
          </div>
        </div>

        {/* Incident Location & Claimed Amount */}
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div>
            <label className="text-xs font-semibold text-slate-300 block mb-1.5">Incident Location *</label>
            <input
              type="text"
              required
              placeholder="e.g. Bandra Kurla Complex, Mumbai"
              value={form.incidentLocation}
              onChange={(e) => setForm({ ...form, incidentLocation: e.target.value })}
              className="w-full bg-slate-950 border border-slate-800 rounded-lg p-2.5 text-xs text-white focus:outline-none focus:border-amber-500"
            />
          </div>

          <div>
            <label className="text-xs font-semibold text-slate-300 block mb-1.5">Estimated Claim Amount (₹) *</label>
            <input
              type="number"
              step="0.01"
              required
              placeholder="e.g. 18500"
              value={form.claimedAmount}
              onChange={(e) => setForm({ ...form, claimedAmount: e.target.value })}
              className="w-full bg-slate-950 border border-slate-800 rounded-lg p-2.5 text-xs text-white focus:outline-none focus:border-amber-500 font-mono"
            />
          </div>
        </div>

        {/* Description */}
        <div>
          <label className="text-xs font-semibold text-slate-300 block mb-1.5">Detailed Incident Narrative *</label>
          <textarea
            rows={3}
            required
            placeholder="Describe the incident sequence, impact, and damage circumstances clearly..."
            value={form.incidentDescription}
            onChange={(e) => setForm({ ...form, incidentDescription: e.target.value })}
            className="w-full bg-slate-950 border border-slate-800 rounded-lg p-2.5 text-xs text-white focus:outline-none focus:border-amber-500"
          />
        </div>

        {/* Police Report (If applicable) */}
        {(form.incidentType === 'THEFT' || form.incidentType === 'LOSS') && (
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 p-3.5 bg-slate-950/60 rounded-xl border border-slate-800">
            <div>
              <label className="text-xs font-semibold text-slate-300 block mb-1">Police FIR / Incident Number</label>
              <input
                type="text"
                placeholder="e.g. FIR-2026-9921"
                value={form.policeReportNumber}
                onChange={(e) => setForm({ ...form, policeReportNumber: e.target.value })}
                className="w-full bg-slate-900 border border-slate-700 rounded-lg p-2 text-xs text-white focus:outline-none focus:border-amber-500 font-mono"
              />
            </div>
            <div>
              <label className="text-xs font-semibold text-slate-300 block mb-1">Police Station Jurisdiction</label>
              <input
                type="text"
                placeholder="e.g. Bandra Police Station"
                value={form.policeStation}
                onChange={(e) => setForm({ ...form, policeStation: e.target.value })}
                className="w-full bg-slate-900 border border-slate-700 rounded-lg p-2 text-xs text-white focus:outline-none focus:border-amber-500"
              />
            </div>
          </div>
        )}

        {/* Settlement Preference & Jeweller */}
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div>
            <label className="text-xs font-semibold text-slate-300 block mb-1.5">Settlement Preference</label>
            <select
              value={form.settlementPreference}
              onChange={(e) => setForm({ ...form, settlementPreference: e.target.value })}
              className="w-full bg-slate-950 border border-slate-800 rounded-lg p-2.5 text-xs text-white focus:outline-none focus:border-amber-500"
            >
              <option value="REPAIR">Certified Network Repair (Restoration at Verified Jeweller)</option>
              <option value="REPLACEMENT">Exact Spec Asset Replacement</option>
              <option value="CASH_SETTLEMENT">Cash Settlement (Net of Deductible)</option>
            </select>
          </div>

          <div>
            <label className="text-xs font-semibold text-slate-300 block mb-1.5">Preferred Network Jeweller</label>
            <select
              value={form.preferredJewellerId}
              onChange={(e) => setForm({ ...form, preferredJewellerId: e.target.value })}
              className="w-full bg-slate-950 border border-slate-800 rounded-lg p-2.5 text-xs text-white focus:outline-none focus:border-amber-500"
            >
              <option value="">Any Available Network Jeweller</option>
              {jewellers.map((j) => (
                <option key={j.id} value={j.id}>{j.businessName} ({j.city})</option>
              ))}
            </select>
          </div>
        </div>

        {/* Evidence Photo */}
        <div>
          <label className="text-xs font-semibold text-slate-300 block mb-1.5">Supporting Damage / Police Photo URL</label>
          <input
            type="url"
            value={form.evidencePhotoUrl}
            onChange={(e) => setForm({ ...form, evidencePhotoUrl: e.target.value })}
            className="w-full bg-slate-950 border border-slate-800 rounded-lg p-2.5 text-xs text-white focus:outline-none focus:border-amber-500"
          />
        </div>

        <button
          type="submit"
          disabled={loading}
          className="w-full py-3 bg-gradient-to-r from-red-600 to-amber-600 hover:from-red-700 hover:to-amber-700 text-white font-bold rounded-xl text-xs shadow-lg shadow-red-600/20 transition-all"
        >
          {loading ? 'Submitting & Running TAGE Analysis...' : 'Submit Claim for Adjudication'}
        </button>
      </form>
    </div>
  );
};
