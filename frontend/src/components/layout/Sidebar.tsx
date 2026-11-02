import React from 'react';
import { NavLink } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import {
  LayoutDashboard,
  Gem,
  PlusCircle,
  Calculator,
  FileCheck2,
  Receipt,
  FileText,
  AlertCircle,
  Wrench,
  ShieldAlert,
  ClipboardList,
  Sliders,
  DollarSign,
  History,
  FileSearch,
} from 'lucide-react';

export const Sidebar: React.FC = () => {
  const { role } = useAuth();

  const customerLinks = [
    { to: '/customer/dashboard', label: 'Portfolio Overview', icon: LayoutDashboard },
    { to: '/customer/jewellery', label: 'My Jewellery Assets', icon: Gem },
    { to: '/customer/register-jewellery', label: 'Register New Asset', icon: PlusCircle },
    { to: '/customer/quotation', label: 'Quotation Engine', icon: Calculator },
    { to: '/customer/policies', label: 'My Policies', icon: FileCheck2 },
    { to: '/customer/billing', label: 'Billing & Invoices', icon: Receipt },
    { to: '/customer/submit-claim', label: 'File Insurance Claim', icon: AlertCircle },
    { to: '/customer/claims', label: 'Track Claims', icon: FileText },
  ];

  const jewellerLinks = [
    { to: '/jeweller/dashboard', label: 'Jeweller Workbench', icon: LayoutDashboard },
    { to: '/jeweller/verifications', label: 'Verification Queue', icon: FileSearch },
    { to: '/jeweller/repairs', label: 'Repair & Restoration', icon: Wrench },
  ];

  const officerLinks = [
    { to: '/officer/dashboard', label: 'Underwriting Dashboard', icon: LayoutDashboard },
    { to: '/officer/underwriting', label: 'Applications Queue', icon: ClipboardList },
    { to: '/officer/claims', label: 'Claims Adjudication', icon: ShieldAlert },
    { to: '/officer/policies', label: 'Policies Administration', icon: FileCheck2 },
  ];

  const adminLinks = [
    { to: '/admin/dashboard', label: 'Admin Console', icon: LayoutDashboard },
    { to: '/admin/products', label: 'Products & Coverages', icon: Sliders },
    { to: '/admin/rates', label: 'Bullion Gold Rates', icon: DollarSign },
    { to: '/admin/audit', label: 'Audit Trail Logs', icon: History },
  ];

  let links = customerLinks;
  let portalTitle = 'Customer Portal';

  if (role === 'ROLE_JEWELLER') {
    links = jewellerLinks;
    portalTitle = 'Jeweller Verification Portal';
  } else if (role === 'ROLE_INSURANCE_OFFICER') {
    links = officerLinks;
    portalTitle = 'Insurance Officer Portal';
  } else if (role === 'ROLE_ADMIN') {
    links = adminLinks;
    portalTitle = 'Platform Administrator';
  }

  return (
    <aside className="w-64 bg-slate-900 border-r border-slate-800 flex flex-col shrink-0 min-h-[calc(100vh-4rem)]">
      <div className="p-4 border-b border-slate-800">
        <span className="text-[11px] font-bold text-amber-400 uppercase tracking-wider block">Workspace</span>
        <h2 className="text-sm font-semibold text-white mt-0.5">{portalTitle}</h2>
      </div>

      <nav className="flex-1 p-3 space-y-1 overflow-y-auto">
        {links.map((link) => {
          const Icon = link.icon;
          return (
            <NavLink
              key={link.to}
              to={link.to}
              className={({ isActive }) =>
                `flex items-center space-x-3 px-3 py-2.5 rounded-lg text-xs font-medium transition-all ${
                  isActive
                    ? 'bg-amber-500/10 text-amber-400 border border-amber-500/30 shadow-sm'
                    : 'text-slate-400 hover:text-slate-200 hover:bg-slate-800/60'
                }`
              }
            >
              <Icon className="w-4 h-4 shrink-0" />
              <span>{link.label}</span>
            </NavLink>
          );
        })}
      </nav>

      {/* Enterprise Architecture Footer */}
      <div className="p-4 border-t border-slate-800 bg-slate-950/40">
        <div className="flex items-center space-x-2">
          <div className="w-2 h-2 rounded-full bg-emerald-400 animate-ping" />
          <span className="text-[11px] text-slate-400 font-mono font-medium">Standalone • Live</span>
        </div>
        <p className="text-[10px] text-slate-500 mt-1">Guidewire InsuranceSuite Concept Aligned</p>
      </div>
    </aside>
  );
};

