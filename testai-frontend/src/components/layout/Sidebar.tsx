
import React from 'react';
import { NavLink } from 'react-router-dom';
import { NAVIGATION } from '../../constants';

const Sidebar: React.FC = () => {
  return (
    <aside className="hidden md:flex flex-col w-64 bg-white border-r border-gray-200 min-h-[calc(100vh-64px)] p-4">
      <nav className="space-y-2 flex-1">
        {NAVIGATION.map((item) => (
          <NavLink
            key={item.name}
            to={item.href}
            className={({ isActive }) => `
              flex items-center gap-3 px-4 py-3 rounded-lg font-medium transition-all duration-200
              ${isActive 
                ? 'bg-primary/10 text-primary' 
                : 'text-gray-600 hover:bg-gray-50 hover:text-gray-900'}
            `}
          >
            <item.icon className="h-5 w-5" />
            {item.name}
          </NavLink>
        ))}
      </nav>
      <div className="mt-auto pt-6 border-t border-gray-100">
        <div className="bg-gray-50 p-4 rounded-xl">
          <p className="text-xs font-bold text-gray-400 uppercase mb-2">Support</p>
          <a href="#" className="text-sm text-gray-600 hover:text-primary">Documentation</a>
          <br />
          <a href="#" className="text-sm text-gray-600 hover:text-primary">Besoin d'aide ?</a>
        </div>
      </div>
    </aside>
  );
};

export default Sidebar;
