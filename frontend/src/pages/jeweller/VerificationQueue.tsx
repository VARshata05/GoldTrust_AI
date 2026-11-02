import React, { useState, useEffect } from 'react';
import { ShieldCheck, Layers, Wrench, Check, PlusCircle, AlertTriangle, ArrowRight } from 'lucide-react';
import apiClient from '../../api/client';
import { JewelleryAsset } from '../../types';

export const VerificationQueue: React.FC = () => {
  const [assets, setAssets] = useState<JewelleryAsset[]>([]);
  const [selectedAssetForTransform, setSelectedAssetForTransform] = useState<JewelleryAsset | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [feedback, setFeedback] = useState<string | null>(null);

  // Transformation Form State
  const [transType, setTransType] = useState<string>('RESIZING');
  const [newWeight, setNewWeight] = useState<string>('');
  const [description, setDescription] = useState<string>('');
  const [newImageUrl, setNewImageUrl] = useState<string>('');

  useEffect(() => {
    loadAssets();
  }, []);

  const loadAssets = async () => {
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

  const handleVerify = async (assetId: string, approved: boolean) => {
    try {
      await apiClient.post(`/jewellery/${assetId}/verify`, {
        approved,
        notes: approved
          ? 'In-person metallurgical assay and BIS hallmark confirmed.'
          : 'Discrepancy in gold purity standard.',
      });
      setFeedback(`Asset ${assetId} verification status updated to ${approved ? 'VERIFIED' : 'REJECTED'}`);
      await loadAssets();
      setTimeout(() => setFeedback(null), 3000);
    } catch (e) {
      console.error(e);
    }
  };

  const handleRecordTransformation = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedAssetForTransform) return;

    try {
      await apiClient.post(`/jewellery/${selectedAssetForTransform.assetId}/transformations`, {
        transformationType: transType,
        newWeight: parseFloat(newWeight) || selectedAssetForTransform.netGoldWeight,
        description: description || `Certified ${transType} performed in workshop.`,
        newImageUrl: newImageUrl || selectedAssetForTransform.primaryImageUrl,
      });

      setFeedback(`Transformation recorded! Asset ${selectedAssetForTransform.assetId} advanced to version v${selectedAssetForTransform.currentVersion + 1}.`);
      setSelectedAssetForTransform(null);
      setNewWeight('');
      setDescription('');
      await loadAssets();
      setTimeout(() => setFeedback(null), 3000);
    } catch (e) {
      console.error(e);
    }
  };

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-white">Jewellery Verification & Transformation Workbench</h1>
        <p className="text-xs text-slate-400 mt-1">
          Authenticate physical hallmark integrity and record metallurgical modifications creating new digital passport versions.
        </p>
      </div>

      {feedback && (
        <div className="p-4 bg-emerald-950/60 border border-emerald-500/50 rounded-xl text-emerald-300 text-xs flex items-center space-x-2">
          <Check className="w-4 h-4 text-emerald-400" />
          <span>{feedback}</span>
        </div>
      )}

      {/* Assets Table */}
      <div className="bg-slate-900/90 border border-slate-800 rounded-2xl p-6">
        <h3 className="text-sm font-bold text-white uppercase tracking-wider mb-4">
          All Registered Jewellery Assets
        </h3>

        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs">
            <thead className="bg-slate-950 text-slate-400 border-b border-slate-800">
              <tr>
                <th className="py-3 px-3 font-semibold uppercase">Asset ID</th>
                <th className="py-3 px-3 font-semibold uppercase">Item Name</th>
                <th className="py-3 px-3 font-semibold uppercase">Version</th>
                <th className="py-3 px-3 font-semibold uppercase">Purity</th>
                <th className="py-3 px-3 font-semibold uppercase">Net Weight</th>
                <th className="py-3 px-3 font-semibold uppercase">HUID</th>
                <th className="py-3 px-3 font-semibold uppercase">Status</th>
                <th className="py-3 px-3 font-semibold uppercase text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-800/60 text-slate-300">
              {assets.map((asset) => (
                <tr key={asset.id} className="hover:bg-slate-800/30">
                  <td className="py-3 px-3 font-mono font-bold text-amber-400">{asset.assetId}</td>
                  <td className="py-3 px-3 font-semibold text-white">{asset.name}</td>
                  <td className="py-3 px-3 font-mono">v{asset.currentVersion}</td>
                  <td className="py-3 px-3">{asset.purity}</td>
                  <td className="py-3 px-3 font-mono">{asset.netGoldWeight}g</td>
                  <td className="py-3 px-3 font-mono text-emerald-400">{asset.huid || '—'}</td>
                  <td className="py-3 px-3">
                    <span className={`px-2 py-0.5 rounded text-[10px] font-bold ${
                      asset.verificationStatus === 'VERIFIED'
                        ? 'bg-emerald-500/10 text-emerald-400 border border-emerald-500/30'
                        : 'bg-amber-500/10 text-amber-400 border border-amber-500/30'
                    }`}>
                      {asset.verificationStatus}
                    </span>
                  </td>
                  <td className="py-3 px-3 text-right space-x-2">
                    {asset.verificationStatus !== 'VERIFIED' ? (
                      <button
                        onClick={() => handleVerify(asset.assetId, true)}
                        className="px-2.5 py-1 bg-emerald-600 hover:bg-emerald-700 text-white font-bold rounded-lg text-xs"
                      >
                        Verify BIS
                      </button>
                    ) : (
                      <button
                        onClick={() => {
                          setSelectedAssetForTransform(asset);
                          setNewWeight(String(asset.netGoldWeight));
                        }}
                        className="px-2.5 py-1 bg-amber-500 hover:bg-amber-600 text-slate-950 font-bold rounded-lg text-xs flex items-center space-x-1 inline-flex"
                      >
                        <Wrench className="w-3 h-3" />
                        <span>Record Transformation</span>
                      </button>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {/* Record Transformation Modal */}
      {selectedAssetForTransform && (
        <div className="fixed inset-0 bg-slate-950/80 backdrop-blur-sm z-50 flex items-center justify-center p-4">
          <div className="bg-slate-900 border border-slate-800 rounded-2xl max-w-lg w-full p-6 shadow-2xl">
            <h3 className="text-base font-bold text-white mb-1">
              Record Metallurgical Transformation
            </h3>
            <p className="text-xs text-slate-400 mb-4">
              Advances {selectedAssetForTransform.assetId} from version v{selectedAssetForTransform.currentVersion} to v{selectedAssetForTransform.currentVersion + 1} with verifiable gold continuity.
            </p>

            <form onSubmit={handleRecordTransformation} className="space-y-4">
              <div>
                <label className="text-xs font-semibold text-slate-300 block mb-1">Transformation Event Type *</label>
                <select
                  value={transType}
                  onChange={(e) => setTransType(e.target.value)}
                  className="w-full bg-slate-950 border border-slate-800 rounded-lg p-2.5 text-xs text-white focus:outline-none focus:border-amber-500"
                >
                  <option value="REPAIR">Repair (Restoration / Solder / Prong Retipping)</option>
                  <option value="RESIZING">Resizing (Ring / Chain / Bangle expansion or segment removal)</option>
                  <option value="REDESIGN">Redesign (Remounting / Motif modification)</option>
                  <option value="WEIGHT_MODIFICATION">Weight Modification (Gold addition or removal)</option>
                  <option value="STONE_REPLACEMENT">Stone Replacement (Gemstone dislodgement recovery)</option>
                  <option value="COMPONENT_REPLACEMENT">Component Replacement (Clasp / Hook / Lock replacement)</option>
                </select>
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="text-xs font-semibold text-slate-300 block mb-1">Previous Net Weight</label>
                  <input
                    type="text"
                    disabled
                    value={`${selectedAssetForTransform.netGoldWeight}g`}
                    className="w-full bg-slate-950/60 border border-slate-800 rounded-lg p-2.5 text-xs text-slate-500 font-mono"
                  />
                </div>
                <div>
                  <label className="text-xs font-semibold text-slate-300 block mb-1">New Net Weight (grams) *</label>
                  <input
                    type="number"
                    step="0.001"
                    required
                    value={newWeight}
                    onChange={(e) => setNewWeight(e.target.value)}
                    className="w-full bg-slate-950 border border-slate-800 rounded-lg p-2.5 text-xs text-white focus:outline-none focus:border-amber-500 font-mono"
                  />
                </div>
              </div>

              <div>
                <label className="text-xs font-semibold text-slate-300 block mb-1">Workshop Technical Narrative *</label>
                <textarea
                  rows={3}
                  required
                  placeholder="Document modifications performed, weight changes, and hallmark confirmation..."
                  value={description}
                  onChange={(e) => setDescription(e.target.value)}
                  className="w-full bg-slate-950 border border-slate-800 rounded-lg p-2.5 text-xs text-white focus:outline-none focus:border-amber-500"
                />
              </div>

              <div>
                <label className="text-xs font-semibold text-slate-300 block mb-1">Updated Photograph URL</label>
                <input
                  type="url"
                  placeholder="URL to post-modification photo"
                  value={newImageUrl}
                  onChange={(e) => setNewImageUrl(e.target.value)}
                  className="w-full bg-slate-950 border border-slate-800 rounded-lg p-2.5 text-xs text-white focus:outline-none focus:border-amber-500"
                />
              </div>

              <div className="flex items-center space-x-3 pt-3">
                <button
                  type="button"
                  onClick={() => setSelectedAssetForTransform(null)}
                  className="flex-1 py-2.5 bg-slate-800 hover:bg-slate-700 text-slate-300 text-xs font-semibold rounded-xl"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="flex-1 py-2.5 bg-gradient-to-r from-amber-500 to-yellow-500 hover:from-amber-600 hover:to-yellow-600 text-slate-950 text-xs font-bold rounded-xl shadow-lg shadow-amber-500/20"
                >
                  Record & Increment Version
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

