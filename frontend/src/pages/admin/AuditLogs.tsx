import React, { useState, useEffect } from 'react';
import { History, ShieldCheck, User } from 'lucide-react';
import apiClient from '../../api/client';
import { AuditEvent } from '../../types';

export const AuditLogs: React.FC = () => {
  const [logs, setLogs] = useState<AuditEvent[]>([]);
  const [loading, setLoading] = useState<boolean>(true);

  useEffect(() => {
    fetchLogs();
  }, []);

  const fetchLogs = async () => {
    try {
      const resp = await apiClient.get('/audit');
      if (resp.data?.data) {
        setLogs(resp.data.data);
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
        <h1 className="text-2xl font-bold text-white">Regulatory Audit Trails</h1>
        <p className="text-xs text-slate-400 mt-1">
          Immutable event log tracking all underwriting decisions, claims adjudications, and asset lifecycle events.
        </p>
      </div>

      <div className="bg-slate-900/90 border border-slate-800 rounded-2xl p-6">
        <h3 className="text-sm font-bold text-white uppercase tracking-wider mb-4 flex items-center space-x-2">
          <History className="w-4 h-4 text-amber-400" />
          <span>System Audit Log</span>
        </h3>

        {loading ? (
          <p className="text-center py-8 text-xs text-slate-400">Loading audit records...</p>
        ) : logs.length === 0 ? (
          <p className="text-center py-8 text-xs text-slate-400">No audit events recorded yet.</p>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs">
              <thead className="bg-slate-950 text-slate-400 border-b border-slate-800">
                <tr>
                  <th className="py-3 px-3 font-semibold uppercase">Timestamp</th>
                  <th className="py-3 px-3 font-semibold uppercase">Action</th>
                  <th className="py-3 px-3 font-semibold uppercase">User</th>
                  <th className="py-3 px-3 font-semibold uppercase">Entity</th>
                  <th className="py-3 px-3 font-semibold uppercase">Entity ID</th>
                  <th className="py-3 px-3 font-semibold uppercase">IP Address</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-800/60 text-slate-300">
                {logs.map((log, idx) => (
                  <tr key={idx} className="hover:bg-slate-800/30">
                    <td className="py-3 px-3 font-mono text-slate-400">{new Date(log.createdAt).toLocaleString()}</td>
                    <td className="py-3 px-3 font-bold text-amber-400">{log.action}</td>
                    <td className="py-3 px-3 font-semibold text-white">{log.username || 'System'}</td>
                    <td className="py-3 px-3">{log.entityName}</td>
                    <td className="py-3 px-3 font-mono text-slate-300">{log.entityId}</td>
                    <td className="py-3 px-3 font-mono text-slate-500">{log.ipAddress || '127.0.0.1'}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
};
