import React, { useContext } from 'react';
import Navbar from '../components/Navbar';
import { AuthContext } from '../context/AuthContext';
import { User, Mail, ShieldAlert, Phone, Calendar } from 'lucide-react';

const UserProfile = () => {
  const { user } = useContext(AuthContext);

  return (
    <div className="min-h-screen bg-brand-dark text-white pb-24">
      <Navbar />

      <div className="max-w-2xl mx-auto px-4 sm:px-6 pt-24">
        <div className="mb-8 text-center md:text-left">
          <span className="text-brand-red text-xs font-black uppercase tracking-wider">User Profile</span>
          <h1 className="text-3xl font-black text-white">Account Settings</h1>
        </div>

        {user ? (
          <div className="bg-brand-gray border border-white/5 rounded-xl p-8 space-y-6">
            {/* Header Avatar card */}
            <div className="flex flex-col md:flex-row items-center gap-6 pb-6 border-b border-white/5">
              <div className="h-24 w-24 rounded-full bg-brand-red text-white flex items-center justify-center font-bold text-4xl uppercase shadow-xl border-4 border-white/10">
                {user.name.charAt(0)}
              </div>
              <div className="text-center md:text-left">
                <h3 className="text-2xl font-black">{user.name}</h3>
                <p className="text-xs text-brand-red font-black uppercase tracking-widest mt-1">{user.role}</p>
              </div>
            </div>

            {/* Profile fields */}
            <div className="space-y-4">
              <div className="flex items-center space-x-3 py-3 border-b border-white/5">
                <Mail className="h-5 w-5 text-gray-500" />
                <div>
                  <p className="text-[10px] text-gray-500 uppercase font-black">Email Address</p>
                  <p className="text-sm font-semibold text-white">{user.email}</p>
                </div>
              </div>

              <div className="flex items-center space-x-3 py-3 border-b border-white/5">
                <Phone className="h-5 w-5 text-gray-500" />
                <div>
                  <p className="text-[10px] text-gray-500 uppercase font-black">Phone Number</p>
                  <p className="text-sm font-semibold text-white">{user.phone}</p>
                </div>
              </div>

              <div className="flex items-center space-x-3 py-3 border-b border-white/5">
                <ShieldAlert className="h-5 w-5 text-gray-500" />
                <div>
                  <p className="text-[10px] text-gray-500 uppercase font-black">Security Level role</p>
                  <p className="text-sm font-semibold text-white">{user.role}</p>
                </div>
              </div>

              <div className="flex items-center space-x-3 py-3">
                <Calendar className="h-5 w-5 text-gray-500" />
                <div>
                  <p className="text-[10px] text-gray-500 uppercase font-black">Registered Since</p>
                  <p className="text-sm font-semibold text-white">
                    {user.createdAt ? new Date(user.createdAt).toLocaleDateString() : 'N/A'}
                  </p>
                </div>
              </div>
            </div>
          </div>
        ) : (
          <p className="text-center text-gray-500 font-semibold">Please sign in to view your profile details.</p>
        )}
      </div>
    </div>
  );
};

export default UserProfile;
