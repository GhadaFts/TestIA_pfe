
import React, { useState, useEffect, useRef } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import Navbar from '../components/layout/Navbar';
import Sidebar from '../components/layout/Sidebar';
import Card from '../components/common/Card';
import Button from '../components/common/Button';
import { 
  PlayIcon, 
  CheckCircleIcon, 
  XCircleIcon,
  ChevronLeftIcon
} from '@heroicons/react/24/outline';

const TestExecutionPage: React.FC = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [isRunning, setIsRunning] = useState(false);
  const [progress, setProgress] = useState(0);
  const [logs, setLogs] = useState<string[]>([]);
  const [selectedTests, setSelectedTests] = useState<string[]>(['1', '2', '3']);
  const [results, setResults] = useState({ total: 0, passed: 0, failed: 0 });
  const terminalRef = useRef<HTMLDivElement>(null);

  const tests = [
    { id: '1', name: 'Check User List Pagination' },
    { id: '2', name: 'Validate Create User Payload Constraints' },
    { id: '3', name: 'Edge Case: Empty String Name' },
    { id: '4', name: 'Edge Case: Duplicate Email Check' },
    { id: '5', name: 'Auth: Missing Bearer Token' },
  ];

  const addLog = (msg: string) => {
    setLogs(prev => [...prev, `[${new Date().toLocaleTimeString()}] ${msg}`]);
  };

  useEffect(() => {
    if (terminalRef.current) {
      terminalRef.current.scrollTop = terminalRef.current.scrollHeight;
    }
  }, [logs]);

  const runTests = async () => {
    if (selectedTests.length === 0) return;
    setIsRunning(true);
    setProgress(0);
    setLogs([]);
    setResults({ total: selectedTests.length, passed: 0, failed: 0 });

    addLog(`Démarrage de la suite de tests pour le service #${id}...`);
    
    for (let i = 0; i < selectedTests.length; i++) {
      const test = tests.find(t => t.id === selectedTests[i]);
      addLog(`Exécution : ${test?.name}...`);
      await new Promise(r => setTimeout(r, 800 + Math.random() * 1000));
      
      const isOk = Math.random() > 0.2;
      if (isOk) {
        addLog(`SUCCESS: ${test?.name} (HTTP 200 OK)`);
        setResults(prev => ({ ...prev, passed: prev.passed + 1 }));
      } else {
        addLog(`FAILED: ${test?.name} (HTTP 400 Bad Request)`);
        addLog(`Détail: Validation error on 'email' field - invalid format.`);
        setResults(prev => ({ ...prev, failed: prev.failed + 1 }));
      }
      setProgress(Math.round(((i + 1) / selectedTests.length) * 100));
    }

    addLog(`--- Terminée ---`);
    addLog(`Total: ${selectedTests.length} | Réussis: ${results.passed + (Math.random() > 0.5 ? 1 : 0)} | Échoués: ${results.failed}`);
    setIsRunning(false);
  };

  const toggleTest = (id: string) => {
    setSelectedTests(prev => prev.includes(id) ? prev.filter(t => t !== id) : [...prev, id]);
  };

  return (
    <div className="min-h-screen bg-background">
      <Navbar />
      <div className="flex">
        <Sidebar />
        <main className="flex-1 p-6 md:p-10 max-w-7xl mx-auto w-full">
          {/* Header */}
          <div className="flex items-center gap-4 mb-8">
            <button onClick={() => navigate(-1)} className="p-2 hover:bg-gray-100 rounded-lg">
              <ChevronLeftIcon className="w-5 h-5" />
            </button>
            <h1 className="text-2xl font-bold">Exécution des tests</h1>
          </div>

          {/* Progress Bar */}
          <div className="w-full bg-gray-200 rounded-full h-2.5 mb-8">
            <div className="bg-primary h-2.5 rounded-full transition-all duration-300" style={{ width: `${progress}%` }}></div>
          </div>

          <div className="grid grid-cols-1 lg:grid-cols-10 gap-8">
            {/* Selection List */}
            <div className="lg:col-span-3">
              <Card title="Tests à lancer" footer={
                <Button 
                  className="w-full" 
                  onClick={runTests} 
                  loading={isRunning}
                  disabled={selectedTests.length === 0}
                  icon={<PlayIcon className="w-5 h-5" />}
                >
                  Lancer les tests
                </Button>
              }>
                <div className="space-y-3">
                  <div className="flex justify-between items-center mb-4">
                    <span className="text-xs font-bold text-gray-400 uppercase">{selectedTests.length} sélectionnés</span>
                    <button 
                      className="text-xs text-primary font-bold hover:underline"
                      onClick={() => setSelectedTests(tests.map(t => t.id))}
                    >
                      Tout sélectionner
                    </button>
                  </div>
                  {tests.map(test => (
                    <label key={test.id} className="flex items-start gap-3 p-3 rounded-lg hover:bg-gray-50 cursor-pointer border border-transparent hover:border-gray-200 transition">
                      <input 
                        type="checkbox" 
                        className="mt-1 rounded text-primary" 
                        checked={selectedTests.includes(test.id)}
                        onChange={() => toggleTest(test.id)}
                      />
                      <span className="text-sm font-medium text-gray-700">{test.name}</span>
                    </label>
                  ))}
                </div>
              </Card>
            </div>

            {/* Console Log */}
            <div className="lg:col-span-7 flex flex-col gap-6">
              <div 
                ref={terminalRef}
                className="bg-zinc-900 rounded-xl p-6 font-mono text-sm h-[500px] overflow-y-auto shadow-inner"
              >
                {logs.length === 0 ? (
                  <p className="text-zinc-500 italic">En attente de lancement...</p>
                ) : (
                  logs.map((log, i) => (
                    <div key={i} className={`mb-1 ${log.includes('SUCCESS') ? 'text-green-400' : log.includes('FAILED') ? 'text-red-400' : 'text-zinc-300'}`}>
                      {log}
                    </div>
                  ))
                )}
                {isRunning && <div className="animate-pulse text-primary mt-2">_</div>}
              </div>

              {/* Summary */}
              {!isRunning && progress === 100 && (
                <div className="grid grid-cols-3 gap-4">
                  <div className="bg-white p-4 rounded-xl border border-gray-100 flex items-center gap-3">
                    <div className="p-2 bg-gray-50 rounded-lg"><ListBulletIcon className="w-6 h-6 text-gray-400" /></div>
                    <div><p className="text-xs text-gray-500 uppercase font-bold">Total</p><p className="text-xl font-bold">{results.total}</p></div>
                  </div>
                  <div className="bg-white p-4 rounded-xl border border-green-100 flex items-center gap-3">
                    <div className="p-2 bg-green-50 rounded-lg"><CheckCircleIcon className="w-6 h-6 text-green-500" /></div>
                    <div><p className="text-xs text-gray-500 uppercase font-bold">Réussis</p><p className="text-xl font-bold text-green-600">{results.passed}</p></div>
                  </div>
                  <div className="bg-white p-4 rounded-xl border border-red-100 flex items-center gap-3">
                    <div className="p-2 bg-red-50 rounded-lg"><XCircleIcon className="w-6 h-6 text-red-500" /></div>
                    <div><p className="text-xs text-gray-500 uppercase font-bold">Échoués</p><p className="text-xl font-bold text-red-600">{results.failed}</p></div>
                  </div>
                </div>
              )}
            </div>
          </div>
        </main>
      </div>
    </div>
  );
};

const ListBulletIcon = ({ className }: { className?: string }) => (
  <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={1.5} stroke="currentColor" className={className}>
    <path strokeLinecap="round" strokeLinejoin="round" d="M8.25 6.75h12M8.25 12h12m-12 5.25h12M3.75 6.75h.007v.008H3.75V6.75zm.375 0a.375.375 0 11-.75 0 .375.375 0 01.75 0zM3.75 12h.007v.008H3.75V12zm.375 0a.375.375 0 11-.75 0 .375.375 0 01.75 0zm-.375 5.25h.007v.008H3.75v-.008zm.375 0a.375.375 0 11-.75 0 .375.375 0 01.75 0z" />
  </svg>
);

export default TestExecutionPage;
