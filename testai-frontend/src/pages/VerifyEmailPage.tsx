// src/pages/VerifyEmailPage.tsx - VERSION CORRIGÉE

import React, { useState, useEffect, useRef } from 'react';
import { useSearchParams, useNavigate } from 'react-router-dom';
import Card from '../components/common/Card';
import Button from '../components/common/Button';
import { CheckCircleIcon, XCircleIcon, ArrowPathIcon } from '@heroicons/react/24/outline';
import authService from '../services/authService';

const VerifyEmailPage: React.FC = () => {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  
  const [status, setStatus] = useState<'loading' | 'success' | 'error'>('loading');
  const [message, setMessage] = useState('Vérification en cours...');
  
  // ⭐️ PROTECTION CONTRE LE DOUBLE APPEL (React Strict Mode)
  const hasVerified = useRef(false);
  
  const token = searchParams.get('token');

  const verifyEmail = async (tokenToVerify: string) => {
    // ⭐️ Si déjà vérifié, ne rien faire
    if (hasVerified.current) {
      console.log('⚠️ Vérification déjà effectuée, ignorée');
      return;
    }

    // ⭐️ Marquer comme en cours
    hasVerified.current = true;

    try {
      console.log('📡 Appel API verify-email avec token:', tokenToVerify);
      
      const response = await authService.verifyEmail(tokenToVerify);
      
      console.log('✅ Réponse:', response);
      setStatus('success');
      setMessage(response.message || 'Email vérifié avec succès !');
      
      // Redirection après 2 secondes
      setTimeout(() => {
        if (response.phoneVerified || !response.requiresPhoneVerification) {
          // Si téléphone déjà vérifié ou pas requis → Login
          navigate('/login', { 
            state: { 
              message: '✅ Compte activé ! Vous pouvez vous connecter.' 
            } 
          });
        } else {
          // Sinon → Vérification téléphone
          navigate('/verification-pending', {
            state: {
              email: searchParams.get('email') || '',
              emailVerified: true,
              needsPhoneVerification: true
            }
          });
        }
      }, 2000);

    } catch (error: any) {
      console.error('❌ Erreur vérification:', error);
      setStatus('error');
      setMessage(error.response?.data?.message || error.message || 'Erreur de vérification');
    }
  };

  useEffect(() => {
    if (!token) {
      setStatus('error');
      setMessage('❌ Token manquant dans l\'URL');
      return;
    }

    // ⭐️ Appeler seulement si pas déjà vérifié
    if (!hasVerified.current) {
      verifyEmail(token);
    }
  }, []); // ⭐️ Dépendances vides pour n'exécuter qu'une fois

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100 flex items-center justify-center p-4">
      <div className="w-full max-w-md">
        <Card className="shadow-2xl text-center">
          <div className="mb-6">
            {status === 'loading' && (
              <div className="inline-flex w-16 h-16 bg-blue-100 rounded-full items-center justify-center">
                <ArrowPathIcon className="w-8 h-8 text-primary animate-spin" />
              </div>
            )}
            {status === 'success' && (
              <div className="inline-flex w-16 h-16 bg-green-100 rounded-full items-center justify-center">
                <CheckCircleIcon className="w-8 h-8 text-green-600" />
              </div>
            )}
            {status === 'error' && (
              <div className="inline-flex w-16 h-16 bg-red-100 rounded-full items-center justify-center">
                <XCircleIcon className="w-8 h-8 text-red-600" />
              </div>
            )}
          </div>

          <h1 className="text-2xl font-bold text-gray-900 mb-4">
            {status === 'loading' && 'Vérification en cours...'}
            {status === 'success' && '✅ Email vérifié !'}
            {status === 'error' && '❌ Erreur de vérification'}
          </h1>

          <p className="text-gray-600 mb-8">{message}</p>

          {status === 'success' && (
            <div className="space-y-4">
              <p className="text-sm text-blue-600">
                Redirection automatique en cours...
              </p>
              <div className="w-full bg-blue-100 rounded-full h-2">
                <div className="bg-primary h-2 rounded-full animate-pulse" style={{ width: '70%' }}></div>
              </div>
            </div>
          )}

          {status === 'error' && (
            <div className="space-y-4">
              <Button onClick={() => navigate('/login')} className="w-full">
                Retour à la connexion
              </Button>
              
              <button
                onClick={() => navigate('/verification-pending')}
                className="text-sm text-primary hover:underline"
              >
                Renvoyer l'email de vérification
              </button>
            </div>
          )}
        </Card>
      </div>
    </div>
  );
};

export default VerifyEmailPage;