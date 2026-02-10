
import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import Navbar from '../components/layout/Navbar';
import Sidebar from '../components/layout/Sidebar';
import Card from '../components/common/Card';
import Badge from '../components/common/Badge';
import Button from '../components/common/Button';
import { 
  PlusIcon, 
  ServerStackIcon, 
  CheckBadgeIcon, 
  ClockIcon, 
  ArrowRightIcon,
  MagnifyingGlassIcon,
  SparklesIcon
} from '@heroicons/react/24/outline';
import { MOCK_SERVICES } from '../constants';
import type { Service } from '../types/types';

const Dashboard: React.FC = () => {
  const [services, setServices] = useState<Service[]>([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');

  useEffect(() => {
    const timer = setTimeout(() => {
      setServices(MOCK_SERVICES as Service[]);
      setLoading(false);
    }, 800);
    return () => clearTimeout(timer);
  }, []);

  const filteredServices = services.filter(s => s.name.toLowerCase().includes(search.toLowerCase()));

  return (
    <div className="min-h-screen bg-background selection:bg-primary/20">
      <Navbar />
      <div className="flex">
        <Sidebar />
        <main className="flex-1 p-8 lg:p-12 max-w-7xl mx-auto w-full">
          {/* Header */}
          <div className="flex flex-col md:flex-row md:items-end justify-between mb-12 gap-6">
            <div>
              <div className="flex items-center gap-2 text-primary font-bold text-sm uppercase tracking-widest mb-2">
                <SparklesIcon className="w-4 h-4" />
                <span>Espace de travail Personnel</span>
              </div>
              <h1 className="text-4xl font-extrabold text-slate-900 tracking-tight">Vue d'ensemble</h1>
            </div>
            <div className="flex gap-4">
               <div className="relative">
                <span className="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none">
                  <MagnifyingGlassIcon className="h-5 w-5 text-slate-400" />
                </span>
                <input 
                  type="text" 
                  placeholder="Filtrer vos APIs..." 
                  className="pl-12 pr-6 py-3 w-64 border border-slate-200 rounded-2xl focus:ring-4 focus:ring-primary/10 focus:border-primary outline-none transition bg-white"
                  value={search}
                  onChange={(e) => setSearch(e.target.value)}
                />
              </div>
              <Link to="/add-service">
                <Button icon={<PlusIcon className="w-5 h-5" />}>
                  Nouveau Service
                </Button>
              </Link>
            </div>
          </div>

          {/* Stats Grid */}
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6 mb-12">
            <StatCard title="APIs Connectées" value={services.length.toString()} icon={<ServerStackIcon className="w-6 h-6 text-primary" />} trend="+1" />
            <StatCard title="Tests Générés" value="1,248" icon={<CheckBadgeIcon className="w-6 h-6 text-success" />} trend="+124" />
            <StatCard title="Score Moyen" value="98%" icon={<SparklesIcon className="w-6 h-6 text-warning" />} />
            <StatCard title="Exec. Aujourd'hui" value="24" icon={<ClockIcon className="w-6 h-6 text-info" />} />
          </div>

          {/* Services Grid */}
          {loading ? (
             <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
               {[1,2,3].map(i => <div key={i} className="h-72 bg-slate-100 animate-pulse rounded-3xl"></div>)}
             </div>
          ) : filteredServices.length > 0 ? (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
              {filteredServices.map(service => (
                <ServiceCard key={service.id} service={service} />
              ))}
            </div>
          ) : (
            <EmptyState />
          )}
        </main>
      </div>
    </div>
  );
};

const StatCard: React.FC<{ title: string, value: string, icon: React.ReactNode, trend?: string }> = ({ title, value, icon, trend }) => (
  <Card className="flex flex-col gap-4 p-8 border border-slate-100 hover:shadow-xl transition-all duration-500">
    <div className="flex justify-between items-start">
      <div className="p-3 bg-slate-50 rounded-2xl">
        {icon}
      </div>
      {trend && <span className="text-xs font-bold text-success bg-success/10 px-2 py-1 rounded-lg">{trend}</span>}
    </div>
    <div>
      <p className="text-3xl font-black text-slate-900 mb-1">{value}</p>
      <p className="text-sm text-slate-400 font-bold uppercase tracking-wider">{title}</p>
    </div>
  </Card>
);

const ServiceCard: React.FC<{ service: Service }> = ({ service }) => (
  <Card className="flex flex-col h-full hover:shadow-2xl transition-all duration-500 group border border-slate-100 p-8 rounded-3xl">
    <div className="flex justify-between items-start mb-8">
      <div className="p-4 bg-primary/10 text-primary rounded-2xl group-hover:bg-primary group-hover:text-white transition-colors duration-300">
        <ServerStackIcon className="w-8 h-8" />
      </div>
      <Badge variant={service.status === 'active' ? 'success' : 'gray'}>
        {service.status === 'active' ? 'Opérationnel' : 'En pause'}
      </Badge>
    </div>
    
    <div className="mb-8">
      <h3 className="text-2xl font-black text-slate-900 mb-2 group-hover:text-primary transition-colors">{service.name}</h3>
      <p className="text-sm text-slate-400 font-mono truncate bg-slate-50 p-2 rounded-lg">{service.url}</p>
    </div>
    
    <div className="mt-auto grid grid-cols-2 gap-4 pt-6 border-t border-slate-100">
      <div>
        <p className="text-xs font-bold text-slate-400 uppercase mb-1">Endpoints</p>
        <p className="text-lg font-bold text-slate-900">{service.endpointsCount}</p>
      </div>
      <div className="text-right">
        <p className="text-xs font-bold text-slate-400 uppercase mb-1">Tests IA</p>
        <p className="text-lg font-bold text-slate-900">{service.endpointsCount * 12}</p>
      </div>
    </div>

    <Link to={`/service/${service.id}`} className="mt-8 block">
      <Button variant="ghost" className="w-full justify-between px-4 group/btn" icon={<ArrowRightIcon className="w-4 h-4 group-hover/btn:translate-x-1 transition-transform" />}>
        Gérer l'API
      </Button>
    </Link>
  </Card>
);

const EmptyState = () => (
  <div className="flex flex-col items-center justify-center py-24 text-center bg-white rounded-3xl border-2 border-dashed border-slate-200 px-12">
    <div className="w-24 h-24 bg-slate-50 rounded-3xl flex items-center justify-center mb-8">
      <PlusIcon className="w-12 h-12 text-slate-300" />
    </div>
    <h3 className="text-3xl font-black text-slate-900 mb-4">Prêt à tester ?</h3>
    <p className="text-slate-500 max-w-sm mb-10 leading-relaxed">
      Importez votre documentation d'API et laissez l'intelligence artificielle faire le travail complexe pour vous.
    </p>
    <Link to="/add-service">
      <Button size="lg" icon={<PlusIcon className="w-6 h-6" />}>Commencer maintenant</Button>
    </Link>
  </div>
);

export default Dashboard;
