import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Gem, ShieldCheck, Sparkles, ArrowLeft, Info } from 'lucide-react';
import apiClient from '../../api/client';
import { GoldRate, Jeweller } from '../../types';

export const RegisterJewellery: React.FC = () => {
  const navigate = useNavigate();
  const [liveRate, setLiveRate] = useState<GoldRate | null>(null);
  const [jewellers, setJewellers] = useState<Jeweller[]>([]);
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);

  const [form, setForm] = useState({
    name: '',
    category: 'Necklace',
    purity: '22K',
    netGoldWeight: '',
    grossWeight: '',
    huid: '',
    hallmarkStatus: 'VERIFIED',
    purchaseDate: new Date().toISOString().split('T')[0],
    purchasePrice: '',
    appraisalValue: '',
    primaryImageUrl: 'https://images.unsplash.com/photo-1599643478518-a784e5dc4c8f?auto=format&fit=crop&w=600&q=80',
    preferredJewellerId: '',
  });

  useEffect(() => {
    fetchInitialData();
  }, []);

  const fetchInitialData = async () => {
    try {
      const [rateRes, jewellersRes] = await Promise.allSettled([
        apiClient.get('/valuation/rates/live'),
        apiClient.get('/jewellers'),
      ]);
      if (rateRes.status === 'fulfilled' && rateRes.value.data?.data) {
        setLiveRate(rateRes.value.data.data);
      }
      if (jewellersRes.status === 'fulfilled' && jewellersRes.value.data?.data) {
        setJewellers(jewellersRes.value.data.data);
        if (jewellersRes.value.data.data.length > 0) {
          setForm(prev => ({ ...prev, preferredJewellerId: String(jewellersRes.value.data.data[0].id) }));
        }
      }
    } catch (e) {}
  };

  // Dynamic Valuation Estimation
  const netWeightNum = parseFloat(form.netGoldWeight) || 0;
  const ratePerGram = form.purity === '24K' ? (liveRate?.rate24k || 7850) :
    form.purity === '18K' ? (liveRate?.rate18k || 5888) :
    form.purity === '14K' ? (liveRate?.rate14k || 4580) : (liveRate?.rate22k || 7195);
  const estimatedGoldValue = netWeightNum * ratePerGram;
  const estimatedMakingCharges = estimatedGoldValue * 0.12;
  const totalEstimatedValue = estimatedGoldValue + estimatedMakingCharges;

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError(null);

    try {
      const payload = {
        name: form.name,
        category: form.category,
        purity: form.purity,
        netGoldWeight: netWeightNum,
        grossWeight: parseFloat(form.grossWeight) || netWeightNum,
        huid: form.huid ? form.huid.toUpperCase() : undefined,
        hallmarkStatus: form.hallmarkStatus,
        purchaseDate: form.purchaseDate,
        purchasePrice: parseFloat(form.purchasePrice) || totalEstimatedValue,
        appraisalValue: parseFloat(form.appraisalValue) || totalEstimatedValue,
        primaryImageUrl: form.primaryImageUrl,
        preferredJewellerId: form.preferredJewellerId ? parseInt(form.preferredJewellerId) : undefined,
      };

      const resp = await apiClient.post('/jewellery', payload);
      if (resp.data?.success && resp.data?.data) {
        navigate(`/customer/jewellery/${resp.data.data.assetId}`);
      }
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to register jewellery asset');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-4xl mx-auto space-y-6">
      {/* Back button */}
      <button
        onClick={() => navigate(-1)}
        className="inline-flex items-center space-x-1.5 text-xs text-slate-400 hover:text-white transition-colors"
      >
        <ArrowLeft className="w-4 h-4" />
        <span>Back to Portfolio</span>
      </button>

      {/* Header */}
      <div>
        <h1 className="text-2xl font-bold text-white">Register Gold Jewellery Asset</h1>
        <p className="text-xs text-slate-400 mt-1">
          Create an enterprise-grade digital identity passport with BIS hallmark and weight verification.
        </p>
      </div>

      {error && (
        <div className="p-4 bg-red-950/50 border border-red-500/50 rounded-xl text-red-300 text-xs">
          {error}
        </div>
      )}

      <form onSubmit={handleSubmit} className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Left Form Columns */}
        <div className="lg:col-span-2 bg-slate-900/90 border border-slate-800 p-6 rounded-2xl space-y-5">
          <div className="border-b border-slate-800 pb-3">
            <h3 className="text-sm font-bold text-white uppercase tracking-wider">Item Identification</h3>
          </div>

          {/* Name */}
          <div>
            <label className="text-xs font-semibold text-slate-300 block mb-1.5">
              Jewellery Title / Item Name *
            </label>
            <input
              type="text"
              required
              placeholder="e.g. Heritage Kundan Choker Necklace"
              value={form.name}
              onChange={(e) => setForm({ ...form, name: e.target.value })}
              className="w-full bg-slate-950 border border-slate-800 rounded-lg px-3.5 py-2.5 text-xs text-white placeholder-slate-500 focus:outline-none focus:border-amber-500"
            />
          </div>

          {/* Category & Purity */}
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div>
              <label className="text-xs font-semibold text-slate-300 block mb-1.5">Category *</label>
              <select
                value={form.category}
                onChange={(e) => setForm({ ...form, category: e.target.value })}
                className="w-full bg-slate-950 border border-slate-800 rounded-lg px-3.5 py-2.5 text-xs text-white focus:outline-none focus:border-amber-500"
              >
                <option value="Necklace">Necklace</option>
                <option value="Ring">Ring</option>
                <option value="Chain">Chain</option>
                <option value="Bracelet">Bracelet</option>
                <option value="Earrings">Earrings</option>
                <option value="Bangle">Bangle</option>
                <option value="Pendant">Pendant</option>
                <option value="Watch">Watch</option>
                <option value="Other">Other</option>
              </select>
            </div>

            <div>
              <label className="text-xs font-semibold text-slate-300 block mb-1.5">Purity Standard *</label>
              <select
                value={form.purity}
                onChange={(e) => setForm({ ...form, purity: e.target.value })}
                className="w-full bg-slate-950 border border-slate-800 rounded-lg px-3.5 py-2.5 text-xs text-white focus:outline-none focus:border-amber-500"
              >
                <option value="24K">24K (99.9% Pure Gold)</option>
                <option value="22K">22K (91.6% BIS Hallmark Standard)</option>
                <option value="18K">18K (75.0% Fine Jewellery)</option>
                <option value="14K">14K (58.5% Durable Gold)</option>
              </select>
            </div>
          </div>

          {/* Weights */}
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div>
              <label className="text-xs font-semibold text-slate-300 block mb-1.5">
                Net Gold Weight (grams) *
              </label>
              <input
                type="number"
                step="0.001"
                min="0.01"
                required
                placeholder="e.g. 45.500"
                value={form.netGoldWeight}
                onChange={(e) => setForm({ ...form, netGoldWeight: e.target.value })}
                className="w-full bg-slate-950 border border-slate-800 rounded-lg px-3.5 py-2.5 text-xs text-white placeholder-slate-500 focus:outline-none focus:border-amber-500 font-mono"
              />
              <span className="text-[10px] text-slate-500 mt-1 block">Net gold content excluding stones.</span>
            </div>

            <div>
              <label className="text-xs font-semibold text-slate-300 block mb-1.5">
                Gross Total Weight (grams) *
              </label>
              <input
                type="number"
                step="0.001"
                min="0.01"
                required
                placeholder="e.g. 52.200"
                value={form.grossWeight}
                onChange={(e) => setForm({ ...form, grossWeight: e.target.value })}
                className="w-full bg-slate-950 border border-slate-800 rounded-lg px-3.5 py-2.5 text-xs text-white placeholder-slate-500 focus:outline-none focus:border-amber-500 font-mono"
              />
              <span className="text-[10px] text-slate-500 mt-1 block">Total weight on calibrated scale.</span>
            </div>
          </div>

          {/* HUID & Hallmark */}
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div>
              <label className="text-xs font-semibold text-slate-300 block mb-1.5">
                BIS Hallmark HUID (6 Characters)
              </label>
              <input
                type="text"
                maxLength={6}
                placeholder="e.g. HN7291"
                value={form.huid}
                onChange={(e) => setForm({ ...form, huid: e.target.value.toUpperCase() })}
                className="w-full bg-slate-950 border border-slate-800 rounded-lg px-3.5 py-2.5 text-xs text-white placeholder-slate-500 focus:outline-none focus:border-amber-500 font-mono tracking-widest uppercase"
              />
              <span className="text-[10px] text-slate-500 mt-1 block">Unique alphanumeric laser mark.</span>
            </div>

            <div>
              <label className="text-xs font-semibold text-slate-300 block mb-1.5">
                Hallmark Status
              </label>
              <select
                value={form.hallmarkStatus}
                onChange={(e) => setForm({ ...form, hallmarkStatus: e.target.value })}
                className="w-full bg-slate-950 border border-slate-800 rounded-lg px-3.5 py-2.5 text-xs text-white focus:outline-none focus:border-amber-500"
              >
                <option value="VERIFIED">BIS Certified Hallmark</option>
                <option value="PENDING_APPRAISAL">Pending Laboratory Appraisal</option>
                <option value="UNHALLMARKED">Unhallmarked / Vintage Heirloom</option>
              </select>
            </div>
          </div>

          {/* Photo & Jeweller Selection */}
          <div className="space-y-4 pt-2">
            <div>
              <label className="text-xs font-semibold text-slate-300 block mb-1.5">
                Jewellery Photograph URL
              </label>
              <input
                type="url"
                value={form.primaryImageUrl}
                onChange={(e) => setForm({ ...form, primaryImageUrl: e.target.value })}
                className="w-full bg-slate-950 border border-slate-800 rounded-lg px-3.5 py-2.5 text-xs text-white placeholder-slate-500 focus:outline-none focus:border-amber-500"
              />
            </div>

            <div>
              <label className="text-xs font-semibold text-slate-300 block mb-1.5">
                Certified Verification Jeweller
              </label>
              <select
                value={form.preferredJewellerId}
                onChange={(e) => setForm({ ...form, preferredJewellerId: e.target.value })}
                className="w-full bg-slate-950 border border-slate-800 rounded-lg px-3.5 py-2.5 text-xs text-white focus:outline-none focus:border-amber-500"
              >
                {jewellers.map((j) => (
                  <option key={j.id} value={j.id}>
                    {j.businessName} ({j.city}) – Rating: {j.rating} ★
                  </option>
                ))}
              </select>
            </div>
          </div>

          <button
            type="submit"
            disabled={loading}
            className="w-full py-3 bg-gradient-to-r from-amber-500 to-yellow-500 hover:from-amber-600 hover:to-yellow-600 text-slate-950 font-bold rounded-xl text-xs shadow-lg shadow-amber-500/20 transition-all flex items-center justify-center space-x-2"
          >
            <ShieldCheck className="w-4 h-4" />
            <span>{loading ? 'Creating Digital Passport...' : 'Register Asset & Generate Digital Passport'}</span>
          </button>
        </div>

        {/* Right Live Actuarial & Valuation Preview */}
        <div className="space-y-5">
          <div className="bg-gradient-to-br from-slate-900 via-slate-900 to-amber-950/20 border border-amber-500/30 p-5 rounded-2xl">
            <div className="flex items-center space-x-2 text-amber-400 mb-3">
              <Sparkles className="w-4 h-4" />
              <h3 className="text-xs font-bold uppercase tracking-wider">Dynamic Valuation Preview</h3>
            </div>

            <div className="space-y-3 text-xs">
              <div className="flex justify-between py-1 border-b border-slate-800 text-slate-400">
                <span>Applicable Live Rate ({form.purity})</span>
                <span className="font-mono text-white">₹{ratePerGram.toLocaleString()}/g</span>
              </div>
              <div className="flex justify-between py-1 border-b border-slate-800 text-slate-400">
                <span>Net Gold Value ({netWeightNum}g)</span>
                <span className="font-mono text-white">₹{Math.round(estimatedGoldValue).toLocaleString()}</span>
              </div>
              <div className="flex justify-between py-1 border-b border-slate-800 text-slate-400">
                <span>Eligible Making Charges (12%)</span>
                <span className="font-mono text-white">₹{Math.round(estimatedMakingCharges).toLocaleString()}</span>
              </div>
              <div className="flex justify-between pt-2 text-sm font-bold text-amber-300">
                <span>Estimated Asset Value</span>
                <span className="font-mono text-base">₹{Math.round(totalEstimatedValue).toLocaleString()}</span>
              </div>
            </div>

            <div className="mt-4 p-3 bg-slate-950/60 rounded-xl border border-slate-800 text-[11px] text-slate-400 flex items-start space-x-2">
              <Info className="w-4 h-4 text-amber-400 shrink-0 mt-0.5" />
              <p>
                Asset will be minted with persistent ID (e.g. <code>GT-JWL-2026-XXXXXX</code>) and Version 1 immutable snapshot.
              </p>
            </div>
          </div>

          {/* Photo Preview */}
          {form.primaryImageUrl && (
            <div className="bg-slate-900 border border-slate-800 p-3 rounded-2xl">
              <span className="text-[10px] font-bold text-slate-400 uppercase tracking-wider block mb-2">Photo Preview</span>
              <div className="h-44 w-full rounded-xl overflow-hidden bg-slate-950">
                <img
                  src={form.primaryImageUrl}
                  alt="Asset Preview"
                  className="w-full h-full object-cover"
                />
              </div>
            </div>
          )}
        </div>
      </form>
    </div>
  );
};
