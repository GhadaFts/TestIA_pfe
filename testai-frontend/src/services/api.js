// ==========================================
// src/services/api.js
// Configuration Axios pour communiquer avec la Gateway
// ==========================================

import axios from 'axios';

// URL de base de la gateway
const API_BASE_URL = process.env.REACT_APP_API_BASE_URL || 'http://localhost:8888';

// Instance Axios avec configuration CORS
const api = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
  withCredentials: true, // IMPORTANT : Permet d'envoyer les cookies et JWT
});

// ==========================================
// INTERCEPTEUR REQUEST (Ajouter JWT Token)
// ==========================================
api.interceptors.request.use(
  (config) => {
    // Récupérer le token depuis localStorage
    const token = localStorage.getItem('accessToken');
    
    if (token) {
      // Ajouter le token dans le header Authorization
      config.headers.Authorization = `Bearer ${token}`;
    }
    
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// ==========================================
// INTERCEPTEUR RESPONSE (Gérer les erreurs)
// ==========================================
api.interceptors.response.use(
  (response) => {
    // Retourner la réponse si tout va bien
    return response;
  },
  (error) => {
    // Gérer les erreurs
    if (error.response) {
      // Le serveur a répondu avec un code d'erreur
      switch (error.response.status) {
        case 401:
          // Token expiré ou invalide
          console.error('Authentification expirée');
          localStorage.removeItem('accessToken');
          localStorage.removeItem('refreshToken');
          window.location.href = '/login';
          break;
        
        case 403:
          // Accès interdit
          console.error('Accès refusé');
          break;
        
        case 404:
          // Ressource non trouvée
          console.error('Ressource non trouvée');
          break;
        
        case 500:
          // Erreur serveur
          console.error('Erreur serveur interne');
          break;
        
        default:
          console.error('Erreur:', error.response.status);
      }
    } else if (error.request) {
      // La requête a été envoyée mais pas de réponse
      console.error('Pas de réponse du serveur');
    } else {
      // Erreur lors de la configuration de la requête
      console.error('Erreur:', error.message);
    }
    
    return Promise.reject(error);
  }
);

// ==========================================
// SERVICES API
// ==========================================

// Auth Service
export const authService = {
  login: (email, password) => 
    api.post('/api/auth/login', { email, password }),
  
  register: (userData) => 
    api.post('/api/auth/register', userData),
  
  logout: () => {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    window.location.href = '/login';
  },
  
  verifyEmail: (token) => 
    api.get(`/api/auth/verify-email?token=${token}`),
  
  verifyPhone: (code) => 
    api.post('/api/auth/verify-phone', { code }),
};

// User Service
export const userService = {
  getAllUsers: () => 
    api.get('/api/users/all'),
  
  getUserById: (userId) => 
    api.get(`/api/users/${userId}`),
  
  getCurrentUser: () => 
    api.get('/api/users/me'),
  
  updateProfile: (profileData) => 
    api.put('/api/users/me', profileData),
};

// Project Service
export const projectService = {
  getAllProjects: () => 
    api.get('/api/projects/all'),
  
  getProjectById: (projectId) => 
    api.get(`/api/projects/${projectId}`),
  
  createProject: (projectData) => {
    const formData = new FormData();
    Object.keys(projectData).forEach(key => {
      formData.append(key, projectData[key]);
    });
    return api.post('/api/projects/add', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
  },
  
  updateProject: (projectId, projectData) => 
    api.put(`/api/projects/${projectId}`, projectData),
  
  deleteProject: (projectId) => 
    api.delete(`/api/projects/${projectId}`),
  
  getProjectEndpoints: (projectId) => 
    api.get(`/api/projects/${projectId}/endpoints`),
  
  scanProjectEndpoints: (projectId) => 
    api.post(`/api/projects/${projectId}/scan-endpoints`),
  
  countProjectEndpoints: (projectId) => 
    api.get(`/api/projects/${projectId}/endpoints/count`),
};

// Endpoint Service
export const endpointService = {
  getAllEndpoints: () => 
    api.get('/api/endpoints'),
  
  getEndpointById: (endpointId) => 
    api.get(`/api/endpoints/${endpointId}`),
  
  getEndpointsByProjectId: (projectId) => 
    api.get(`/api/endpoints/project/${projectId}`),
  
  createEndpoint: (endpointData) => 
    api.post('/api/endpoints', endpointData),
  
  updateEndpoint: (endpointId, endpointData) => 
    api.put(`/api/endpoints/${endpointId}`, endpointData),
  
  deleteEndpoint: (endpointId) => 
    api.delete(`/api/endpoints/${endpointId}`),
  
  scanSwagger: (projectId, swaggerUrl) => 
    api.post('/api/endpoints/scan', { projectId, swaggerUrl }),
};

// Export par défaut de l'instance Axios
export default api;

// ==========================================
// EXEMPLE D'UTILISATION DANS UN COMPOSANT
// ==========================================

/*
import { authService, projectService } from '../services/api';

// Dans un composant
const handleLogin = async () => {
  try {
    const response = await authService.login('user@example.com', 'password123');
    localStorage.setItem('accessToken', response.data.accessToken);
    console.log('Login successful');
  } catch (error) {
    console.error('Login failed:', error);
  }
};

const fetchProjects = async () => {
  try {
    const response = await projectService.getAllProjects();
    setProjects(response.data);
  } catch (error) {
    console.error('Error fetching projects:', error);
  }
};
*/