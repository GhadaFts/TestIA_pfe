
import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Navbar from '../components/layout/Navbar';
import Sidebar from '../components/layout/Sidebar';
import Card from '../components/common/Card';
import Input from '../components/common/Input';
import Button from '../components/common/Button';
import { 
  ArrowPathIcon,
  PlusIcon,
  TrashIcon
} from '@heroicons/react/24/outline';
import { DocMode, AuthType } from '../types/types'; // Assuming you have a type for DocMode

const AddServicePage: React.FC = () => {
  const [docMode, setDocMode] = useState<DocMode>(DocMode.SWAGGER);
  const [loading, setLoading] = useState(false);
  const [authType, setAuthType] = useState<AuthType>(AuthType.NONE);
  const [manualEndpoints, setManualEndpoints] = useState([{ method: 'GET', path: '', description: '' }]);
  const navigate = useNavigate();

  const handleAnalyze = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    // Simulate AI analysis
    setTimeout(() => {
      navigate('/service/new');
      setLoading(false);
    }, 2500);
  };

  const addManualEndpoint = () => {
    setManualEndpoints([...manualEndpoints, { method: 'GET', path: '', description: '' }]);
  };

  const removeManualEndpoint = (index: number) => {
    setManualEndpoints(manualEndpoints.filter((_, i) => i !== index));
  };

  return (
    <div className="min-h-screen bg-background">
      <Navbar />
      <div className="flex">
        <Sidebar />
        <main className="flex-1 p-6 md:p-10 max-w-4xl mx-auto w-full">
          <div className="mb-8">
            <h1 className="text-3xl font-bold text-gray-900">Ajouter un service</h1>
            <p className="text-gray-500">Connectez votre API pour que l'IA puisse générer les tests.</p>
          </div>

          <Card className="mb-8 p-0 overflow-hidden">

            <div className="p-8">
              <form onSubmit={handleAnalyze}>
                  <div className="space-y-6">
                    <Input label="Nom du service" placeholder="ex: User Management API" required />
                    <Input label="Description du service" placeholder="ex: This API manages user accounts and authentication" required />
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-1">Documentation Type</label>
                      <select 
                        className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary outline-none"
                        onChange={(e) => setDocMode(e.target.value as DocMode)}
                      >
                        <option>Dossier de documentation</option>
                        <option value={DocMode.SWAGGER}>Swagger / OpenAPI</option>
                        <option value={DocMode.POSTMAN}>Postman</option>
                        <option value={DocMode.MANUAL}>Manuelle</option>
                      </select>
                    </div>
                    <Input label="URL de base du service" placeholder="http://api.example.com/v1" required />
                    {docMode === 'MANUAL' && (
                  <div className="space-y-6">
                    <div className="space-y-4">
                      <p className="text-sm font-bold text-gray-400 uppercase tracking-wider">Endpoints</p>
                      {manualEndpoints.map((ep, idx) => (
                        <div key={idx} className="flex gap-4 items-end bg-gray-50 p-4 rounded-xl relative">
                          <div className="w-32">
                            <label className="block text-xs font-bold text-gray-400 mb-1 uppercase">Méthode</label>
                            <select className="w-full px-3 py-2 border rounded-lg bg-white">
                              <option>GET</option>
                              <option>POST</option>
                              <option>PUT</option>
                              <option>DELETE</option>
                            </select>
                          </div>
                          <div className="flex-1">
                            <label className="block text-xs font-bold text-gray-400 mb-1 uppercase">Chemin (Path)</label>
                            <Input placeholder="/users" className="mb-0" />
                          </div>
                          <Button 
                            variant="ghost" 
                            className="text-red-400 hover:text-red-600 p-2"
                            onClick={() => removeManualEndpoint(idx)}
                            type="button"
                          >
                            <TrashIcon className="w-5 h-5" />
                          </Button>
                        </div>
                      ))}
                      <Button 
                        variant="outline" 
                        className="w-full border-dashed" 
                        type="button"
                        onClick={addManualEndpoint}
                        icon={<PlusIcon className="w-4 h-4" />}
                      >
                        Ajouter un endpoint
                      </Button>
                    </div>
                  </div>
                )}
                    {docMode !== DocMode.MANUAL && (
                    <Input label="URL de la documentation Swagger (JSON/YAML)" placeholder="/v3/api-docs" type='file' /> 
                    )}
                    
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-1">Authentification</label>
                      <select 
                        className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary outline-none"
                        value={authType}
                        onChange={(e) => setAuthType(e.target.value as AuthType)}
                      >
                        <option value={AuthType.NONE}>Aucune</option>
                        <option value={AuthType.APIKEY}>API Key</option>
                        <option value={AuthType.BEARER}>Bearer Token</option>
                        <option value={AuthType.BASIC}>Basic Auth</option>
                      </select>
                    </div>

                    {authType !== AuthType.NONE && (
                      <div className="p-4 bg-blue-50 rounded-lg border border-blue-100">
                        <Input 
                          label={authType === AuthType.APIKEY ? "Clé API" : "Token / Identifiants"} 
                          placeholder="••••••••" 
                        />
                      </div>
                    )}

                  </div>
                <div className="flex gap-4 mt-12 pt-8 border-t border-gray-100">
                  <Button variant="outline" type="button" onClick={() => navigate('/dashboard')} disabled={loading}>
                    Annuler
                  </Button>
                  <Button type="submit" className="flex-1" loading={loading} icon={!loading && <ArrowPathIcon className="w-5 h-5" />}>
                    Analyser et Générer Tests
                  </Button>
                </div>
              </form>
            </div>
          </Card>
          
          {loading && (
            <div className="text-center p-8">
              <p className="text-lg font-bold text-primary animate-pulse">L'IA analyse vos endpoints...</p>
              <p className="text-gray-500 text-sm mt-2 italic">Analyse des structures JSON, détection des contraintes, génération des scénarios limites...</p>
            </div>
          )}
        </main>
      </div>
    </div>
  );
};

export default AddServicePage;