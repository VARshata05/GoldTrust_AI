import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { Gem, PlusCircle, Search, Filter, ShieldCheck, Clock, ExternalLink } from 'lucide-react';
import apiClient from '../../api/client';
import { JewelleryAsset } from '../../types';

export const JewelleryList: React.FC = () => {
  const [assets, setAssets] = useState<JewelleryAsset[]>([]);
  const [searchTerm, setSearchTerm] = useState<string>('');
  const [categoryFilter, setCategoryFilter] = useState<string>('ALL');
  const [purityFilter, setPurityFilter] = useState<string>('ALL');
  const [loading, setLoading] = useState<boolean>(true);

  useEffect(() => {
    fetchAssets();
  }, []);

  const fetchAssets = async () => {
    try {
      const resp = await apiClient.get('/jewellery');
      if (resp.data?.data) {
        setAssets(resp.data.data);
      }
    } catch (e) {
      console.error(e);
    } finally {
      setLoading(false);
    }
  };

  const filteredAssets = assets.filter((asset) => {
    const matchesSearch =
      asset.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
      asset.assetId.toLowerCase().includes(searchTerm.toLowerCase()) ||
      (asset.huid && asset.huid.toLowerCase().includes(searchTerm.toLowerCase()));

    const matchesCategory = categoryFilter === 'ALL' || asset.category.toLowerCase() === categoryFilter.toLowerCase();
    const matchesPurity = purityFilter === 'ALL' || asset.purity === purityFilter;

    return matchesSearch && matchesCategory && matchesPurity;
  });

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-white">Jewellery Asset Registry</h1>
          <p className="text-xs text-slate-400 mt-1">
            Certified digital identities, HUID tracking, and immutable lifecycle history.
          </p>
        </div>
        <Link
          to="/customer/register-jewellery"
          className="inline-flex items-center space-x-2 bg-gradient-to-r from-amber-500 to-yellow-500 text-slate-950 font-bold px-4 py-2.5 rounded-xl text-xs hover:from-amber-600 hover:to-yellow-600 transition-all shadow-lg shadow-amber-500/20"
        >
          <PlusCircle className="w-4 h-4" />
          <span>Register New Jewellery</span>
        </Link>
      </div>

      {/* Search and Filters Bar */}
      <div className="bg-slate-900/90 border border-slate-800 p-4 rounded-xl flex flex-col md:flex-row gap-3 items-center justify-between">
        <div className="relative w-full md:w-80">
          <Search className="w-4 h-4 text-slate-400 absolute left-3 top-3" />
          <input
            type="text"
            placeholder="Search by name, asset ID, or HUID..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="w-full bg-slate-950 border border-slate-800 rounded-lg pl-9 pr-3 py-2 text-xs text-white placeholder-slate-500 focus:outline-none focus:border-amber-500"
          />
        </div>

        <div className="flex items-center space-x-2 w-full md:w-auto">
          <Filter className="w-4 h-4 text-slate-400 shrink-0" />
          <select
            value={categoryFilter}
            onChange={(e) => setCategoryFilter(e.target.value)}
            className="bg-slate-950 border border-slate-800 rounded-lg px-3 py-2 text-xs text-slate-300 focus:outline-none focus:border-amber-500"
          >
            <option value="ALL">All Categories</option>
            <option value="Necklace">Necklace</option>
            <option value="Ring">Ring</option>
            <option value="Chain">Chain</option>
            <option value="Bracelet">Bracelet</option>
            <option value="Earrings">Earrings</option>
            <option value="Bangle">Bangle</option>
            <option value="Pendant">Pendant</option>
            <option value="Watch">Watch</option>
          </select>

          <select
            value={purityFilter}
            onChange={(e) => setPurityFilter(e.target.value)}
            className="bg-slate-950 border border-slate-800 rounded-lg px-3 py-2 text-xs text-slate-300 focus:outline-none focus:border-amber-500"
          >
            <option value="ALL">All Purities</option>
            <option value="24K">24K (999)</option>
            <option value="22K">22K (916)</option>
            <option value="18K">18K (750)</option>
            <option value="14K">14K (585)</option>
          </select>
        </div>
      </div>

      {/* Asset Cards Grid */}
      {loading ? (
        <p className="text-center py-12 text-xs text-slate-400">Loading jewellery portfolio...</p>
      ) : filteredAssets.length === 0 ? (
        <div className="bg-slate-900/60 border border-slate-800 rounded-2xl p-12 text-center">
          <Gem className="w-12 h-12 text-slate-600 mx-auto mb-3" />
          <h3 className="text-sm font-bold text-white">No Jewellery Assets Found</h3>
          <p className="text-xs text-slate-400 mt-1 max-w-sm mx-auto">
            {searchTerm ? 'No assets matched your search criteria.' : 'You have not registered any jewellery assets yet.'}
          </p>
          <Link
            to="/customer/register-jewellery"
            className="inline-flex items-center space-x-2 mt-4 text-xs font-bold text-amber-400 bg-amber-500/10 border border-amber-500/30 px-3.5 py-2 rounded-lg hover:bg-amber-500/20"
          >
            <PlusCircle className="w-4 h-4" />
            <span>Register First Asset</span>
          </Link>
        </div>
      ) : (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
          {filteredAssets.map((asset) => (
            <div
              key={asset.id}
              className="bg-slate-900/90 border border-slate-800 hover:border-amber-500/50 rounded-2xl overflow-hidden transition-all duration-300 flex flex-col justify-between group shadow-lg"
            >
              <div>
                {/* Photo Banner with Badges */}
                <div className="relative h-48 w-full bg-slate-950 overflow-hidden">
                  <img
                    src={asset.primaryImageUrl}
                    alt={asset.name}
                    className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-500"
                  />
                  <div className="absolute inset-0 bg-gradient-to-t from-slate-950 via-transparent to-transparent opacity-80" />

                  {/* Version Badge */}
                  <div className="absolute top-3 right-3 bg-slate-950/80 backdrop-blur border border-amber-500/40 px-2.5 py-1 rounded-md text-[11px] font-mono font-bold text-amber-300 flex items-center space-x-1">
                    <Clock className="w-3 h-3 text-amber-400" />
                    <span>v{asset.currentVersion}</span>
                  </div>

                  {/* Verification Status */}
                  <div className="absolute top-3 left-3">
                    {asset.verificationStatus === 'VERIFIED' ? (
                      <span className="bg-emerald-950/80 backdrop-blur border border-emerald-500/40 text-emerald-400 text-[10px] font-bold px-2.5 py-1 rounded-md flex items-center space-x-1">
                        <ShieldCheck className="w-3 h-3" />
                        <span>BIS VERIFIED</span>
                      </span>
                    ) : (
                      <span className="bg-amber-950/80 backdrop-blur border border-amber-500/40 text-amber-400 text-[10px] font-bold px-2.5 py-1 rounded-md">
                        PENDING VERIFICATION
                      </span>
                    )}
                  </div>

                  {/* HUID Badge */}
                  {asset.huid && (
                    <div className="absolute bottom-3 left-3 text-[11px] font-mono text-slate-300 bg-slate-900/80 backdrop-blur px-2 py-0.5 rounded border border-slate-700">
                      HUID: <span className="text-white font-bold">{asset.huid}</span>
                    </div>
                  )}
                </div>

                {/* Details Body */}
                <div className="p-5">
                  <span className="text-[10px] font-mono font-semibold text-amber-400 tracking-wider uppercase">
                    {asset.assetId}
                  </span>
                  <h3 className="text-sm font-bold text-white mt-1 group-hover:text-amber-400 transition-colors line-clamp-1">
                    {asset.name}
                  </h3>

                  <div className="grid grid-cols-2 gap-2 mt-4 text-xs bg-slate-950/50 p-3 rounded-xl border border-slate-800/80">
                    <div>
                      <span className="text-[10px] text-slate-500 block uppercase">Purity & Type</span>
                      <strong className="text-slate-200">{asset.purity} • {asset.category}</strong>
                    </div>
                    <div>
                      <span className="text-[10px] text-slate-500 block uppercase">Net Gold Weight</span>
                      <strong className="text-amber-400 font-mono">{asset.netGoldWeight} grams</strong>
                    </div>
                  </div>
                </div>
              </div>

              {/* Card Footer with Valuation & Action */}
              <div className="p-5 pt-0">
                <div className="pt-3 border-t border-slate-800/80 flex items-center justify-between">
                  <div>
                    <span className="text-[10px] text-slate-500 uppercase block">Current Est. Value</span>
                    <span className="text-base font-bold text-white font-mono">
                      ₹{asset.currentEstimatedValue ? asset.currentEstimatedValue.toLocaleString() : '—'}
                    </span>
                  </div>
                  <Link
                    to={`/customer/jewellery/${asset.assetId}`}
                    className="inline-flex items-center space-x-1 px-3 py-1.5 rounded-lg bg-slate-800 hover:bg-amber-500 hover:text-slate-950 text-xs font-semibold text-slate-200 transition-colors"
                  >
                    <span>Digital Passport</span>
                    <ExternalLink className="w-3.5 h-3.5" />
                  </Link>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};
