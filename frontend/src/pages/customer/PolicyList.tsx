import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { FileCheck2, ShieldCheck, Gem, Calendar, ArrowUpRight, Clock, PlusCircle } from 'lucide-react';
import apiClient from '../../api/client';
import { InsurancePolicy } from '../../types';

export const PolicyList: React.FC = () => {
  const [policies, setPolicies] = useState<InsurancePolicy[]>([]);
  const [loading, setLoading] = useState<boolean>(true);

  useEffect(() => {
    fetchPolicies();
  }, []);

  const fetchPolicies = async () => {
    try {
      const resp = await apiClient.get('/policies');
      if (resp.data?.data) {
        setPolicies(resp.data.data);
      }
    } catch (e) {
      console.error(e);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-white">Active Insurance Policies</h1>
          <p className="text-xs text-slate-400 mt-1">
            Guidewire-aligned policy administration, multi-asset endorsements, and automated renewal schedules.
          </p>
        </div>
        <Link
          to="/customer/quotation"
          className="inline-flex items-center space-x-2 bg-gradient-to-r from-amber-500 to-yellow-500 text-slate-950 font-bold px-4 py-2.5 rounded-xl text-xs hover:from-amber-600 hover:to-yellow-600 transition-all shadow-lg shadow-amber-500/20"
        >
          <PlusCircle className="w-4 h-4" />
          <span>New Policy Quotation</span>
        </Link>
      </div>

      {loading ? (
        <p className="text-center py-12 text-xs text-slate-400">Loading active policies...</p>
      ) : policies.length === 0 ? (
        <div className="bg-slate-900/60 border border-slate-800 rounded-2xl p-12 text-center">
          <FileCheck2 className="w-12 h-12 text-slate-600 mx-auto mb-3" />
          <h3 className="text-sm font-bold text-white">No Policies Active</h3>
          <p className="text-xs text-slate-400 mt-1 max-w-sm mx-auto">
            You do not have any active insurance policies protecting your jewellery yet.
          </p>
          <Link
            to="/customer/quotation"
            className="inline-flex items-center space-x-2 mt-4 text-xs font-bold text-amber-400 bg-amber-500/10 border border-amber-500/30 px-3.5 py-2 rounded-lg hover:bg-amber-500/20"
          >
            <span>Generate Insurance Quote</span>
          </Link>
        </div>
      ) : (
        <div className="space-y-4">
          {policies.map((policy) => (
            <div
              key={policy.id}
              className="bg-slate-900/90 border border-slate-800 hover:border-amber-500/40 rounded-2xl p-6 transition-all duration-300 shadow-lg"
            >
              <div className="flex flex-col lg:flex-row lg:items-center justify-between gap-4 pb-4 border-b border-slate-800/80">
                <div>
                  <div className="flex items-center space-x-2.5 mb-1.5">
                    <span className="font-mono text-sm font-bold text-amber-400 bg-amber-500/10 border border-amber-500/30 px-2.5 py-0.5 rounded-md">
                      {policy.policyNumber}
                    </span>
                    <span className="text-[11px] font-semibold px-2 py-0.5 rounded-full bg-emerald-500/10 text-emerald-400 border border-emerald-500/30">
                      {policy.status}
                    </span>
                    <span className="text-[11px] text-slate-400 font-mono">
                      Renewal Cycle: {policy.renewalCount}
                    </span>
                  </div>
                  <h3 className="text-base font-bold text-white">{policy.productName}</h3>
                </div>

                <div className="flex items-center space-x-6 text-xs">
                  <div>
                    <span className="text-[10px] text-slate-500 uppercase block">Total Sum Insured</span>
                    <strong className="text-lg font-bold text-white font-mono">
                      ₹{policy.totalSumInsured.toLocaleString()}
                    </strong>
                  </div>
                  <div>
                    <span className="text-[10px] text-slate-500 uppercase block">Annual Premium</span>
                    <strong className="text-lg font-bold text-amber-400 font-mono">
                      ₹{policy.annualPremium.toLocaleString()}
                    </strong>
                  </div>
                </div>
              </div>

              {/* Covered Assets Grid */}
              <div className="mt-4">
                <span className="text-[11px] font-bold text-slate-400 uppercase tracking-wider block mb-2.5">
                  Covered Gold Assets ({policy.coveredAssets?.length || 0})
                </span>
                <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-3">
                  {policy.coveredAssets?.map((item) => (
                    <div
                      key={item.assetId}
                      className="bg-slate-950/60 border border-slate-800/80 p-3 rounded-xl flex items-center space-x-3"
                    >
                      {item.imageUrl ? (
                        <img src={item.imageUrl} alt="" className="w-10 h-10 rounded-lg object-cover bg-slate-900" />
                      ) : (
                        <div className="w-10 h-10 rounded-lg bg-amber-500/10 flex items-center justify-center text-amber-400">
                          <Gem className="w-5 h-5" />
                        </div>
                      )}
                      <div className="flex-1 min-w-0">
                        <h4 className="text-xs font-bold text-white truncate">{item.assetName}</h4>
                        <p className="text-[11px] text-slate-400">{item.purity} • {item.netGoldWeight}g • v{item.versionNumber}</p>
                        <p className="text-[11px] font-mono text-amber-300">Sum: ₹{item.sumInsured.toLocaleString()}</p>
                      </div>
                    </div>
                  ))}
                </div>
              </div>

              {/* Policy Terms & Coverage Footer */}
              <div className="mt-5 pt-3 border-t border-slate-800/60 flex flex-col sm:flex-row sm:items-center justify-between text-xs text-slate-400 gap-2">
                <div className="flex items-center space-x-4">
                  <span className="flex items-center space-x-1">
                    <Calendar className="w-3.5 h-3.5 text-slate-500" />
                    <span>Effective: {policy.effectiveDate}</span>
                  </span>
                  <span>Expires: {policy.expirationDate}</span>
                </div>
                <div className="flex items-center space-x-3">
                  <Link
                    to="/customer/submit-claim"
                    className="text-xs font-semibold text-slate-300 hover:text-amber-400"
                  >
                    File Claim
                  </Link>
                  <span className="text-slate-600">•</span>
                  <a
                    href={policy.policyDocumentUrl || '#'}
                    className="text-xs font-semibold text-amber-400 hover:underline"
                    target="_blank"
                    rel="noreferrer"
                  >
                    View Document
                  </a>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

