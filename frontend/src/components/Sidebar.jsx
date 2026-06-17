import React from 'react';
import { NavLink } from 'react-router-dom';

const Sidebar = ({ links }) => {
  return (
    <aside className="w-64 bg-brand-gray border-r border-white/5 h-screen sticky top-16 pt-6 flex flex-col justify-between">
      <div className="px-4 space-y-2">
        {links.map((link) => (
          <NavLink
            key={link.to}
            to={link.to}
            end
            className={({ isActive }) =>
              `flex items-center space-x-3 px-4 py-3 rounded-lg text-sm font-medium transition-all ${
                isActive
                  ? 'bg-brand-red text-white shadow-lg'
                  : 'text-gray-400 hover:bg-brand-light hover:text-white'
              }`
            }
          >
            {link.icon}
            <span>{link.label}</span>
          </NavLink>
        ))}
      </div>
      <div className="p-4 border-t border-white/5 text-xs text-gray-500 text-center">
        &copy; 2026 CineReserve
      </div>
    </aside>
  );
};

export default Sidebar;
