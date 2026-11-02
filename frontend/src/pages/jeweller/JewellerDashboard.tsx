import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { ShieldCheck, FileSearch, Wrench, CheckCircle2, Clock, Sparkles } from 'lucide-react';
import apiClient from '../../api/client';
import { JewelleryAsset, RepairOrder, Jeweller } from '../../types';

export const JewellerDashboard: React.FC = () => {
  const [profile, setProfile] = useState<Jeweller | null>(null);
  const [pendingAssets, setPendingAssets] = useState<JewelleryAsset[]>([]);
  const [repairs, setRepairs] = useState<RepairOrder[]>([]);
  const [loading, setLoading] = useState<boolean>(true);

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      const [profRes, pendingRes, repRes] = await Promise.allSettled([
        apiClient.get('/jewellers/me'),
        apiClient.get('/jewellery/pending-verification'),
        apiClient.get('/repairs'),
      ]);

      if (profRes.status === 'fulfilled' && profRes.value.data?.data) {
        setProfile(profRes.value.data.data);
      }
      if (pendingRes.status === 'fulfilled' && pendingRes.value.data?.data) {
        setPendingAssets(pendingRes.value.data.data);
      }
      if (repRes.status === 'fulfilled' && repRes.value.data?.data) {
        setRepairs(repRes.value.data.data);
      }
    } catch (e) {
      console.error(e);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="space-y-6">
      {/* Banner */}
      <div className="bg-gradient-to-r from-slate-900 via-slate-900 to-amber-950/40 p-6 rounded-2xl border border-amber-500/30 shadow-xl">
        <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
          <div>
            <div className="inline-flex items-center space-x-1.5 px-2.5 py-0.5 rounded-full bg-emerald-500/10 border border-emerald-500/30 text-emerald-400 text-xs font-semibold mb-2">
              <ShieldCheck className="w-3.5 h-3.5" />
              <span>Certified BIS Hallmark Centre</span>
            </div>
            <h1 className="text-2xl font-bold text-white">
              {profile?.businessName || 'Tanishq Hallmark Care & Valuation Centre'}
            </h1>
            <p className="text-xs text-slate-400 mt-1">
              BIS Center ID: <strong className="text-amber-400">{profile?.bisHallmarkCenterId || 'AHC-MUM-4001'}</strong> • License: {profile?.licenseNumber}
            </p>
          </div>
          <div className="flex items-center space-x-3">
            <Link
              to="/jeweller/verifications"
              className="px-4 py-2 bg-gradient-to-r from-amber-500 to-yellow-500 text-slate-950 text-xs font-bold rounded-xl shadow-lg shadow-amber-500/20"
            >
              Verify Queue ({pendingAssets.length})
            </Link>
          </div>
        </div>
      </div>

      {/* Metrics Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
        <div className="bg-slate-900/90 border border-slate-800 p-5 rounded-xl">
          <div className="flex items-center justify-between">
            <span className="text-xs font-semibold text-slate-400">Awaiting Physical Verification</span>
            <FileSearch className="w-4 h-4 text-amber-400" />
          </div>
          <p className="text-2xl font-bold text-white mt-2">{pendingAssets.length}</p>
          <p className="text-[11px] text-slate-500 mt-1">Hallmark laser scan & weight assay</p>
        </div>

        <div className="bg-slate-900/90 border border-slate-800 p-5 rounded-xl">
          <div className="flex items-center justify-between">
            <span className="text-xs font-semibold text-slate-400">Assigned Repair Orders</span>
            <Wrench className="w-4 h-4 text-blue-400" />
          </div>
          <p className="text-2xl font-bold text-white mt-2">{repairs.length}</p>
          <p className="text-[11px] text-slate-500 mt-1">
            {repairs.filter(r => r.status === 'IN_PROGRESS' || r.status === 'ESTIMATE_SUBMITTED').length} active in workshop
          </p>
        </div>

        <div className="bg-slate-900/90 border border-slate-800 p-5 rounded-xl">
          <div className="flex items-center justify-between">
            <span className="text-xs font-semibold text-slate-400">Jeweller Trust Rating</span>
            <Sparkles className="w-4 h-4 text-yellow-400" />
          </div>
          <p className="text-2xl font-bold text-white mt-2">{profile?.rating || '4.90'} ★</p>
          <p className="text-[11px] text-emerald-400 mt-1">Network Certified</p>
        </div>
      </div>

      {/* Verification Queue Preview */}
      <div className="bg-slate-900/90 border border-slate-800 p-6 rounded-2xl">
        <div className="flex items-center justify-between mb-4">
          <div>
            <h3 className="text-sm font-bold text-white uppercase tracking-wider">
              Pending Asset Verifications
            </h3>
            <p className="text-xs text-slate-400">Incoming assets registered by customers requiring certified hallmark authentication</p>
          </div>
          <Link to="/jeweller/verifications" className="text-xs font-semibold text-amber-400 hover:underline">
            View All
          </Link>
        </div>

        {pendingAssets.length === 0 ? (
          <div className="text-center py-8 text-xs text-slate-400 bg-slate-950/40 rounded-xl">
            <CheckCircle2 className="w-8 h-8 text-emerald-500 mx-auto mb-2" />
            <p>Verification queue is clear! All customer gold assets are verified.</p>
          </div>
        ) : (
          <div className="space-y-3">
            {pendingAssets.slice(0, 3).map((a) => (
              <div key={a.id} className="p-4 bg-slate-950/60 border border-slate-800 rounded-xl flex items-center justify-between">
                <div>
                  <div className="flex items-center space-x-2">
                    <span className="font-mono text-xs font-bold text-amber-400">{a.assetId}</span>
                    <span className="text-xs text-white font-bold">{a.name}</span>
                  </div>
                  <p className="text-xs text-slate-400 mt-0.5">{a.category} • {a.purity} • Net Weight: {a.netGoldWeight}g • HUID: {a.huid || 'Pending'}</p>
                </div>
                <Link
                  to="/jeweller/verifications"
                  className="px-3 py-1.5 bg-amber-500 hover:bg-amber-600 text-slate-950 text-xs font-bold rounded-lg transition-colors"
                >
                  Verify Now
                </Link>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

