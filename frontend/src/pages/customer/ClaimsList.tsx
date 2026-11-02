import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { ShieldAlert, PlusCircle, ArrowUpRight, Calendar, Gem } from 'lucide-react';
import apiClient from '../../api/client';
import { Claim } from '../../types';

export const ClaimsList: React.FC = () => {
  const [claims, setClaims] = useState<Claim[]>([]);
  const [loading, setLoading] = useState<boolean>(true);

  useEffect(() => {
    fetchClaims();
  }, []);

  const fetchClaims = async () => {
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

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-white">Track Insurance Claims</h1>
          <p className="text-xs text-slate-400 mt-1">
            Guidewire ClaimCenter aligned workflow tracking with TAGE AI evidence verification.
          </p>
        </div>
        <Link
          to="/customer/submit-claim"
          className="inline-flex items-center space-x-2 bg-gradient-to-r from-red-600 to-amber-600 hover:from-red-700 hover:to-amber-700 text-white font-bold px-4 py-2.5 rounded-xl text-xs transition-all shadow-lg shadow-red-600/20"
        >
          <PlusCircle className="w-4 h-4" />
          <span>File New Claim</span>
        </Link>
      </div>

      {loading ? (
        <p className="text-center py-12 text-xs text-slate-400">Loading claims history...</p>
      ) : claims.length === 0 ? (
        <div className="bg-slate-900/60 border border-slate-800 rounded-2xl p-12 text-center">
          <ShieldAlert className="w-12 h-12 text-slate-600 mx-auto mb-3" />
          <h3 className="text-sm font-bold text-white">No Claims Registered</h3>
          <p className="text-xs text-slate-400 mt-1 max-w-sm mx-auto">
            You do not have any active or historical claims filed under your policies.
          </p>
        </div>
      ) : (
        <div className="space-y-4">
          {claims.map((claim) => (
            <div
              key={claim.id}
              className="bg-slate-900/90 border border-slate-800 hover:border-amber-500/40 rounded-2xl p-5 transition-all shadow-lg flex flex-col md:flex-row md:items-center justify-between gap-4"
            >
              <div>
                <div className="flex items-center space-x-2 mb-1.5">
                  <span className="font-mono text-xs font-bold text-amber-400 bg-amber-500/10 border border-amber-500/30 px-2.5 py-0.5 rounded">
                    {claim.claimNumber}
                  </span>
                  <span className={`text-[10px] font-bold px-2 py-0.5 rounded-full ${
                    claim.status === 'APPROVED' || claim.status === 'SETTLED'
                      ? 'bg-emerald-500/10 text-emerald-400 border border-emerald-500/30'
                      : claim.status === 'REJECTED'
                      ? 'bg-red-500/10 text-red-400 border border-red-500/30'
                      : 'bg-amber-500/10 text-amber-400 border border-amber-500/30'
                  }`}>
                    {claim.status}
                  </span>
                  <span className="text-[11px] text-slate-400 font-mono">Policy: {claim.policyNumber}</span>
                </div>

                <h3 className="text-sm font-bold text-white">{claim.assetName}</h3>
                <p className="text-xs text-slate-400 mt-0.5 line-clamp-1">{claim.incidentDescription}</p>

                <div className="flex items-center space-x-4 text-[11px] text-slate-500 mt-2">
                  <span>Incident: <strong>{claim.incidentType}</strong></span>
                  <span>Date: {claim.incidentDate}</span>
                  <span>Location: {claim.incidentLocation}</span>
                </div>
              </div>

              <div className="flex items-center justify-between md:justify-end space-x-6 border-t md:border-t-0 pt-3 md:pt-0 border-slate-800">
                <div className="text-right">
                  <span className="text-[10px] text-slate-500 uppercase block">Claimed Value</span>
                  <span className="text-base font-bold text-white font-mono">₹{claim.claimedAmount.toLocaleString()}</span>
                </div>
                <Link
                  to={`/customer/claims/${claim.claimNumber}`}
                  className="px-3.5 py-2 bg-slate-800 hover:bg-amber-500 hover:text-slate-950 text-xs font-semibold text-slate-200 rounded-xl transition-colors flex items-center space-x-1"
                >
                  <span>Track Adjudication</span>
                  <ArrowUpRight className="w-3.5 h-3.5" />
                </Link>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

