import React, { useContext, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { AuthContext } from '../context/AuthContext';
import { LocationContext } from '../context/LocationContext';
import { Film, LogOut, MapPin, User, Settings, Shield, Menu } from 'lucide-react';

const Navbar = ({ onSearchChange }) => {
  const { user, logout } = useContext(AuthContext);
  const { selectedLocation, locations, selectLocation } = useContext(LocationContext);
  const [dropdownOpen, setDropdownOpen] = useState(false);
  const [locDropdownOpen, setLocDropdownOpen] = useState(false);
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <nav className="fixed top-0 left-0 right-0 z-50 glassmorphism transition-all duration-300">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-16">
          {/* Logo */}
          <div className="flex items-center">
            <Link to="/" className="flex items-center space-x-2">
              <Film className="h-8 w-8 text-brand-red animate-pulse" />
              <span className="text-2xl font-black tracking-wider text-brand-red bg-clip-text">
                Cine<span className="text-white">Reserve</span>
              </span>
            </Link>
          </div>

          {/* Search bar & location selection */}
          <div className="hidden md:flex items-center flex-1 max-w-md mx-8">
            <input
              type="text"
              placeholder="Search movies by title, genre..."
              onChange={(e) => onSearchChange && onSearchChange(e.target.value)}
              className="w-full bg-brand-light text-white px-4 py-1.5 rounded-full text-sm outline-none border border-transparent focus:border-brand-red focus:bg-black transition-all"
            />
          </div>

          {/* Right menu options */}
          <div className="flex items-center space-x-6">
            {/* Location Selector */}
            <div className="relative">
              <button
                onClick={() => setLocDropdownOpen(!locDropdownOpen)}
                className="flex items-center space-x-1 text-sm font-semibold hover:text-brand-red text-gray-300 transition-colors"
              >
                <MapPin className="h-4 w-4 text-brand-red" />
                <span>{selectedLocation ? selectedLocation.city : 'Select Location'}</span>
              </button>
              {locDropdownOpen && (
                <div className="absolute right-0 mt-2 w-48 bg-brand-gray border border-white/5 rounded-md shadow-2xl py-1 z-50">
                  {locations.map((loc) => (
                    <button
                      key={loc.id}
                      onClick={() => {
                        selectLocation(loc);
                        setLocDropdownOpen(false);
                        navigate('/'); // Refresh catalog on dashboard
                      }}
                      className="w-full text-left px-4 py-2 text-sm text-gray-300 hover:bg-brand-red hover:text-white transition-colors"
                    >
                      {loc.city}, {loc.state}
                    </button>
                  ))}
                  {locations.length === 0 && (
                    <span className="block px-4 py-2 text-xs text-gray-500">No locations</span>
                  )}
                </div>
              )}
            </div>

            {/* Profile Dropdown */}
            {user ? (
              <div className="relative">
                <button
                  onClick={() => setDropdownOpen(!dropdownOpen)}
                  className="flex items-center space-x-2 focus:outline-none"
                >
                  <div className="h-8 w-8 rounded-full bg-brand-red text-white flex items-center justify-center font-bold uppercase text-sm border-2 border-transparent hover:border-white transition-all">
                    {user.name.charAt(0)}
                  </div>
                </button>
                {dropdownOpen && (
                  <div className="absolute right-0 mt-2 w-56 bg-brand-gray border border-white/5 rounded-md shadow-2xl py-1 z-50">
                    <div className="px-4 py-2 border-b border-white/5">
                      <p className="text-sm font-bold text-white truncate">{user.name}</p>
                      <p className="text-xs text-gray-400 truncate">{user.email}</p>
                    </div>
                    
                    <Link
                      to="/profile"
                      onClick={() => setDropdownOpen(false)}
                      className="flex items-center space-x-2 px-4 py-2 text-sm text-gray-300 hover:bg-brand-red hover:text-white transition-colors"
                    >
                      <User className="h-4 w-4" />
                      <span>My Profile</span>
                    </Link>

                    <Link
                      to="/history"
                      onClick={() => setDropdownOpen(false)}
                      className="flex items-center space-x-2 px-4 py-2 text-sm text-gray-300 hover:bg-brand-red hover:text-white transition-colors"
                    >
                      <Film className="h-4 w-4" />
                      <span>My Bookings</span>
                    </Link>

                    {/* Conditional Owner Dashboard */}
                    {user.role === 'THEATRE_OWNER' && (
                      <Link
                        to="/owner"
                        onClick={() => setDropdownOpen(false)}
                        className="flex items-center space-x-2 px-4 py-2 text-sm text-yellow-500 hover:bg-brand-red hover:text-white transition-colors"
                      >
                        <Shield className="h-4 w-4" />
                        <span>Owner Panel</span>
                      </Link>
                    )}

                    {/* Conditional Admin Dashboard */}
                    {user.role === 'ADMIN' && (
                      <Link
                        to="/admin"
                        onClick={() => setDropdownOpen(false)}
                        className="flex items-center space-x-2 px-4 py-2 text-sm text-blue-500 hover:bg-brand-red hover:text-white transition-colors"
                      >
                        <Shield className="h-4 w-4" />
                        <span>Admin Panel</span>
                      </Link>
                    )}

                    <button
                      onClick={handleLogout}
                      className="w-full text-left flex items-center space-x-2 px-4 py-2 text-sm text-brand-red hover:bg-brand-red hover:text-white transition-colors"
                    >
                      <LogOut className="h-4 w-4" />
                      <span>Sign Out</span>
                    </button>
                  </div>
                )}
              </div>
            ) : (
              <Link
                to="/login"
                className="bg-brand-red hover:bg-red-700 text-white px-4 py-1.5 rounded text-sm font-semibold transition-all hover:scale-105"
              >
                Sign In
              </Link>
            )}
          </div>
        </div>
      </div>
    </nav>
  );
};

export default Navbar;
