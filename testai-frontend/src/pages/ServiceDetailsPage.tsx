
import React, { useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import Navbar from '../components/layout/Navbar';
import Sidebar from '../components/layout/Sidebar';
import Card from '../components/common/Card';
import Badge from '../components/common/Badge';
import Button from '../components/common/Button';
import { 
  PencilSquareIcon, 
  TrashIcon, 
  PlayIcon,
  DocumentArrowDownIcon,
  ListBulletIcon,
  BeakerIcon,
  PresentationChartLineIcon,
  CogIcon
} from '@heroicons/react/24/outline';
import { PieChart, Pie, Cell, ResponsiveContainer, LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip } from 'recharts';

const MOCK_ENDPOINTS = [
  { id: '1', method: 'GET', path: '/users', params: ['limit', 'offset'], tests: 5 },
  { id: '2', method: 'POST', path: '/users', params: ['body'], tests: 12 },
  { id: '3', method: 'GET', path: '/users/{id}', params: ['id'], tests: 8 },
  { id: '4', method: 'PUT', path: '/users/{id}', params: ['id', 'body'], tests: 10 },
  { id: '5', method: 'DELETE', path: '/users/{id}', params: ['id'], tests: 4 },
];

const MOCK_TESTS = [
  { id: '101', name: 'GET /users - Success (200)', endpoint: '/users', status: 'passed', duration: '45ms', date: '2023-11-20 14:30' },
  { id: '102', name: 'POST /users - Invalid Body (400)', endpoint: '/users', status: 'failed', duration: '32ms', date: '2023-11-20 14:31' },
  { id: '103', name: 'GET /users/{id} - Not Found (404)', endpoint: '/users/{id}', status: 'passed', duration: '28ms', date: '2023-11-20 14:32' },
];

const PIE_DATA = [
  { name: 'Réussis', value: 85, color: '#28a745' },
  { name: 'Échoués', value: 15, color: '#dc3545' },
];

const ServiceDetailsPage: React.FC = () => {
  const { id } = useParams();
  const [activeTab, setActiveTab] = useState('endpoints');

  return (
    <div className="min-h-screen bg-background">
      <Navbar />
      <div className="flex">
        <Sidebar />
        <main className="flex-1 p-6 md:p-10 max-w-7xl mx-auto w-full">
          {/* Header */}
          <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-4 mb-8">
            <div className="flex items-center gap-4">
              <div className="w-14 h-14 bg-blue-50 text-primary rounded-2xl flex items-center justify-center">
                <BeakerIcon className="w-8 h-8" />
              </div>
              <div>
                <h1 className="text-3xl font-bold text-gray-900">User API Service</h1>
                <p className="text-gray-500 font-mono text-sm">https://api.example.com/v1</p>
              </div>
            </div>
            <div className="flex gap-2">
              <Button variant="outline" size="sm" icon={<PencilSquareIcon className="w-4 h-4" />}>Éditer</Button>
              <Button variant="outline" size="sm" className="text-red-500 border-red-200 hover:bg-red-50" icon={<TrashIcon className="w-4 h-4" />}>Supprimer</Button>
              <Link to={`/service/${id}/execute`}>
                <Button icon={<PlayIcon className="w-5 h-5" />}>Exécuter Tests</Button>
              </Link>
            </div>
          </div>

          {/* Tabs */}
          <div className="flex gap-8 border-b border-gray-200 mb-8 overflow-x-auto whitespace-nowrap scrollbar-hide">
            <TabItem active={activeTab === 'endpoints'} label="Endpoints" onClick={() => setActiveTab('endpoints')} icon={<ListBulletIcon className="w-5 h-5" />} />
            <TabItem active={activeTab === 'tests'} label="Tests" onClick={() => setActiveTab('tests')} icon={<BeakerIcon className="w-5 h-5" />} />
            <TabItem active={activeTab === 'reports'} label="Rapports" onClick={() => setActiveTab('reports')} icon={<PresentationChartLineIcon className="w-5 h-5" />} />
            <TabItem active={activeTab === 'settings'} label="Paramètres" onClick={() => setActiveTab('settings')} icon={<CogIcon className="w-5 h-5" />} />
          </div>

          {/* Tab Content */}
          <div className="animate-fadeIn">
            {activeTab === 'endpoints' && (
              <Card className="p-0">
                <div className="p-6 border-b border-gray-100 flex justify-between items-center">
                  <h3 className="text-lg font-bold">Endpoints Détectés ({MOCK_ENDPOINTS.length})</h3>
                  <Button variant="outline" size="sm">Regénérer tous les tests</Button>
                </div>
                <div className="overflow-x-auto">
                  <table className="w-full">
                    <thead className="bg-gray-50 text-left">
                      <tr>
                        <th className="px-6 py-4 text-xs font-bold text-gray-400 uppercase">Méthode</th>
                        <th className="px-6 py-4 text-xs font-bold text-gray-400 uppercase">Chemin</th>
                        <th className="px-6 py-4 text-xs font-bold text-gray-400 uppercase">Paramètres</th>
                        <th className="px-6 py-4 text-xs font-bold text-gray-400 uppercase text-center">Tests</th>
                        <th className="px-6 py-4 text-xs font-bold text-gray-400 uppercase text-right">Actions</th>
                      </tr>
                    </thead>
                    <tbody className="divide-y divide-gray-100">
                      {MOCK_ENDPOINTS.map(ep => (
                        <tr key={ep.id} className="hover:bg-gray-50 transition">
                          <td className="px-6 py-4">
                            <Badge variant="method" method={ep.method as any}>{ep.method}</Badge>
                          </td>
                          <td className="px-6 py-4 font-mono text-sm text-gray-700">{ep.path}</td>
                          <td className="px-6 py-4">
                            <div className="flex gap-1 flex-wrap">
                              {ep.params.map(p => <span key={p} className="px-2 py-1 bg-gray-100 rounded text-[10px] text-gray-600 font-medium">{p}</span>)}
                            </div>
                          </td>
                          <td className="px-6 py-4 text-center font-bold">{ep.tests}</td>
                          <td className="px-6 py-4 text-right">
                            <button className="text-primary font-bold text-sm hover:underline">Gérer tests</button>
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              </Card>
            )}

            {activeTab === 'tests' && (
              <div className="space-y-6">
                <div className="flex justify-between items-center">
                  <div className="flex gap-2">
                    <Badge variant="info">Tous (320)</Badge>
                    <Badge variant="success">Réussis (285)</Badge>
                    <Badge variant="danger">Échoués (35)</Badge>
                  </div>
                  <Button variant="outline" size="sm">Filtres avancés</Button>
                </div>
                <Card className="p-0">
                  <table className="w-full">
                    <thead className="bg-gray-50 text-left">
                      <tr>
                        <th className="px-6 py-4 text-xs font-bold text-gray-400 uppercase">Statut</th>
                        <th className="px-6 py-4 text-xs font-bold text-gray-400 uppercase">Nom du Test</th>
                        <th className="px-6 py-4 text-xs font-bold text-gray-400 uppercase">Durée</th>
                        <th className="px-6 py-4 text-xs font-bold text-gray-400 uppercase">Dernière exéc.</th>
                      </tr>
                    </thead>
                    <tbody className="divide-y divide-gray-100">
                      {MOCK_TESTS.map(test => (
                        <tr key={test.id} className="cursor-pointer hover:bg-gray-50 transition">
                          <td className="px-6 py-4">
                            {test.status === 'passed' ? 
                              <Badge variant="success">OK</Badge> : 
                              <Badge variant="danger">ERR</Badge>
                            }
                          </td>
                          <td className="px-6 py-4 font-medium text-gray-900">{test.name}</td>
                          <td className="px-6 py-4 text-gray-500">{test.duration}</td>
                          <td className="px-6 py-4 text-gray-500 text-sm">{test.date}</td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </Card>
              </div>
            )}

            {activeTab === 'reports' && (
              <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
                <Card title="Répartition des résultats">
                  <div className="h-[300px] w-full">
                    <ResponsiveContainer width="100%" height="100%">
                      <PieChart>
                        <Pie data={PIE_DATA} innerRadius={60} outerRadius={80} paddingAngle={5} dataKey="value">
                          {PIE_DATA.map((entry, index) => (
                            <Cell key={`cell-${index}`} fill={entry.color} />
                          ))}
                        </Pie>
                        <Tooltip />
                      </PieChart>
                    </ResponsiveContainer>
                    <div className="flex justify-center gap-6 mt-4">
                      {PIE_DATA.map(d => (
                        <div key={d.name} className="flex items-center gap-2">
                          <div className="w-3 h-3 rounded-full" style={{ backgroundColor: d.color }}></div>
                          <span className="text-sm font-medium text-gray-600">{d.name} ({d.value}%)</span>
                        </div>
                      ))}
                    </div>
                  </div>
                </Card>
                <Card title="Historique de succès" footer={<Button variant="outline" className="w-full" icon={<DocumentArrowDownIcon className="w-5 h-5" />}>Exporter Rapport PDF</Button>}>
                  <div className="h-[300px] w-full">
                     <ResponsiveContainer width="100%" height="100%">
                      <LineChart data={[
                        { name: 'Lun', success: 90 },
                        { name: 'Mar', success: 92 },
                        { name: 'Mer', success: 85 },
                        { name: 'Jeu', success: 95 },
                        { name: 'Ven', success: 98 },
                      ]}>
                        <CartesianGrid strokeDasharray="3 3" />
                        <XAxis dataKey="name" />
                        <YAxis />
                        <Tooltip />
                        <Line type="monotone" dataKey="success" stroke="#2E75B6" strokeWidth={3} />
                      </LineChart>
                    </ResponsiveContainer>
                  </div>
                </Card>
              </div>
            )}

            {activeTab === 'settings' && (
              <div className="max-w-2xl mx-auto space-y-8">
                <Card title="Configuration Jenkins">
                  <div className="space-y-4">
                    <label className="flex items-center gap-3 cursor-pointer">
                      <input type="checkbox" className="w-5 h-5 rounded border-gray-300" defaultChecked />
                      <span className="text-gray-700">Activer le déclenchement automatique</span>
                    </label>
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-1">Fréquence (Cron expression)</label>
                      <input type="text" className="w-full p-2 border rounded" defaultValue="0 0 * * *" />
                    </div>
                  </div>
                </Card>
                <Card title="Notifications Slack / Webhook">
                   <div className="space-y-4">
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-1">Webhook URL</label>
                      <input type="text" className="w-full p-2 border rounded" placeholder="https://hooks.slack.com/services/..." />
                    </div>
                    <Button variant="primary">Enregistrer les modifications</Button>
                  </div>
                </Card>
              </div>
            )}
          </div>
        </main>
      </div>
    </div>
  );
};

const TabItem = ({ active, label, onClick, icon }: { active: boolean, label: string, onClick: () => void, icon: React.ReactNode }) => (
  <button 
    onClick={onClick}
    className={`flex items-center gap-2 pb-4 px-2 font-semibold transition-all duration-200 border-b-2 
      ${active ? 'border-primary text-primary' : 'border-transparent text-gray-500 hover:text-gray-900'}`}
  >
    {icon}
    {label}
  </button>
);

export default ServiceDetailsPage;
