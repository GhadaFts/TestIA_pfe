
import React from 'react';
import { HashRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import LandingPage from './pages/LandingPage';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import Dashboard from './pages/Dashboard';
import AddServicePage from './pages/AddServicePage';
import ServiceDetailsPage from './pages/ServiceDetailsPage';
import TestExecutionPage from './pages/TestExecutionPage';
import ReportsPage from './pages/ReportsPage';

const App: React.FC = () => {
  
  return (
    <Router>
       <Routes>
        <Route path="/" element={<LandingPage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
        
        {/* Protected Routes Simulation */}
        <Route path="/dashboard" element={<Dashboard />} />
        <Route path="/add-service" element={<AddServicePage />} />
        <Route path="/service/:id" element={<ServiceDetailsPage />} />
        <Route path="/service/:id/execute" element={<TestExecutionPage />} />
        <Route path="/reports" element={<ReportsPage />} />
        
        {/* Fallback */}
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </Router>
  );
};

export default App;
