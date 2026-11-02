import React, { useState, useEffect } from 'react';
import { Receipt, CreditCard, ShieldCheck, CheckCircle2, ArrowRight, Download, Clock } from 'lucide-react';
import apiClient from '../../api/client';
import { BillingAccount, Invoice } from '../../types';

export const BillingPayments: React.FC = () => {
  const [billing, setBilling] = useState<BillingAccount | null>(null);
  const [selectedInvoice, setSelectedInvoice] = useState<Invoice | null>(null);
  const [paymentMethod, setPaymentMethod] = useState<string>('SIMULATED_UPI');
  const [processing, setProcessing] = useState<boolean>(false);
  const [paymentSuccess, setPaymentSuccess] = useState<string | null>(null);
  const [loading, setLoading] = useState<boolean>(true);

  useEffect(() => {
    fetchBillingData();
  }, []);

  const fetchBillingData = async () => {
    try {
      const resp = await apiClient.get('/billing/accounts/me');
      if (resp.data?.data) {
        setBilling(resp.data.data);
      }
    } catch (e) {
      console.error(e);
    } finally {
      setLoading(false);
    }
  };

  const handlePayInvoice = async () => {
    if (!selectedInvoice) return;
    setProcessing(true);
    setPaymentSuccess(null);

    try {
      const resp = await apiClient.post('/billing/payments', {
        invoiceId: selectedInvoice.id,
        amount: selectedInvoice.totalAmount,
        paymentMethod: paymentMethod,
      });

      if (resp.data?.data) {
        setPaymentSuccess(`Payment successful! Reference: ${resp.data.data.paymentReference}`);
        await fetchBillingData();
        setTimeout(() => {
          setSelectedInvoice(null);
          setPaymentSuccess(null);
        }, 2000);
      }
    } catch (e) {
      console.error(e);
    } finally {
      setProcessing(false);
    }
  };

  return (
    <div className="max-w-5xl mx-auto space-y-6">
      {/* Header */}
      <div>
        <h1 className="text-2xl font-bold text-white">Billing & Premium Payments</h1>
        <p className="text-xs text-slate-400 mt-1">
          Guidewire BillingCenter concept alignment: Account ledgers, premium billing, and simulated cashless clearing.
        </p>
      </div>

      {/* Account Balances Card */}
      <div className="bg-gradient-to-r from-slate-900 via-slate-900 to-amber-950/30 border border-slate-800 p-6 rounded-2xl shadow-xl">
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-4 border-b border-slate-800">
          <div>
            <span className="text-[10px] font-bold text-amber-400 uppercase tracking-wider block">Billing Account</span>
            <h2 className="text-lg font-mono font-bold text-white">{billing?.accountNumber || 'GT-BIL-2026-000001'}</h2>
            <p className="text-xs text-slate-400 mt-0.5">Customer: {billing?.customerName}</p>
          </div>
          <div className="flex items-center space-x-2">
            <span className="text-xs px-3 py-1 rounded-full font-semibold bg-emerald-500/10 text-emerald-400 border border-emerald-500/30">
              Account Status: {billing?.status || 'CURRENT'}
            </span>
          </div>
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-3 gap-4 pt-5">
          <div className="bg-slate-950/60 border border-slate-800/80 p-4 rounded-xl">
            <span className="text-[10px] text-slate-500 uppercase block">Current Outstanding Balance</span>
            <span className="text-2xl font-bold text-amber-300 font-mono">
              ₹{billing ? billing.currentBalance.toLocaleString() : '0'}
            </span>
          </div>
          <div className="bg-slate-950/60 border border-slate-800/80 p-4 rounded-xl">
            <span className="text-[10px] text-slate-500 uppercase block">Total Lifetime Billed</span>
            <span className="text-2xl font-bold text-white font-mono">
              ₹{billing ? billing.totalBilled.toLocaleString() : '0'}
            </span>
          </div>
          <div className="bg-slate-950/60 border border-slate-800/80 p-4 rounded-xl">
            <span className="text-[10px] text-slate-500 uppercase block">Total Premium Settled</span>
            <span className="text-2xl font-bold text-emerald-400 font-mono">
              ₹{billing ? billing.totalPaid.toLocaleString() : '0'}
            </span>
          </div>
        </div>
      </div>

      {/* Invoices Ledger Table */}
      <div className="bg-slate-900/90 border border-slate-800 rounded-2xl p-6">
        <h3 className="text-sm font-bold text-white uppercase tracking-wider mb-4 flex items-center space-x-2">
          <Receipt className="w-4 h-4 text-amber-400" />
          <span>Invoices & Payment Schedules</span>
        </h3>

        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs">
            <thead className="bg-slate-950 text-slate-400 border-b border-slate-800">
              <tr>
                <th className="py-3 px-3 font-semibold uppercase">Invoice #</th>
                <th className="py-3 px-3 font-semibold uppercase">Policy Ref</th>
                <th className="py-3 px-3 font-semibold uppercase">Type</th>
                <th className="py-3 px-3 font-semibold uppercase">Due Date</th>
                <th className="py-3 px-3 font-semibold uppercase">Amount</th>
                <th className="py-3 px-3 font-semibold uppercase">Status</th>
                <th className="py-3 px-3 font-semibold uppercase text-right">Action</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-800/60 text-slate-300">
              {billing?.invoices?.map((inv) => (
                <tr key={inv.id} className="hover:bg-slate-800/30 transition-colors">
                  <td className="py-3 px-3 font-mono font-bold text-white">{inv.invoiceNumber}</td>
                  <td className="py-3 px-3 font-mono text-amber-400">{inv.policyNumber || 'N/A'}</td>
                  <td className="py-3 px-3">{inv.invoiceType}</td>
                  <td className="py-3 px-3 text-slate-400">{inv.dueDate}</td>
                  <td className="py-3 px-3 font-mono font-bold text-white">₹{inv.totalAmount.toLocaleString()}</td>
                  <td className="py-3 px-3">
                    <span className={`px-2 py-0.5 rounded text-[10px] font-bold ${
                      inv.status === 'PAID'
                        ? 'bg-emerald-500/10 text-emerald-400 border border-emerald-500/30'
                        : 'bg-amber-500/10 text-amber-400 border border-amber-500/30'
                    }`}>
                      {inv.status}
                    </span>
                  </td>
                  <td className="py-3 px-3 text-right">
                    {inv.status === 'PENDING' ? (
                      <button
                        onClick={() => setSelectedInvoice(inv)}
                        className="px-3 py-1 bg-amber-500 hover:bg-amber-600 text-slate-950 font-bold rounded-lg text-xs transition-colors"
                      >
                        Pay Now
                      </button>
                    ) : (
                      <span className="text-[11px] text-emerald-400 flex items-center justify-end space-x-1">
                        <CheckCircle2 className="w-3.5 h-3.5" />
                        <span>Settled</span>
                      </span>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {/* Simulated Payment Modal */}
      {selectedInvoice && (
        <div className="fixed inset-0 bg-slate-950/80 backdrop-blur-sm z-50 flex items-center justify-center p-4">
          <div className="bg-slate-900 border border-slate-800 rounded-2xl max-w-md w-full p-6 shadow-2xl">
            <h3 className="text-base font-bold text-white mb-1">Simulated Cashless Payment</h3>
            <p className="text-xs text-slate-400 mb-4">
              Academic payment gateway simulation for Invoice <strong className="text-white">{selectedInvoice.invoiceNumber}</strong>.
            </p>

            {paymentSuccess ? (
              <div className="p-4 bg-emerald-950/50 border border-emerald-500/50 rounded-xl text-emerald-300 text-xs flex items-center space-x-2">
                <CheckCircle2 className="w-5 h-5 text-emerald-400 shrink-0" />
                <span>{paymentSuccess}</span>
              </div>
            ) : (
              <div className="space-y-4">
                <div className="bg-slate-950 p-3.5 rounded-xl border border-slate-800 flex justify-between text-xs">
                  <span className="text-slate-400">Total Premium to Pay:</span>
                  <span className="text-base font-bold text-amber-300 font-mono">
                    ₹{selectedInvoice.totalAmount.toLocaleString()}
                  </span>
                </div>

                <div>
                  <label className="text-xs font-semibold text-slate-300 block mb-2">Select Payment Method</label>
                  <div className="grid grid-cols-3 gap-2">
                    <button
                      type="button"
                      onClick={() => setPaymentMethod('SIMULATED_UPI')}
                      className={`p-2.5 rounded-xl border text-xs font-medium transition-all ${
                        paymentMethod === 'SIMULATED_UPI'
                          ? 'bg-amber-500/10 border-amber-500 text-amber-300'
                          : 'bg-slate-950 border-slate-800 text-slate-400'
                      }`}
                    >
                      Instant UPI
                    </button>
                    <button
                      type="button"
                      onClick={() => setPaymentMethod('SIMULATED_CARD')}
                      className={`p-2.5 rounded-xl border text-xs font-medium transition-all ${
                        paymentMethod === 'SIMULATED_CARD'
                          ? 'bg-amber-500/10 border-amber-500 text-amber-300'
                          : 'bg-slate-950 border-slate-800 text-slate-400'
                      }`}
                    >
                      Credit Card
                    </button>
                    <button
                      type="button"
                      onClick={() => setPaymentMethod('SIMULATED_NET_BANKING')}
                      className={`p-2.5 rounded-xl border text-xs font-medium transition-all ${
                        paymentMethod === 'SIMULATED_NET_BANKING'
                          ? 'bg-amber-500/10 border-amber-500 text-amber-300'
                          : 'bg-slate-950 border-slate-800 text-slate-400'
                      }`}
                    >
                      Net Banking
                    </button>
                  </div>
                </div>

                <div className="p-3 bg-slate-950/50 rounded-lg text-[10px] text-slate-500">
                  Notice: No real banking or card credentials are exchanged or stored.
                </div>

                <div className="flex items-center space-x-3 pt-2">
                  <button
                    onClick={() => setSelectedInvoice(null)}
                    className="flex-1 py-2.5 bg-slate-800 hover:bg-slate-700 text-slate-300 text-xs font-semibold rounded-xl"
                  >
                    Cancel
                  </button>
                  <button
                    onClick={handlePayInvoice}
                    disabled={processing}
                    className="flex-1 py-2.5 bg-gradient-to-r from-amber-500 to-yellow-500 hover:from-amber-600 hover:to-yellow-600 text-slate-950 text-xs font-bold rounded-xl shadow-lg shadow-amber-500/20"
                  >
                    {processing ? 'Processing...' : 'Authorize Payment'}
                  </button>
                </div>
              </div>
            )}
          </div>
        </div>
      )}
    </div>
  );
};

