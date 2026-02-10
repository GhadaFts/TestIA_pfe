
import React, { useState } from 'react';
import Navbar from '../components/layout/Navbar';
import Sidebar from '../components/layout/Sidebar';
import Card from '../components/common/Card';
import Badge from '../components/common/Badge';
import Button from '../components/common/Button';
import { 
  ArrowDownTrayIcon, 
  FunnelIcon,
  ChartBarSquareIcon,
  ShieldCheckIcon,
  BugAntIcon
} from '@heroicons/react/24/outline';
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, LineChart, Line } from 'recharts';

const REPORTS_DATA = [
  { date: '20 Nov', execs: 45, success: 98 },
  { date: '21 Nov', execs: 52, success: 95 },
  { date: '22 Nov', execs: 38, success: 88 },
  { date: '23 Nov', execs: 65, success: 97 },
  { date: '24 Nov', execs: 48, success: 94 },
  { date: '25 Nov', execs: 70, success: 99 },
];

const ReportsPage: React.FC = () => {
  const [period, setPeriod] = useState('30d');

  return (
    <div className="min-h-screen bg-background">
      <Navbar />
      <div className="flex">
        <Sidebar />
        <main className="flex-1 p-6 md:p-10 max-w-7xl mx-auto w-full">
          {/* Header */}
          <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-4 mb-10">
            <div>
              <h1 className="text-3xl font-bold text-gray-900">Analyses & Rapports</h1>
              <p className="text-gray-500">Performances globales de vos APIs sur la période sélectionnée.</p>
            </div>
            <div className="flex gap-3">
              <div className="flex bg-white border border-gray-200 rounded-lg p-1">
                <PeriodBtn active={period === '7d'} onClick={() => setPeriod('7d')}>7j</PeriodBtn>
                <PeriodBtn active={period === '30d'} onClick={() => setPeriod('30d')}>30j</PeriodBtn>
                <PeriodBtn active={period === '90d'} onClick={() => setPeriod('90d')}>90j</PeriodBtn>
              </div>
              <Button variant="outline" icon={<ArrowDownTrayIcon className="w-5 h-5" />}>Exporter</Button>
            </div>
          </div>

          {/* KPIs */}
          <div className="grid grid-cols-1 md:grid-cols-3 gap-8 mb-10">
            <KPI icon={<ChartBarSquareIcon className="w-6 h-6" />} title="Total exécutions" value="2,450" change="+12%" />
            <KPI icon={<ShieldCheckIcon className="w-6 h-6" />} title="Taux de réussite" value="96.8%" change="+0.4%" isGood />
            <KPI icon={<BugAntIcon className="w-6 h-6" />} title="Bugs détectés" value="42" change="-5%" isGood />
          </div>

          {/* Charts */}
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-8 mb-10">
            <Card title="Volume de tests quotidiens">
              <div className="h-[300px] w-full mt-4">
                <ResponsiveContainer width="100%" height="100%">
                  <BarChart data={REPORTS_DATA}>
                    <CartesianGrid strokeDasharray="3 3" vertical={false} />
                    <XAxis dataKey="date" />
                    <YAxis />
                    <Tooltip />
                    <Bar dataKey="execs" fill="#2E75B6" radius={[4, 4, 0, 0]} />
                  </BarChart>
                </ResponsiveContainer>
              </div>
            </Card>
            <Card title="Stabilité des services (%)">
               <div className="h-[300px] w-full mt-4">
                <ResponsiveContainer width="100%" height="100%">
                  <LineChart data={REPORTS_DATA}>
                    <CartesianGrid strokeDasharray="3 3" vertical={false} />
                    <XAxis dataKey="date" />
                    <YAxis domain={[80, 100]} />
                    <Tooltip />
                    <Line type="monotone" dataKey="success" stroke="#28a745" strokeWidth={3} dot={{ r: 6 }} />
                  </LineChart>
                </ResponsiveContainer>
              </div>
            </Card>
          </div>

          {/* History Table */}
          <Card className="p-0 overflow-hidden">
            <div className="p-6 flex flex-col md:flex-row justify-between items-start md:items-center gap-4 border-b border-gray-100">
              <h3 className="text-lg font-bold">Historique des exécutions</h3>
              <div className="flex gap-2">
                 <div className="relative">
                  <span className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none text-gray-400">
                    <FunnelIcon className="h-4 w-4" />
                  </span>
                  <select className="pl-10 pr-4 py-2 border rounded-lg text-sm bg-white outline-none">
                    <option>Tous les services</option>
                    <option>User API</option>
                    <option>Payment Service</option>
                  </select>
                </div>
              </div>
            </div>
            <table className="w-full text-left">
              <thead className="bg-gray-50">
                <tr>
                  <th className="px-6 py-4 text-xs font-bold text-gray-400 uppercase">Service</th>
                  <th className="px-6 py-4 text-xs font-bold text-gray-400 uppercase">Statut</th>
                  <th className="px-6 py-4 text-xs font-bold text-gray-400 uppercase">Tests</th>
                  <th className="px-6 py-4 text-xs font-bold text-gray-400 uppercase">Durée</th>
                  <th className="px-6 py-4 text-xs font-bold text-gray-400 uppercase">Date</th>
                  <th className="px-6 py-4 text-xs font-bold text-gray-400 uppercase text-right">Rapport</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-100">
                {[1, 2, 3, 4, 5].map(i => (
                  <tr key={i} className="hover:bg-gray-50 transition">
                    <td className="px-6 py-4 font-semibold text-gray-900">User Management API</td>
                    <td className="px-6 py-4">
                      <Badge variant={i % 3 === 0 ? 'warning' : 'success'}>
                        {i % 3 === 0 ? 'Avertissement' : 'Réussi'}
                      </Badge>
                    </td>
                    <td className="px-6 py-4">48/50</td>
                    <td className="px-6 py-4 text-gray-500">12.4s</td>
                    <td className="px-6 py-4 text-gray-500 text-sm">Aujourd'hui, 14:02</td>
                    <td className="px-6 py-4 text-right">
                      <button className="text-primary text-sm font-bold hover:underline">Ouvrir</button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
            <div className="p-4 bg-gray-50 text-center border-t border-gray-100">
              <button className="text-sm text-primary font-bold hover:underline">Charger plus...</button>
            </div>
          </Card>
        </main>
      </div>
    </div>
  );
};

// Fixed: Using React.FC for correct prop validation of internal KPI component
const KPI: React.FC<{ icon: React.ReactNode, title: string, value: string, change: string, isGood?: boolean }> = ({ icon, title, value, change, isGood }) => (
  <Card className="flex flex-col gap-2 border-l-4 border-l-primary">
    <div className="flex justify-between items-start">
      <div className="text-gray-400">{icon}</div>
      <span className={`text-xs font-bold px-2 py-1 rounded ${isGood ? 'bg-green-50 text-green-600' : 'bg-blue-50 text-primary'}`}>
        {change}
      </span>
    </div>
    <p className="text-2xl font-bold text-gray-900">{value}</p>
    <p className="text-xs text-gray-500 font-bold uppercase tracking-tight">{title}</p>
  </Card>
);

// Fixed: Using React.FC for correct prop validation and children support of internal PeriodBtn component
const PeriodBtn: React.FC<{ active: boolean, children: React.ReactNode, onClick: () => void }> = ({ active, children, onClick }) => (
  <button 
    onClick={onClick}
    className={`px-4 py-1.5 text-sm font-semibold rounded-md transition ${active ? 'bg-primary text-white shadow-sm' : 'text-gray-500 hover:bg-gray-100'}`}
  >
    {children}
  </button>
);

export default ReportsPage;