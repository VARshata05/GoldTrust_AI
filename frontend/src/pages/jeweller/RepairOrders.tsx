import React, { useState, useEffect } from 'react';
import { Wrench, CheckCircle2, AlertTriangle, ArrowRight, Clock, Camera } from 'lucide-react';
import apiClient from '../../api/client';
import { RepairOrder } from '../../types';

export const RepairOrders: React.FC = () => {
  const [orders, setOrders] = useState<RepairOrder[]>([]);
  const [selectedOrder, setSelectedOrder] = useState<RepairOrder | null>(null);
  const [statusInput, setStatusInput] = useState<string>('IN_PROGRESS');
  const [notesInput, setNotesInput] = useState<string>('');
  const [postWeight, setPostWeight] = useState<string>('');
  const [postPhotoUrl, setPostPhotoUrl] = useState<string>('');
  const [loading, setLoading] = useState<boolean>(true);
  const [msg, setMsg] = useState<string | null>(null);

  useEffect(() => {
    loadOrders();
  }, []);

  const loadOrders = async () => {
    try {
      const resp = await apiClient.get('/repairs');
      if (resp.data?.data) {
        setOrders(resp.data.data);
      }
    } catch (e) {
      console.error(e);
    } finally {
      setLoading(false);
    }
  };

  const handleUpdateStatus = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedOrder) return;

    try {
      await apiClient.patch(`/repairs/${selectedOrder.id}/status`, {
        status: statusInput,
        notes: notesInput,
        postRepairWeight: postWeight ? parseFloat(postWeight) : undefined,
        postRepairImageUrl: postPhotoUrl || undefined,
      });

      setMsg(`Repair Order ${selectedOrder.orderNumber} updated to ${statusInput}`);
      setSelectedOrder(null);
      await loadOrders();
      setTimeout(() => setMsg(null), 3000);
    } catch (e) {
      console.error(e);
    }
  };

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-white">Certified Repair & Restoration Orders</h1>
        <p className="text-xs text-slate-400 mt-1">
          Orchestrate cashless restoration orders dispatched by Insurance Officers.
        </p>
      </div>

      {msg && (
        <div className="p-4 bg-emerald-950/60 border border-emerald-500/50 rounded-xl text-emerald-300 text-xs">
          {msg}
        </div>
      )}

      {loading ? (
        <p className="text-center py-12 text-xs text-slate-400">Loading repair queue...</p>
      ) : orders.length === 0 ? (
        <div className="bg-slate-900/60 border border-slate-800 rounded-2xl p-12 text-center">
          <Wrench className="w-12 h-12 text-slate-600 mx-auto mb-3" />
          <h3 className="text-sm font-bold text-white">No Active Repair Orders</h3>
          <p className="text-xs text-slate-400 mt-1">There are currently no repair orders assigned to your workshop.</p>
        </div>
      ) : (
        <div className="space-y-4">
          {orders.map((o) => (
            <div
              key={o.id}
              className="bg-slate-900/90 border border-slate-800 p-5 rounded-2xl shadow-lg flex flex-col md:flex-row md:items-center justify-between gap-4"
            >
              <div>
                <div className="flex items-center space-x-2 mb-1.5">
                  <span className="font-mono text-xs font-bold text-amber-400 bg-amber-500/10 border border-amber-500/30 px-2.5 py-0.5 rounded">
                    {o.orderNumber}
                  </span>
                  <span className={`text-[10px] font-bold px-2 py-0.5 rounded-full ${
                    o.status === 'COMPLETED' || o.status === 'VERIFIED'
                      ? 'bg-emerald-500/10 text-emerald-400 border border-emerald-500/30'
                      : 'bg-blue-500/10 text-blue-400 border border-blue-500/30'
                  }`}>
                    {o.status}
                  </span>
                  <span className="text-[11px] font-mono text-slate-400">Claim: {o.claimNumber}</span>
                </div>

                <h3 className="text-sm font-bold text-white">{o.assetName} ({o.assetId})</h3>
                <p className="text-xs text-slate-300 mt-1">Damage: {o.damageDescription}</p>

                <div className="flex items-center space-x-4 text-[11px] text-slate-400 mt-2">
                  <span>Approved Budget: <strong className="text-amber-300 font-mono">₹{o.approvedCost.toLocaleString()}</strong></span>
                  {o.postRepairWeight && (
                    <span>Post-Repair Weight: <strong className="text-emerald-400 font-mono">{o.postRepairWeight}g</strong></span>
                  )}
                </div>
              </div>

              <button
                onClick={() => {
                  setSelectedOrder(o);
                  setStatusInput(o.status === 'ESTIMATE_SUBMITTED' ? 'IN_PROGRESS' : 'COMPLETED');
                  setNotesInput(o.jewellerNotes || '');
                }}
                className="px-4 py-2 bg-slate-800 hover:bg-amber-500 hover:text-slate-950 text-slate-200 text-xs font-bold rounded-xl transition-colors whitespace-nowrap"
              >
                Update Workshop Status
              </button>
            </div>
          ))}
        </div>
      )}

      {/* Update Repair Modal */}
      {selectedOrder && (
        <div className="fixed inset-0 bg-slate-950/80 backdrop-blur-sm z-50 flex items-center justify-center p-4">
          <div className="bg-slate-900 border border-slate-800 rounded-2xl max-w-md w-full p-6 shadow-2xl">
            <h3 className="text-base font-bold text-white mb-1">Update Repair Progress</h3>
            <p className="text-xs text-slate-400 mb-4 font-mono">{selectedOrder.orderNumber}</p>

            <form onSubmit={handleUpdateStatus} className="space-y-4">
              <div>
                <label className="text-xs font-semibold text-slate-300 block mb-1">Workshop Status *</label>
                <select
                  value={statusInput}
                  onChange={(e) => setStatusInput(e.target.value)}
                  className="w-full bg-slate-950 border border-slate-800 rounded-lg p-2.5 text-xs text-white focus:outline-none focus:border-amber-500"
                >
                  <option value="ESTIMATE_SUBMITTED">Estimate Submitted</option>
                  <option value="IN_PROGRESS">Repair In Progress</option>
                  <option value="COMPLETED">Repair Completed (Restoration Finalized)</option>
                  <option value="VERIFIED">Quality & Hallmark Verified</option>
                </select>
              </div>

              <div>
                <label className="text-xs font-semibold text-slate-300 block mb-1">Post-Repair Verified Net Weight (grams)</label>
                <input
                  type="number"
                  step="0.001"
                  placeholder="e.g. 45.450"
                  value={postWeight}
                  onChange={(e) => setPostWeight(e.target.value)}
                  className="w-full bg-slate-950 border border-slate-800 rounded-lg p-2.5 text-xs text-white focus:outline-none focus:border-amber-500 font-mono"
                />
              </div>

              <div>
                <label className="text-xs font-semibold text-slate-300 block mb-1">Post-Repair Evidence Photo URL</label>
                <input
                  type="url"
                  placeholder="URL to photo after restoration"
                  value={postPhotoUrl}
                  onChange={(e) => setPostPhotoUrl(e.target.value)}
                  className="w-full bg-slate-950 border border-slate-800 rounded-lg p-2.5 text-xs text-white focus:outline-none focus:border-amber-500"
                />
              </div>

              <div>
                <label className="text-xs font-semibold text-slate-300 block mb-1">Jeweller Technical Notes</label>
                <textarea
                  rows={2}
                  placeholder="e.g. Gemstone reseated with 18K gold prongs. Polished."
                  value={notesInput}
                  onChange={(e) => setNotesInput(e.target.value)}
                  className="w-full bg-slate-950 border border-slate-800 rounded-lg p-2.5 text-xs text-white focus:outline-none focus:border-amber-500"
                />
              </div>

              <div className="flex items-center space-x-3 pt-2">
                <button
                  type="button"
                  onClick={() => setSelectedOrder(null)}
                  className="flex-1 py-2.5 bg-slate-800 hover:bg-slate-700 text-slate-300 text-xs font-semibold rounded-xl"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="flex-1 py-2.5 bg-gradient-to-r from-amber-500 to-yellow-500 hover:from-amber-600 hover:to-yellow-600 text-slate-950 text-xs font-bold rounded-xl shadow-lg shadow-amber-500/20"
                >
                  Save Status
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

