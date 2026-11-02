import React, { useState, useEffect } from 'react';
import { useAuth } from '../../context/AuthContext';
import { Bell, ShieldCheck, User as UserIcon, LogOut, TrendingUp, Sparkles, CheckCircle2 } from 'lucide-react';
import apiClient from '../../api/client';
import { NotificationItem, GoldRate } from '../../types';

export const Navbar: React.FC = () => {
  const { user, role, logout, quickSwitchUser } = useAuth();
  const [notifications, setNotifications] = useState<NotificationItem[]>([]);
  const [unreadCount, setUnreadCount] = useState<number>(0);
  const [showNotifications, setShowNotifications] = useState<boolean>(false);
  const [liveRates, setLiveRates] = useState<GoldRate | null>(null);

  useEffect(() => {
    fetchRates();
    if (user) {
      fetchNotifications();
    }
  }, [user]);

  const fetchRates = async () => {
    try {
      const resp = await apiClient.get('/valuation/rates/live');
      if (resp.data?.data) {
        setLiveRates(resp.data.data);
      }
    } catch (e) {}
  };

  const fetchNotifications = async () => {
    try {
      const resp = await apiClient.get('/notifications');
      if (resp.data?.data) {
        setNotifications(resp.data.data);
      }
      const countResp = await apiClient.get('/notifications/unread-count');
      if (countResp.data?.data?.unreadCount !== undefined) {
        setUnreadCount(countResp.data.data.unreadCount);
      }
    } catch (e) {}
  };

  const markRead = async (id: number) => {
    try {
      await apiClient.patch(`/notifications/${id}/read`);
      setNotifications(prev => prev.map(n => n.id === id ? { ...n, read: true } : n));
      setUnreadCount(prev => Math.max(0, prev - 1));
    } catch (e) {}
  };

  return (
    <header className="bg-slate-900 border-b border-slate-800 text-white sticky top-0 z-50">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-16">
          {/* Brand Logo */}
          <div className="flex items-center space-x-3">
            <div className="w-10 h-10 rounded-xl bg-gradient-to-tr from-amber-600 via-gold-500 to-yellow-300 flex items-center justify-center shadow-lg shadow-amber-500/20">
              <ShieldCheck className="w-6 h-6 text-slate-950 stroke-[2.5]" />
            </div>
            <div>
              <div className="flex items-center space-x-1.5">
                <span className="font-serif-gold text-xl font-bold tracking-wider text-white">GoldTrust</span>
                <span className="text-xs px-1.5 py-0.5 rounded font-mono font-semibold bg-gradient-to-r from-amber-400 to-yellow-500 text-slate-950">AI</span>
              </div>
              <p className="text-[10px] text-slate-400 tracking-wider uppercase font-medium">Enterprise Jewellery Insurance</p>
            </div>
          </div>

          {/* Live Bullion Rates Ticker */}
          {liveRates && (
            <div className="hidden md:flex items-center space-x-4 bg-slate-800/80 px-3.5 py-1.5 rounded-full border border-slate-700/60 text-xs">
              <div className="flex items-center space-x-1 text-amber-400 font-semibold">
                <TrendingUp className="w-3.5 h-3.5 animate-pulse" />
                <span>Live Gold:</span>
              </div>
              <div className="flex items-center space-x-3 text-slate-300">
                <span>24K: <strong className="text-white">₹{liveRates.rate24k.toLocaleString()}/g</strong></span>
                <span className="text-slate-600">•</span>
                <span>22K: <strong className="text-amber-300">₹{liveRates.rate22k.toLocaleString()}/g</strong></span>
                <span className="text-slate-600">•</span>
                <span>18K: <strong className="text-white">₹{liveRates.rate18k.toLocaleString()}/g</strong></span>
              </div>
            </div>
          )}

          {/* Right Action Controls: Quick Switch, Notifications, User */}
          <div className="flex items-center space-x-4">
            {/* Quick Role Switcher for Academic Evaluation */}
            <div className="hidden lg:flex items-center space-x-1.5 bg-slate-800/60 p-1 rounded-lg border border-slate-700">
              <span className="text-[10px] font-bold text-slate-400 uppercase px-2">Role:</span>
              <button
                onClick={() => quickSwitchUser('customer')}
                className={`text-xs px-2.5 py-1 rounded transition-colors ${role === 'ROLE_CUSTOMER' ? 'bg-amber-500 text-slate-950 font-bold' : 'text-slate-300 hover:text-white'}`}
              >
                Customer
              </button>
              <button
                onClick={() => quickSwitchUser('jeweller')}
                className={`text-xs px-2.5 py-1 rounded transition-colors ${role === 'ROLE_JEWELLER' ? 'bg-amber-500 text-slate-950 font-bold' : 'text-slate-300 hover:text-white'}`}
              >
                Jeweller
              </button>
              <button
                onClick={() => quickSwitchUser('officer')}
                className={`text-xs px-2.5 py-1 rounded transition-colors ${role === 'ROLE_INSURANCE_OFFICER' ? 'bg-amber-500 text-slate-950 font-bold' : 'text-slate-300 hover:text-white'}`}
              >
                Officer
              </button>
              <button
                onClick={() => quickSwitchUser('admin')}
                className={`text-xs px-2.5 py-1 rounded transition-colors ${role === 'ROLE_ADMIN' ? 'bg-amber-500 text-slate-950 font-bold' : 'text-slate-300 hover:text-white'}`}
              >
                Admin
              </button>
            </div>

            {/* Notifications Dropdown */}
            <div className="relative">
              <button
                onClick={() => setShowNotifications(!showNotifications)}
                className="relative p-2 text-slate-300 hover:text-white hover:bg-slate-800 rounded-lg transition-colors"
                title="Notifications"
              >
                <Bell className="w-5 h-5" />
                {unreadCount > 0 && (
                  <span className="absolute top-1 right-1 w-4 h-4 bg-red-500 text-white text-[10px] font-bold rounded-full flex items-center justify-center animate-pulse">
                    {unreadCount}
                  </span>
                )}
              </button>

              {showNotifications && (
                <div className="absolute right-0 mt-2 w-80 bg-slate-900 border border-slate-800 rounded-xl shadow-2xl py-2 z-50">
                  <div className="px-4 py-2 border-b border-slate-800 flex items-center justify-between">
                    <span className="text-xs font-bold text-slate-200 uppercase tracking-wider">Alerts & Notifications</span>
                    <span className="text-[10px] text-amber-400 font-medium">{unreadCount} unread</span>
                  </div>
                  <div className="max-h-72 overflow-y-auto divide-y divide-slate-800/60">
                    {notifications.length === 0 ? (
                      <p className="text-xs text-slate-400 p-4 text-center">No alerts at this moment.</p>
                    ) : (
                      notifications.slice(0, 5).map((n) => (
                        <div
                          key={n.id}
                          onClick={() => markRead(n.id)}
                          className={`p-3 text-xs cursor-pointer hover:bg-slate-800/70 transition-colors ${!n.read ? 'bg-slate-800/40' : ''}`}
                        >
                          <div className="flex items-center justify-between mb-1">
                            <span className="font-semibold text-slate-200">{n.title}</span>
                            {!n.read && <span className="w-2 h-2 rounded-full bg-amber-400"></span>}
                          </div>
                          <p className="text-slate-400 leading-snug">{n.message}</p>
                        </div>
                      ))
                    )}
                  </div>
                </div>
              )}
            </div>

            {/* User Info & Logout */}
            <div className="flex items-center space-x-3 pl-3 border-l border-slate-800">
              <div className="hidden sm:block text-right">
                <p className="text-xs font-semibold text-white leading-tight">{user?.fullName || 'Demo User'}</p>
                <p className="text-[10px] text-amber-400 font-mono">
                  {role ? role.replace('ROLE_', '') : 'GUEST'}
                </p>
              </div>
              <button
                onClick={logout}
                className="p-2 text-slate-400 hover:text-red-400 hover:bg-slate-800 rounded-lg transition-colors"
                title="Sign out"
              >
                <LogOut className="w-5 h-5" />
              </button>
            </div>
          </div>
        </div>
      </div>
    </header>
  );
};

