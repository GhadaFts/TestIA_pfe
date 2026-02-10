
import { 
  HomeIcon, 
  ServerIcon, 
  ChartBarIcon, 
  Cog6ToothIcon, 
 
} from '@heroicons/react/24/outline';

export const COLORS = {
  primary: '#6366f1', // Indigo 500
  primaryDark: '#4f46e5', // Indigo 600
  secondary: '#0f172a', // Slate 900
  success: '#10b981', // Emerald 500
  danger: '#ef4444', // Red 500
  warning: '#f59e0b', // Amber 500
  info: '#06b6d4', // Cyan 500
  background: '#f8fafc', // Slate 50
};

export const NAVIGATION = [
  { name: 'Dashboard', href: '/dashboard', icon: HomeIcon },
  { name: 'Services', href: '/services', icon: ServerIcon },
  { name: 'Rapports', href: '/reports', icon: ChartBarIcon },
  { name: 'Paramètres', href: '/settings', icon: Cog6ToothIcon },
];

export const MOCK_SERVICES = [
  { id: '1', name: 'User API', url: 'https://api.example.com/v1', status: 'active', endpointsCount: 12, lastTestDate: '2023-11-20', authType: 'bearer' },
  { id: '2', name: 'Payment Gateway', url: 'https://pay.example.com', status: 'active', endpointsCount: 8, lastTestDate: '2023-11-21', authType: 'apiKey' },
  { id: '3', name: 'Inventory Service', url: 'https://stock.example.com', status: 'inactive', endpointsCount: 15, lastTestDate: '2023-10-15', authType: 'none' },
];
