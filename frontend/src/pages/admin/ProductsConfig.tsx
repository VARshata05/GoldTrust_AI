import React, { useState, useEffect } from 'react';
import { Sliders, ShieldCheck, Check, DollarSign } from 'lucide-react';
import apiClient from '../../api/client';
import { CoverageDefinition, InsuranceProduct } from '../../types';

export const ProductsConfig: React.FC = () => {
  const [coverages, setCoverages] = useState<CoverageDefinition[]>([]);
  const [loading, setLoading] = useState<boolean>(true);

  useEffect(() => {
    fetchCoverages();
  }, []);

  const fetchCoverages = async () => {
    try {
      const resp = await apiClient.get('/products/coverages');
      if (resp.data?.data) {
        setCoverages(resp.data.data);
      }
    } catch (e) {
      console.error(e);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-white">Coverage & Product Configuration</h1>
        <p className="text-xs text-slate-400 mt-1">
          Rule-based underwriting engine configuration. Never hardcoded — all claims evaluate against active policy terms.
        </p>
      </div>

      <div className="bg-slate-900/90 border border-slate-800 rounded-2xl p-6">
        <h3 className="text-sm font-bold text-white uppercase tracking-wider mb-4 flex items-center space-x-2">
          <ShieldCheck className="w-4 h-4 text-amber-400" />
          <span>The 9 Core Configurable Jewellery Coverages</span>
        </h3>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {coverages.map((cov) => (
            <div
              key={cov.id}
              className="p-4 bg-slate-950/60 border border-slate-800 rounded-xl space-y-2 flex flex-col justify-between"
            >
              <div>
                <div className="flex items-center justify-between">
                  <span className="font-mono text-xs font-bold text-amber-400">{cov.coverageCode}</span>
                  <span className="text-[10px] font-bold px-2 py-0.5 rounded-full bg-emerald-500/10 text-emerald-400 border border-emerald-500/30">
                    Active
                  </span>
                </div>
                <h4 className="text-sm font-bold text-white mt-1">{cov.coverageName}</h4>
                <p className="text-xs text-slate-400 mt-1 leading-relaxed">{cov.description}</p>
              </div>

              <div className="pt-3 border-t border-slate-800/80 text-[11px] font-mono text-slate-400 space-y-1">
                <div className="flex justify-between">
                  <span>Coverage Limit:</span>
                  <span className="text-white font-bold">{cov.coverageLimitPercentage}%</span>
                </div>
                <div className="flex justify-between">
                  <span>Default Deductible:</span>
                  <span className="text-amber-300 font-bold">{cov.defaultDeductiblePercentage}%</span>
                </div>
                <div className="flex justify-between">
                  <span>Premium Factor:</span>
                  <span className="text-white">{cov.premiumFactor}</span>
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};
