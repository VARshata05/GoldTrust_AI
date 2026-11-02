import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { Sliders, DollarSign, History, ShieldCheck, Users, TrendingUp } from 'lucide-react';
import apiClient from '../../api/client';
import { GoldRate, AuditEvent, InsuranceProduct } from '../../types';

export const AdminDashboard: React.FC = () => {
  const [rate, setRate] = useState<GoldRate | null>(null);
  const [products, setProducts] = useState<InsuranceProduct[]>([]);
  const [auditLogs, setAuditLogs] = useState<AuditEvent[]>([]);
  const [loading, setLoading] = useState<boolean>(true);

  useEffect(() => {
    loadAdminData();
  }, []);

  const loadAdminData = async () => {
    try {
      const [rateRes, prodsRes, auditRes] = await Promise.allSettled([
        apiClient.get('/valuation/rates/live'),
        apiClient.get('/products'),
        apiClient.get('/audit'),
      ]);

      if (rateRes.status === 'fulfilled' && rateRes.value.data?.data) {
        setRate(rateRes.value.data.data);
      }
      if (prodsRes.status === 'fulfilled' && prodsRes.value.data?.data) {
        setProducts(prodsRes.value.data.data);
      }
      if (auditRes.status === 'fulfilled' && auditRes.value.data?.data) {
        setAuditLogs(auditRes.value.data.data);
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
      <div className="bg-gradient-to-r from-slate-900 via-slate-900 to-amber-950/40 p-6 rounded-2xl border border-amber-500/30 shadow-xl flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <div className="inline-flex items-center space-x-1.5 px-2.5 py-0.5 rounded-full bg-amber-500/10 border border-amber-500/30 text-amber-400 text-xs font-semibold mb-2">
            <ShieldCheck className="w-3.5 h-3.5" />
            <span>Master Governance Console</span>
          </div>
          <h1 className="text-2xl font-bold text-white tracking-tight">Platform Administration</h1>
          <p className="text-xs text-slate-400 mt-1">
            Configure insurance products, adjust dynamic gold rate feeds, and monitor regulatory audit trails.
          </p>
        </div>

        <div className="flex items-center space-x-3">
          <Link
            to="/admin/products"
            className="px-4 py-2.5 bg-gradient-to-r from-amber-500 to-yellow-500 text-slate-950 font-bold rounded-xl text-xs shadow-lg shadow-amber-500/20"
          >
            Configure Coverages
          </Link>
          <Link
            to="/admin/audit"
            className="px-4 py-2.5 bg-slate-800 hover:bg-slate-700 text-white font-semibold rounded-xl text-xs border border-slate-700"
          >
            View Audit Logs
          </Link>
        </div>
      </div>

      {/* Bullion Benchmark Rates Panel */}
      <div className="bg-slate-900/90 border border-slate-800 p-6 rounded-2xl">
        <div className="flex items-center justify-between mb-4">
          <div className="flex items-center space-x-2 text-amber-400">
            <DollarSign className="w-5 h-5" />
            <h3 className="text-sm font-bold uppercase tracking-wider">Active Bullion Exchange Gold Rates</h3>
          </div>
          <span className="text-xs text-slate-400 font-mono">Source: {rate?.source || 'IBJA Live'}</span>
        </div>

        <div className="grid grid-cols-2 sm:grid-cols-4 gap-4">
          <div className="bg-slate-950/80 p-4 rounded-xl border border-slate-800">
            <span className="text-[10px] text-slate-500 uppercase block">24K Gold Rate</span>
            <span className="text-xl font-bold text-white font-mono">₹{rate?.rate24k?.toLocaleString()}/g</span>
          </div>
          <div className="bg-slate-950/80 p-4 rounded-xl border border-slate-800">
            <span className="text-[10px] text-slate-500 uppercase block">22K Gold Rate</span>
            <span className="text-xl font-bold text-amber-300 font-mono">₹{rate?.rate22k?.toLocaleString()}/g</span>
          </div>
          <div className="bg-slate-950/80 p-4 rounded-xl border border-slate-800">
            <span className="text-[10px] text-slate-500 uppercase block">18K Gold Rate</span>
            <span className="text-xl font-bold text-white font-mono">₹{rate?.rate18k?.toLocaleString()}/g</span>
          </div>
          <div className="bg-slate-950/80 p-4 rounded-xl border border-slate-800">
            <span className="text-[10px] text-slate-500 uppercase block">14K Gold Rate</span>
            <span className="text-xl font-bold text-white font-mono">₹{rate?.rate14k?.toLocaleString()}/g</span>
          </div>
        </div>
      </div>

      {/* Insurance Products Overview */}
      <div className="bg-slate-900/90 border border-slate-800 p-6 rounded-2xl">
        <h3 className="text-sm font-bold text-white uppercase tracking-wider mb-4 flex items-center space-x-2">
          <Sliders className="w-4 h-4 text-amber-400" />
          <span>Configured Insurance Plans</span>
        </h3>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          {products.map((p) => (
            <div key={p.id} className="p-4 bg-slate-950/60 border border-slate-800 rounded-xl space-y-2">
              <div className="flex items-center justify-between">
                <span className="font-mono text-xs font-bold text-amber-400">{p.productCode}</span>
                <span className="text-[10px] px-2 py-0.5 rounded font-semibold bg-emerald-500/10 text-emerald-400 border border-emerald-500/30">
                  Active
                </span>
              </div>
              <h4 className="text-sm font-bold text-white">{p.productName}</h4>
              <p className="text-xs text-slate-400 line-clamp-2">{p.description}</p>
              <div className="flex items-center justify-between text-[11px] text-slate-500 pt-2 border-t border-slate-800 font-mono">
                <span>Base Rate: {(p.baseRatePercentage * 100).toFixed(2)}%</span>
                <span>Max Sum: ₹{(p.maxSumInsured / 100000).toFixed(0)} Lakhs</span>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};
