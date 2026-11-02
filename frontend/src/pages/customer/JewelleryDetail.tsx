import React, { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import {
  Gem,
  ShieldCheck,
  Clock,
  ArrowLeft,
  TrendingUp,
  AlertTriangle,
  History,
  FileCheck2,
  Calendar,
  Layers,
  Wrench,
  CheckCircle2,
} from 'lucide-react';
import apiClient from '../../api/client';
import { JewelleryAsset, ValuationRecord } from '../../types';

export const JewelleryDetail: React.FC = () => {
  const { assetId } = useParams<{ assetId: string }>();
  const [asset, setAsset] = useState<JewelleryAsset | null>(null);
  const [valuation, setValuation] = useState<ValuationRecord | null>(null);
  const [loading, setLoading] = useState<boolean>(true);

  useEffect(() => {
    if (assetId) {
      loadAssetData(assetId);
    }
  }, [assetId]);

  const loadAssetData = async (id: string) => {
    try {
      const [assetRes, valRes] = await Promise.allSettled([
        apiClient.get(`/jewellery/${id}`),
        apiClient.get(`/valuation/${id}`),
      ]);

      if (assetRes.status === 'fulfilled' && assetRes.value.data?.data) {
        setAsset(assetRes.value.data.data);
      }
      if (valRes.status === 'fulfilled' && valRes.value.data?.data) {
        setValuation(valRes.value.data.data);
      }
    } catch (e) {
      console.error(e);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return <div className="text-center py-20 text-xs text-slate-400">Loading digital identity passport...</div>;
  }

  if (!asset) {
    return (
      <div className="text-center py-20">
        <h2 className="text-base font-bold text-white">Asset Not Found</h2>
        <Link to="/customer/jewellery" className="text-xs text-amber-400 mt-2 inline-block">Return to Registry</Link>
      </div>
    );
  }

  return (
    <div className="max-w-6xl mx-auto space-y-6">
      {/* Back button */}
      <Link
        to="/customer/jewellery"
        className="inline-flex items-center space-x-1.5 text-xs text-slate-400 hover:text-white transition-colors"
      >
        <ArrowLeft className="w-4 h-4" />
        <span>Back to Assets</span>
      </Link>

      {/* Digital Passport Header */}
      <div className="bg-gradient-to-r from-slate-900 via-slate-900 to-amber-950/40 p-6 rounded-2xl border border-amber-500/30 shadow-xl flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <div className="flex items-center space-x-2 mb-2">
            <span className="font-mono text-xs font-bold text-amber-400 bg-amber-500/10 border border-amber-500/30 px-2.5 py-0.5 rounded-full">
              {asset.assetId}
            </span>
            <span className="font-mono text-xs font-bold text-amber-300 bg-slate-800 px-2 py-0.5 rounded-md border border-slate-700">
              Version v{asset.currentVersion}
            </span>
            {asset.verificationStatus === 'VERIFIED' && (
              <span className="text-xs font-semibold text-emerald-400 bg-emerald-500/10 border border-emerald-500/30 px-2.5 py-0.5 rounded-full flex items-center space-x-1">
                <ShieldCheck className="w-3.5 h-3.5" />
                <span>BIS Hallmark Authenticated</span>
              </span>
            )}
          </div>
          <h1 className="text-2xl font-bold text-white tracking-tight">{asset.name}</h1>
          <p className="text-xs text-slate-400 mt-1">
            Persistent Digital Identity Passport • Customer: {asset.customerName}
          </p>
        </div>

        <div className="flex items-center space-x-3">
          <Link
            to="/customer/quotation"
            className="inline-flex items-center space-x-2 bg-gradient-to-r from-amber-500 to-yellow-500 hover:from-amber-600 hover:to-yellow-600 text-slate-950 px-4 py-2.5 rounded-xl text-xs font-bold shadow-lg shadow-amber-500/20 transition-all"
          >
            <FileCheck2 className="w-4 h-4" />
            <span>Insure This Asset</span>
          </Link>
        </div>
      </div>

      {/* Grid: Photo & Specs + Dynamic Valuation */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Photo Card */}
        <div className="bg-slate-900/90 border border-slate-800 rounded-2xl p-4 flex flex-col">
          <div className="relative h-64 w-full rounded-xl overflow-hidden bg-slate-950 mb-4">
            <img
              src={asset.primaryImageUrl}
              alt={asset.name}
              className="w-full h-full object-cover"
            />
            {asset.huid && (
              <div className="absolute bottom-3 left-3 bg-slate-950/85 backdrop-blur px-2.5 py-1 rounded text-xs font-mono text-emerald-400 border border-emerald-500/30">
                HUID: <strong>{asset.huid}</strong>
              </div>
            )}
          </div>
          <div className="space-y-2 text-xs text-slate-300">
            <div className="flex justify-between py-1 border-b border-slate-800">
              <span className="text-slate-500">Registration Date</span>
              <span>{new Date(asset.registrationDate).toLocaleDateString()}</span>
            </div>
            <div className="flex justify-between py-1 border-b border-slate-800">
              <span className="text-slate-500">Verified By Jeweller</span>
              <span className="text-amber-400">{asset.verifiedByJewellerName || 'Tanishq Care Centre'}</span>
            </div>
            <div className="flex justify-between py-1 border-b border-slate-800">
              <span className="text-slate-500">Ownership Status</span>
              <span className="text-emerald-400 font-semibold">{asset.ownershipStatus}</span>
            </div>
          </div>
        </div>

        {/* Specifications & Live Dynamic Valuation */}
        <div className="lg:col-span-2 space-y-6">
          {/* Specifications Card */}
          <div className="bg-slate-900/90 border border-slate-800 p-5 rounded-2xl">
            <h3 className="text-xs font-bold text-slate-400 uppercase tracking-wider mb-4">
              Physical Metallurgical Specifications
            </h3>
            <div className="grid grid-cols-2 sm:grid-cols-4 gap-4 text-xs">
              <div className="bg-slate-950/60 p-3.5 rounded-xl border border-slate-800">
                <span className="text-[10px] text-slate-500 block uppercase">Category</span>
                <strong className="text-white text-sm">{asset.category}</strong>
              </div>
              <div className="bg-slate-950/60 p-3.5 rounded-xl border border-slate-800">
                <span className="text-[10px] text-slate-500 block uppercase">Gold Purity</span>
                <strong className="text-amber-300 text-sm">{asset.purity}</strong>
              </div>
              <div className="bg-slate-950/60 p-3.5 rounded-xl border border-slate-800">
                <span className="text-[10px] text-slate-500 block uppercase">Net Gold Weight</span>
                <strong className="text-amber-400 font-mono text-sm">{asset.netGoldWeight}g</strong>
              </div>
              <div className="bg-slate-950/60 p-3.5 rounded-xl border border-slate-800">
                <span className="text-[10px] text-slate-500 block uppercase">Gross Weight</span>
                <strong className="text-white font-mono text-sm">{asset.grossWeight}g</strong>
              </div>
            </div>
          </div>

          {/* Dynamic Valuation & Inflation Gap Card */}
          <div className="bg-gradient-to-br from-slate-900 to-slate-950 border border-amber-500/30 p-5 rounded-2xl">
            <div className="flex items-center justify-between mb-3">
              <div className="flex items-center space-x-2 text-amber-400">
                <TrendingUp className="w-4 h-4" />
                <h3 className="text-xs font-bold uppercase tracking-wider">Dynamic Bullion-Indexed Valuation</h3>
              </div>
              <span className="text-[11px] font-mono text-slate-400">Inflation Protection: Active</span>
            </div>

            <div className="grid grid-cols-1 sm:grid-cols-3 gap-4 my-4">
              <div className="bg-slate-950/80 p-3 rounded-xl border border-slate-800">
                <span className="text-[10px] text-slate-500 uppercase block">Current Estimated Value</span>
                <span className="text-lg font-bold text-amber-300 font-mono">
                  ₹{asset.currentEstimatedValue?.toLocaleString()}
                </span>
              </div>
              <div className="bg-slate-950/80 p-3 rounded-xl border border-slate-800">
                <span className="text-[10px] text-slate-500 uppercase block">Purchase Price</span>
                <span className="text-lg font-bold text-slate-300 font-mono">
                  ₹{asset.purchasePrice ? asset.purchasePrice.toLocaleString() : '—'}
                </span>
              </div>
              <div className="bg-slate-950/80 p-3 rounded-xl border border-slate-800">
                <span className="text-[10px] text-slate-500 uppercase block">Certified Appraisal Value</span>
                <span className="text-lg font-bold text-slate-300 font-mono">
                  ₹{asset.appraisalValue ? asset.appraisalValue.toLocaleString() : '—'}
                </span>
              </div>
            </div>

            {valuation?.gapIdentified && (
              <div className="bg-amber-950/30 border border-amber-500/40 p-3 rounded-xl text-xs text-amber-300 flex items-start space-x-2">
                <AlertTriangle className="w-4 h-4 shrink-0 text-amber-400 mt-0.5" />
                <div>
                  <p className="font-semibold">Coverage Gap Identified: ₹{valuation.coverageGap.toLocaleString()}</p>
                  <p className="text-[11px] text-slate-300 mt-0.5">{valuation.recommendation}</p>
                </div>
              </div>
            )}
          </div>
        </div>
      </div>

      {/* Transformation-Aware Lifecycle Timeline (Requirement Section 7) */}
      <div className="bg-slate-900/90 border border-slate-800 p-6 rounded-2xl">
        <div className="flex items-center justify-between mb-6">
          <div>
            <h3 className="text-sm font-bold text-white uppercase tracking-wider flex items-center space-x-2">
              <Layers className="w-4 h-4 text-amber-400" />
              <span>Transformation-Aware Digital Timeline</span>
            </h3>
            <p className="text-xs text-slate-400 mt-0.5">
              Every repair, resizing, or weight alteration creates an immutable version with verified metallurgy.
            </p>
          </div>
          <span className="text-xs font-mono font-bold text-slate-400 bg-slate-800 px-2.5 py-1 rounded-md">
            {asset.transformations?.length || 0} Recorded Events
          </span>
        </div>

        {/* Timeline Visualization */}
        <div className="relative pl-6 space-y-6 before:content-[''] before:absolute before:left-2 before:top-2 before:bottom-2 before:w-0.5 before:bg-slate-800">
          {asset.transformations && asset.transformations.length > 0 ? (
            asset.transformations.map((t, idx) => (
              <div key={t.id || idx} className="relative group">
                <div className="absolute -left-6 top-1 w-4 h-4 rounded-full bg-amber-500 border-2 border-slate-950 group-hover:scale-125 transition-transform" />
                <div className="bg-slate-950/80 border border-slate-800/80 group-hover:border-amber-500/40 p-4 rounded-xl transition-colors">
                  <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-1 mb-2">
                    <div className="flex items-center space-x-2">
                      <span className="text-xs font-bold text-white uppercase tracking-wider bg-amber-500/10 text-amber-400 px-2 py-0.5 rounded">
                        {t.transformationType}
                      </span>
                      <span className="text-xs font-mono text-slate-400">
                        Version transition: v{t.previousVersion} → v{t.newVersion}
                      </span>
                    </div>
                    <span className="text-[11px] text-slate-500 font-mono">
                      {new Date(t.createdAt).toLocaleString()}
                    </span>
                  </div>

                  <p className="text-xs text-slate-300 mb-2">{t.description}</p>

                  <div className="flex flex-wrap gap-4 text-[11px] text-slate-400 pt-2 border-t border-slate-800/60 font-mono">
                    <span>Previous Weight: <strong className="text-slate-200">{t.previousWeight}g</strong></span>
                    <span>New Weight: <strong className="text-amber-400">{t.newWeight}g</strong></span>
                    <span>Jeweller: <strong className="text-slate-200">{t.jewellerName || 'Certified Center'}</strong></span>
                    <span className="text-emerald-400">Status: {t.verificationStatus}</span>
                  </div>
                </div>
              </div>
            ))
          ) : (
            <p className="text-xs text-slate-400">No transformations recorded yet.</p>
          )}
        </div>
      </div>

      {/* Historical Versions Table (Proving versions are never overwritten) */}
      <div className="bg-slate-900/90 border border-slate-800 p-6 rounded-2xl">
        <h3 className="text-sm font-bold text-white uppercase tracking-wider flex items-center space-x-2 mb-4">
          <History className="w-4 h-4 text-amber-400" />
          <span>Immutable Historical Versions Ledger</span>
        </h3>

        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs">
            <thead className="bg-slate-950 text-slate-400 border-b border-slate-800">
              <tr>
                <th className="py-2.5 px-3 font-semibold uppercase">Version</th>
                <th className="py-2.5 px-3 font-semibold uppercase">Purity</th>
                <th className="py-2.5 px-3 font-semibold uppercase">Net Weight</th>
                <th className="py-2.5 px-3 font-semibold uppercase">Estimated Value</th>
                <th className="py-2.5 px-3 font-semibold uppercase">Change Reason</th>
                <th className="py-2.5 px-3 font-semibold uppercase">Recorded At</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-800/60 text-slate-300">
              {asset.versions?.map((v) => (
                <tr key={v.id} className="hover:bg-slate-800/30">
                  <td className="py-3 px-3 font-mono font-bold text-amber-400">v{v.versionNumber}</td>
                  <td className="py-3 px-3">{v.purity}</td>
                  <td className="py-3 px-3 font-mono">{v.netGoldWeight}g</td>
                  <td className="py-3 px-3 font-mono text-white">₹{v.estimatedValue?.toLocaleString()}</td>
                  <td className="py-3 px-3 text-slate-400">{v.changeReason}</td>
                  <td className="py-3 px-3 text-slate-500 font-mono">{new Date(v.createdAt).toLocaleDateString()}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};
