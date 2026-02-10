
import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import Input from '../components/common/Input';
import Button from '../components/common/Button';
import Card from '../components/common/Card';
import { EnvelopeIcon, LockClosedIcon, ArrowLeftIcon } from '@heroicons/react/24/outline';

const LoginPage: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const navigate = useNavigate();

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    
    // Simulate API call
    setTimeout(() => {
      localStorage.setItem('token', 'mock_jwt_token');
      navigate('/dashboard');
      setLoading(false);
    }, 1000);
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100 flex flex-col items-center justify-center p-4">
      <Link to="/" className="mb-8 flex items-center text-gray-500 hover:text-primary transition">
        <ArrowLeftIcon className="w-4 h-4 mr-2" />
        Retour à l'accueil
      </Link>
      
      <div className="w-full max-w-md">
        <Card className="shadow-2xl">
          <div className="text-center mb-10">
            <div className="inline-flex w-12 h-12 bg-primary rounded-xl items-center justify-center mb-4">
              <span className="text-white font-bold text-2xl">T</span>
            </div>
            <h1 className="text-3xl font-bold text-gray-900">Bon retour !</h1>
            <p className="text-gray-500 mt-2">Connectez-vous pour accéder à vos tests.</p>
          </div>

          <form onSubmit={handleLogin}>
            <Input 
              label="Email professionnel"
              type="email"
              placeholder="votre@email.com"
              required
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              icon={<EnvelopeIcon className="h-5 w-5" />}
            />
            <Input 
              label="Mot de passe"
              type="password"
              placeholder="••••••••"
              required
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              icon={<LockClosedIcon className="h-5 w-5" />}
            />

            <div className="flex items-center justify-between mb-6">
              <label className="flex items-center gap-2 cursor-pointer">
                <input type="checkbox" className="w-4 h-4 text-primary rounded border-gray-300 focus:ring-primary" />
                <span className="text-sm text-gray-600">Se souvenir de moi</span>
              </label>
              <a href="#" className="text-sm font-medium text-primary hover:underline">Mot de passe oublié ?</a>
            </div>

            <Button type="submit" className="w-full" loading={loading}>
              Se connecter
            </Button>
          </form>

          <div className="mt-8 relative">
            <div className="absolute inset-0 flex items-center"><div className="w-full border-t border-gray-200"></div></div>
            <div className="relative flex justify-center text-sm"><span className="px-2 bg-white text-gray-500">Ou continuer avec</span></div>
          </div>

          <div className="mt-6 flex gap-4">
            <button className="flex-1 flex items-center justify-center gap-2 border border-gray-300 p-3 rounded-lg hover:bg-gray-50 transition font-medium">
              <img src="https://www.svgrepo.com/show/475656/google-color.svg" className="w-5 h-5" alt="Google" />
              Google
            </button>
          </div>

          <p className="mt-10 text-center text-gray-600">
            Nouveau sur TestAI ?{' '}
            <Link to="/register" className="font-bold text-primary hover:underline">
              Créez un compte
            </Link>
          </p>
        </Card>
      </div>
    </div>
  );
};

export default LoginPage;
