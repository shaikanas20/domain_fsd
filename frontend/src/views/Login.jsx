import React, { useState, useContext } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { AuthContext } from '../context/AuthContext';
import { Film, Lock, Mail, Eye, EyeOff } from 'lucide-react';

const Login = () => {
  const { login } = useContext(AuthContext);
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [error, setError] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSubmitting(true);

    const result = await login(email, password);
    setSubmitting(false);

    if (result.success) {
      navigate('/');
    } else {
      setError(result.message);
    }
  };

  return (
    <div className="min-h-screen bg-[url('https://images.unsplash.com/photo-1536440136628-849c177e76a1?q=80&w=1470')] bg-cover bg-center flex items-center justify-center p-4 relative">
      {/* Dark overlay */}
      <div className="absolute inset-0 bg-black/80"></div>

      <div className="relative z-10 w-full max-w-md bg-brand-dark/90 border border-white/5 rounded-xl shadow-2xl p-8 backdrop-blur-md">
        <div className="flex flex-col items-center mb-8">
          <div className="flex items-center space-x-2">
            <Film className="h-10 w-10 text-brand-red animate-pulse" />
            <span className="text-3xl font-black text-brand-red">
              Cine<span className="text-white">Reserve</span>
            </span>
          </div>
          <p className="text-gray-400 text-sm mt-2 font-semibold">Sign in to book your cinematic experience</p>
        </div>

        {error && (
          <div className="mb-4 bg-red-950/60 border border-red-500/40 text-red-200 px-4 py-3 rounded text-sm font-semibold">
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit} className="space-y-6">
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
                placeholder="you@example.com"
                className="w-full bg-brand-light text-white pl-10 pr-4 py-3 rounded outline-none border border-transparent focus:border-brand-red focus:bg-black transition-all text-sm"
              />
            </div>
          </div>

          {/* Password */}
          <div className="space-y-1">
            <div className="flex justify-between items-center">
              <label className="text-xs font-bold text-gray-400 uppercase">Password</label>
              <a href="#" className="text-xs font-bold text-brand-red hover:underline">Forgot password?</a>
            </div>
            <div className="relative">
              <Lock className="absolute left-3 top-1/2 -translate-y-1/2 h-5 w-5 text-gray-500" />
              <input
                type={showPassword ? 'text' : 'password'}
                required
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="••••••••"
                className="w-full bg-brand-light text-white pl-10 pr-10 py-3 rounded outline-none border border-transparent focus:border-brand-red focus:bg-black transition-all text-sm"
              />
              <button
                type="button"
                onClick={() => setShowPassword(!showPassword)}
                className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-500 hover:text-white"
              >
                {showPassword ? <EyeOff className="h-5 w-5" /> : <Eye className="h-5 w-5" />}
              </button>
            </div>
          </div>

          {/* Submit */}
          <button
            type="submit"
            disabled={submitting}
            className="w-full bg-brand-red hover:bg-red-700 disabled:bg-red-900/40 text-white font-bold py-3 rounded transition-all hover:scale-[1.02] shadow-lg shadow-brand-red/30 flex items-center justify-center space-x-2 text-sm"
          >
            {submitting ? (
              <div className="animate-spin rounded-full h-5 w-5 border-t-2 border-b-2 border-white"></div>
            ) : (
              <span>Sign In</span>
            )}
          </button>
        </form>

        <p className="text-center text-sm text-gray-400 mt-8 font-semibold">
          New to CineReserve?{' '}
          <Link to="/signup" className="text-white font-bold hover:underline hover:text-brand-red transition-colors">
            Sign up now
          </Link>
        </p>
      </div>
    </div>
  );
};

export default Login;
