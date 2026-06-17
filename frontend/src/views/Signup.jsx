import React, { useState, useContext } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { AuthContext } from '../context/AuthContext';
import { Film, Lock, Mail, User, Phone, ShieldAlert } from 'lucide-react';

const Signup = () => {
  const { register } = useContext(AuthContext);
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [phone, setPhone] = useState('');
  const [role, setRole] = useState('USER'); // Defaults to USER
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');
    setSubmitting(true);

    const result = await register(name, email, password, role, phone);
    setSubmitting(false);

    if (result.success) {
      setSuccess('Account created successfully! Redirecting to sign in...');
      setTimeout(() => {
        navigate('/login');
      }, 2000);
    } else {
      setError(result.message);
    }
  };

  return (
    <div className="min-h-screen bg-[url('https://images.unsplash.com/photo-1536440136628-849c177e76a1?q=80&w=1470')] bg-cover bg-center flex items-center justify-center p-4 relative">
      <div className="absolute inset-0 bg-black/80"></div>

      <div className="relative z-10 w-full max-w-md bg-brand-dark/90 border border-white/5 rounded-xl shadow-2xl p-8 backdrop-blur-md">
        <div className="flex flex-col items-center mb-6">
          <div className="flex items-center space-x-2">
            <Film className="h-10 w-10 text-brand-red animate-pulse" />
            <span className="text-3xl font-black text-brand-red">
              Cine<span className="text-white">Reserve</span>
            </span>
          </div>
          <p className="text-gray-400 text-sm mt-2 font-semibold">Join us to reserve your favorite seats</p>
        </div>

        {error && (
          <div className="mb-4 bg-red-950/60 border border-red-500/40 text-red-200 px-4 py-3 rounded text-sm font-semibold">
            {error}
          </div>
        )}

        {success && (
          <div className="mb-4 bg-green-950/60 border border-green-500/40 text-green-200 px-4 py-3 rounded text-sm font-semibold">
            {success}
          </div>
        )}

        <form onSubmit={handleSubmit} className="space-y-4">
          {/* Full Name */}
          <div className="space-y-1">
            <label className="text-xs font-bold text-gray-400 uppercase">Full Name</label>
            <div className="relative">
              <User className="absolute left-3 top-1/2 -translate-y-1/2 h-5 w-5 text-gray-500" />
              <input
                type="text"
                required
                value={name}
                onChange={(e) => setName(e.target.value)}
                placeholder="John Doe"
                className="w-full bg-brand-light text-white pl-10 pr-4 py-2.5 rounded outline-none border border-transparent focus:border-brand-red focus:bg-black transition-all text-sm"
              />
            </div>
          </div>

          {/* Email */}
          <div className="space-y-1">
            <label className="text-xs font-bold text-gray-400 uppercase">Email Address</label>
            <div className="relative">
              <Mail className="absolute left-3 top-1/2 -translate-y-1/2 h-5 w-5 text-gray-500" />
              <input
                type="email"
                required
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="john@example.com"
                className="w-full bg-brand-light text-white pl-10 pr-4 py-2.5 rounded outline-none border border-transparent focus:border-brand-red focus:bg-black transition-all text-sm"
              />
            </div>
          </div>

          {/* Password */}
          <div className="space-y-1">
            <label className="text-xs font-bold text-gray-400 uppercase">Password</label>
            <div className="relative">
              <Lock className="absolute left-3 top-1/2 -translate-y-1/2 h-5 w-5 text-gray-500" />
              <input
                type="password"
                required
                minLength={6}
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="At least 6 characters"
                className="w-full bg-brand-light text-white pl-10 pr-4 py-2.5 rounded outline-none border border-transparent focus:border-brand-red focus:bg-black transition-all text-sm"
              />
            </div>
          </div>

          {/* Phone Number */}
          <div className="space-y-1">
            <label className="text-xs font-bold text-gray-400 uppercase">Phone Number</label>
            <div className="relative">
              <Phone className="absolute left-3 top-1/2 -translate-y-1/2 h-5 w-5 text-gray-500" />
              <input
                type="tel"
                required
                value={phone}
                onChange={(e) => setPhone(e.target.value)}
                placeholder="1234567890"
                className="w-full bg-brand-light text-white pl-10 pr-4 py-2.5 rounded outline-none border border-transparent focus:border-brand-red focus:bg-black transition-all text-sm"
              />
            </div>
          </div>

          {/* Account Role */}
          <div className="space-y-1">
            <label className="text-xs font-bold text-gray-400 uppercase">Account Type</label>
            <div className="relative">
              <ShieldAlert className="absolute left-3 top-1/2 -translate-y-1/2 h-5 w-5 text-gray-500" />
              <select
                value={role}
                onChange={(e) => setRole(e.target.value)}
                className="w-full bg-brand-light text-white pl-10 pr-4 py-2.5 rounded outline-none border border-transparent focus:border-brand-red focus:bg-black transition-all text-sm appearance-none"
              >
                <option value="USER">Standard Customer</option>
                <option value="THEATRE_OWNER">Theatre Owner</option>
              </select>
            </div>
          </div>

          {/* Submit */}
          <button
            type="submit"
            disabled={submitting}
            className="w-full bg-brand-red hover:bg-red-700 disabled:bg-red-900/40 text-white font-bold py-3 rounded transition-all hover:scale-[1.02] shadow-lg shadow-brand-red/30 flex items-center justify-center space-x-2 text-sm mt-4"
          >
            {submitting ? (
              <div className="animate-spin rounded-full h-5 w-5 border-t-2 border-b-2 border-white"></div>
            ) : (
              <span>Create Account</span>
            )}
          </button>
        </form>

        <p className="text-center text-sm text-gray-400 mt-6 font-semibold">
          Already have an account?{' '}
          <Link to="/login" className="text-white font-bold hover:underline hover:text-brand-red transition-colors">
            Sign In
          </Link>
        </p>
      </div>
    </div>
  );
};

export default Signup;
