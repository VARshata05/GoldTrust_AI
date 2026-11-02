import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Calculator, ShieldCheck, Check, Sparkles, AlertCircle, ArrowRight, Layers } from 'lucide-react';
import apiClient from '../../api/client';
import { JewelleryAsset, InsuranceProduct, CoverageDefinition, Quotation } from '../../types';

export const QuotationCalculator: React.FC = () => {
  const navigate = useNavigate();
  const [assets, setAssets] = useState<JewelleryAsset[]>([]);
  const [products, setProducts] = useState<InsuranceProduct[]>([]);
  const [coverages, setCoverages] = useState<CoverageDefinition[]>([]);
  
  const [selectedAssetIds, setSelectedAssetIds] = useState<string[]>([]);
  const [selectedProductId, setSelectedProductId] = useState<number | null>(null);
  const [selectedCoverageCodes, setSelectedCoverageCodes] = useState<string[]>([
    'COV_ACC_LOSS', 'COV_THEFT', 'COV_ACC_DAMAGE', 'COV_WW_TRAVEL', 'COV_REPAIR'
  ]);
  const [deductible, setDeductible] = useState<number>(5.0);
  const [quotation, setQuotation] = useState<Quotation | null>(null);
  const [submittingApp, setSubmittingApp] = useState<boolean>(false);
  const [loading, setLoading] = useState<boolean>(true);

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      const [assetsRes, prodsRes, covsRes] = await Promise.allSettled([
        apiClient.get('/jewellery'),
        apiClient.get('/products'),
        apiClient.get('/products/coverages'),
      ]);

      if (assetsRes.status === 'fulfilled' && assetsRes.value.data?.data) {
        const assetList: JewelleryAsset[] = assetsRes.value.data.data;
        setAssets(assetList);
        if (assetList.length > 0) {
          setSelectedAssetIds([assetList[0].assetId]);
        }
      }

      if (prodsRes.status === 'fulfilled' && prodsRes.value.data?.data) {
        const prodList: InsuranceProduct[] = prodsRes.value.data.data;
        setProducts(prodList);
        if (prodList.length > 0) {
          setSelectedProductId(prodList[0].id);
        }
      }

      if (covsRes.status === 'fulfilled' && covsRes.value.data?.data) {
        setCoverages(covsRes.value.data.data);
      }
    } catch (e) {
      console.error(e);
    } finally {
      setLoading(false);
    }
  };

  // Recalculate quotation whenever selections change
  useEffect(() => {
    if (selectedAssetIds.length > 0 && selectedProductId) {
      calculateQuotation();
    }
  }, [selectedAssetIds, selectedProductId, selectedCoverageCodes, deductible]);

  const calculateQuotation = async () => {
    try {
      const payload = {
        productId: selectedProductId,
        assetIds: selectedAssetIds,
        selectedCoverageCodes: selectedCoverageCodes,
        deductiblePercentage: deductible,
        stateOrRegion: 'Maharashtra',
      };

      const resp = await apiClient.post('/quotations', payload);
      if (resp.data?.data) {
        setQuotation(resp.data.data);
      }
    } catch (e) {
      console.error(e);
    }
  };

  const toggleAsset = (assetId: string) => {
    setSelectedAssetIds(prev =>
      prev.includes(assetId)
        ? (prev.length > 1 ? prev.filter(id => id !== assetId) : prev)
        : [...prev, assetId]
    );
  };

  const toggleCoverage = (code: string) => {
    setSelectedCoverageCodes(prev =>
      prev.includes(code)
        ? (prev.length > 1 ? prev.filter(c => c !== code) : prev)
        : [...prev, code]
    );
  };

  const handleApplyPolicy = async () => {
    if (!quotation) return;
    setSubmittingApp(true);
    try {
      const resp = await apiClient.post('/applications', {
        quotationId: quotation.id,
        customerNotes: 'Customer submitted binding application via GoldTrust automated engine.',
      });
      if (resp.data?.data) {
        navigate('/customer/policies');
      }
    } catch (e) {
      console.error(e);
    } finally {
      setSubmittingApp(false);
    }
  };

  return (
    <div className="max-w-6xl mx-auto space-y-6">
      {/* Header */}
      <div>
        <h1 className="text-2xl font-bold text-white">Quotation & Underwriting Estimator</h1>
        <p className="text-xs text-slate-400 mt-1">
          Actuarial pricing engine configured with dynamic bullion indices and Guidewire-aligned coverage factors.
        </p>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Left Column: Asset Selection & Coverages */}
        <div className="lg:col-span-2 space-y-6">
          {/* Select Assets */}
          <div className="bg-slate-900/90 border border-slate-800 p-5 rounded-2xl">
            <h3 className="text-xs font-bold text-slate-300 uppercase tracking-wider mb-3 flex items-center justify-between">
              <span>1. Select Jewellery Items to Cover</span>
              <span className="text-amber-400 font-mono text-[11px]">{selectedAssetIds.length} Selected</span>
            </h3>

            {assets.length === 0 ? (
              <p className="text-xs text-slate-400 py-3">No registered assets available. Please register jewellery first.</p>
            ) : (
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                {assets.map((asset) => {
                  const isSelected = selectedAssetIds.includes(asset.assetId);
                  return (
                    <div
                      key={asset.id}
                      onClick={() => toggleAsset(asset.assetId)}
                      className={`cursor-pointer p-3 rounded-xl border transition-all flex items-center space-x-3 ${
                        isSelected
                          ? 'bg-amber-500/10 border-amber-500/50 shadow-sm'
                          : 'bg-slate-950/60 border-slate-800 hover:border-slate-700'
                      }`}
                    >
                      <div className={`w-5 h-5 rounded flex items-center justify-center shrink-0 border ${
                        isSelected ? 'bg-amber-500 border-amber-500 text-slate-950' : 'border-slate-700'
                      }`}>
                        {isSelected && <Check className="w-3.5 h-3.5 stroke-[3]" />}
                      </div>
                      <div className="flex-1 min-w-0">
                        <h4 className="text-xs font-bold text-white truncate">{asset.name}</h4>
                        <p className="text-[11px] text-slate-400">{asset.purity} • {asset.netGoldWeight}g</p>
                        <p className="text-[11px] font-mono font-semibold text-amber-300">
                          Est. ₹{asset.currentEstimatedValue?.toLocaleString()}
                        </p>
                      </div>
                    </div>
                  );
                })}
              </div>
            )}
          </div>

          {/* Select Plan & Deductible */}
          <div className="bg-slate-900/90 border border-slate-800 p-5 rounded-2xl">
            <h3 className="text-xs font-bold text-slate-300 uppercase tracking-wider mb-3">
              2. Plan & Deductible Preference
            </h3>

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <div>
                <label className="text-xs font-semibold text-slate-300 block mb-1.5">Insurance Product</label>
                <select
                  value={selectedProductId || ''}
                  onChange={(e) => setSelectedProductId(Number(e.target.value))}
                  className="w-full bg-slate-950 border border-slate-800 rounded-lg p-2.5 text-xs text-white focus:outline-none focus:border-amber-500"
                >
                  {products.map((p) => (
                    <option key={p.id} value={p.id}>
                      {p.productName} ({p.productCode})
                    </option>
                  ))}
                </select>
              </div>

              <div>
                <label className="text-xs font-semibold text-slate-300 block mb-1.5">Deductible</label>
                <select
                  value={deductible}
                  onChange={(e) => setDeductible(Number(e.target.value))}
                  className="w-full bg-slate-950 border border-slate-800 rounded-lg p-2.5 text-xs text-white focus:outline-none focus:border-amber-500"
                >
                  <option value={2.5}>2.5% Deductible (Lowest Out-of-pocket)</option>
                  <option value={5.0}>5.0% Deductible (Standard Balanced)</option>
                  <option value={10.0}>10.0% Deductible (Economical Premium)</option>
                </select>
              </div>
            </div>
          </div>

          {/* Configurable Coverages (Section 5) */}
          <div className="bg-slate-900/90 border border-slate-800 p-5 rounded-2xl">
            <div className="flex items-center justify-between mb-3">
              <h3 className="text-xs font-bold text-slate-300 uppercase tracking-wider">
                3. Configurable Gold Coverages (The 9 Core Protections)
              </h3>
              <span className="text-[11px] text-amber-400 font-mono">{selectedCoverageCodes.length} Active</span>
            </div>

            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-3">
              {coverages.map((cov) => {
                const isSelected = selectedCoverageCodes.includes(cov.coverageCode);
                return (
                  <div
                    key={cov.id}
                    onClick={() => toggleCoverage(cov.coverageCode)}
                    className={`cursor-pointer p-3 rounded-xl border text-xs transition-all ${
                      isSelected
                        ? 'bg-amber-500/10 border-amber-500/50'
                        : 'bg-slate-950/60 border-slate-800/80 hover:border-slate-700 opacity-70'
                    }`}
                  >
                    <div className="flex items-center justify-between mb-1.5">
                      <span className="font-bold text-white text-xs">{cov.coverageName}</span>
                      <div className={`w-4 h-4 rounded flex items-center justify-center border ${
                        isSelected ? 'bg-amber-500 border-amber-500 text-slate-950' : 'border-slate-700'
                      }`}>
                        {isSelected && <Check className="w-3 h-3 stroke-[3]" />}
                      </div>
                    </div>
                    <p className="text-[11px] text-slate-400 line-clamp-2 leading-relaxed">
                      {cov.description}
                    </p>
                  </div>
                );
              })}
            </div>
          </div>
        </div>

        {/* Right Column: Actuarial Formula Breakdown & Apply Button */}
        <div className="space-y-6">
          <div className="bg-gradient-to-br from-slate-900 via-slate-900 to-amber-950/30 border border-amber-500/30 p-6 rounded-2xl shadow-xl">
            <div className="flex items-center space-x-2 text-amber-400 mb-4">
              <Calculator className="w-5 h-5" />
              <h3 className="text-xs font-bold uppercase tracking-wider">Premium Breakdown</h3>
            </div>

            {quotation ? (
              <div className="space-y-4">
                <div className="text-center py-2 bg-slate-950/60 rounded-xl border border-slate-800">
                  <span className="text-[11px] text-slate-400 uppercase tracking-wider block">Estimated Annual Premium</span>
                  <span className="text-3xl font-extrabold text-amber-300 font-mono">
                    ₹{quotation.annualPremium.toLocaleString()}
                  </span>
                  <span className="text-[11px] text-slate-400 block mt-0.5">
                    or ₹{quotation.monthlyEquivalent.toLocaleString()}/month
                  </span>
                </div>

                {/* Formula Breakdown as required by Section 8 */}
                <div className="p-3.5 bg-slate-950/80 rounded-xl border border-slate-800/80 text-[11px] space-y-2">
                  <span className="text-[10px] font-bold text-slate-400 uppercase tracking-wider block">
                    Actuarial Formula Factor Breakdown
                  </span>
                  <div className="flex justify-between text-slate-400">
                    <span>Sum Insured:</span>
                    <span className="font-mono text-white">₹{quotation.totalInsuredValue.toLocaleString()}</span>
                  </div>
                  <div className="flex justify-between text-slate-400">
                    <span>Base Rate:</span>
                    <span className="font-mono text-white">{(quotation.baseRate * 100).toFixed(2)}%</span>
                  </div>
                  <div className="flex justify-between text-slate-400">
                    <span>Risk Factor:</span>
                    <span className="font-mono text-white">{quotation.riskFactor}</span>
                  </div>
                  <div className="flex justify-between text-slate-400">
                    <span>Coverage Factor:</span>
                    <span className="font-mono text-white">{quotation.coverageFactor}</span>
                  </div>
                  <div className="flex justify-between text-slate-400">
                    <span>Location Factor:</span>
                    <span className="font-mono text-white">{quotation.locationFactor}</span>
                  </div>
                </div>

                {/* Covered Assets Count */}
                <div className="text-[11px] text-slate-400 px-1">
                  Covering <strong>{quotation.items?.length}</strong> jewellery assets under <strong>{quotation.productName}</strong>.
                </div>

                <button
                  onClick={handleApplyPolicy}
                  disabled={submittingApp}
                  className="w-full py-3 bg-gradient-to-r from-amber-500 to-yellow-500 hover:from-amber-600 hover:to-yellow-600 text-slate-950 font-bold rounded-xl text-xs shadow-lg shadow-amber-500/20 transition-all flex items-center justify-center space-x-2"
                >
                  <span>{submittingApp ? 'Submitting to Underwriter...' : 'Submit Application for Underwriting'}</span>
                  <ArrowRight className="w-4 h-4" />
                </button>

                <p className="text-[10px] text-slate-500 italic text-center leading-tight">
                  {quotation.disclaimer}
                </p>
              </div>
            ) : (
              <p className="text-xs text-slate-400 text-center py-8">Select assets to generate estimate.</p>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

