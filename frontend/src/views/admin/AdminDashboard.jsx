import React, { useState, useEffect } from 'react';
import Navbar from '../../components/Navbar';
import API from '../../services/api';
import { Landmark, ShieldAlert, CheckSquare, XCircle, Users } from 'lucide-react';

const AdminDashboard = () => {
  const [activeTab, setActiveTab] = useState('approvals');
  const [theatres, setTheatres] = useState([]);
  const [locations, setLocations] = useState([]);
  const [loading, setLoading] = useState(true);
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');

  const loadTheatres = async () => {
    setLoading(true);
    try {
      const response = await API.get('/theatres');
      setTheatres(response.data.data || []);
      const locResponse = await API.get('/locations');
      setLocations(locResponse.data.data || []);
    } catch (err) {
      console.error(err);
      setError('Failed to fetch theatre registry.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadTheatres();
  }, []);

  const handleApprove = async (id, approveStatus) => {
    setError(''); setMessage('');
    try {
      await API.put(`/theatres/${id}/approve`, null, {
        params: { status: approveStatus },
      });
      setMessage(`Theatre request successfully ${approveStatus.toLowerCase()}!`);
      loadTheatres();
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to update theatre status.');
    }
  };

  const pendingTheatres = theatres.filter(t => t.status === 'PENDING');
  const approvedTheatres = theatres.filter(t => t.status === 'APPROVED');

  return (
    <div className="min-h-screen bg-brand-dark text-white">
      <Navbar />

      <div className="flex pt-16 min-h-screen">
        {/* Sidebar */}
        <aside className="w-64 bg-brand-gray border-r border-white/5 p-4 space-y-2 shrink-0">
          <div className="px-4 py-3 border-b border-white/5 mb-4">
            <h4 className="font-bold text-sm text-blue-500 uppercase tracking-widest">Administrator</h4>
            <p className="text-[10px] text-gray-500 mt-0.5">Control Panel</p>
          </div>
          
          <button
            onClick={() => setActiveTab('approvals')}
            className={`w-full flex items-center space-x-3 px-4 py-3 rounded-lg text-sm font-semibold transition-all ${
              activeTab === 'approvals'
                ? 'bg-brand-red text-white shadow-lg'
                : 'text-gray-400 hover:bg-brand-light hover:text-white'
            }`}
          >
            <ShieldAlert className="h-4 w-4" />
            <span>Pending Approvals ({pendingTheatres.length})</span>
          </button>

          <button
            onClick={() => setActiveTab('cinemas')}
            className={`w-full flex items-center space-x-3 px-4 py-3 rounded-lg text-sm font-semibold transition-all ${
              activeTab === 'cinemas'
                ? 'bg-brand-red text-white shadow-lg'
                : 'text-gray-400 hover:bg-brand-light hover:text-white'
            }`}
          >
            <Landmark className="h-4 w-4" />
            <span>Cinema Directory ({approvedTheatres.length})</span>
          </button>
        </aside>

        {/* Content */}
        <main className="flex-1 p-8">
          <div className="mb-6">
            <h1 className="text-3xl font-black">Admin Dashboard</h1>
            <p className="text-xs text-gray-400 mt-1 font-semibold">Monitor system properties and approve cinema submissions.</p>
          </div>

          {message && (
            <div className="mb-6 bg-green-950/60 border border-green-500/40 text-green-200 px-4 py-3 rounded text-sm font-semibold flex items-center space-x-2">
              <CheckSquare className="h-5 w-5 text-green-500" />
              <span>{message}</span>
            </div>
          )}

          {error && (
            <div className="mb-6 bg-red-950/60 border border-red-500/40 text-red-200 px-4 py-3 rounded text-sm font-semibold flex items-center space-x-2">
              <XCircle className="h-5 w-5 text-brand-red" />
              <span>{error}</span>
            </div>
          )}

          {loading ? (
            <div className="flex justify-center py-12">
              <div className="animate-spin rounded-full h-8 w-8 border-t-2 border-b-2 border-brand-red"></div>
            </div>
          ) : activeTab === 'approvals' ? (
            <div className="bg-brand-gray border border-white/5 rounded-xl p-6">
              <h3 className="text-lg font-bold mb-4">Pending Approvals</h3>
              <div className="overflow-x-auto">
                <table className="w-full text-left text-sm border-collapse">
                  <thead>
                    <tr className="border-b border-white/5 text-gray-400 font-bold text-xs uppercase">
                      <th className="pb-3">Theatre Name</th>
                      <th className="pb-3">Location</th>
                      <th className="pb-3">Address</th>
                      <th className="pb-3 text-right">Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    {pendingTheatres.map((t) => (
                      <tr key={t.id} className="border-b border-white/5 text-gray-300">
                        <td className="py-3.5 font-bold">{t.name}</td>
                        <td className="py-3.5">
                          {(() => {
                            const loc = locations.find(l => l.id === t.locationId);
                            return loc ? `${loc.city}, ${loc.state}` : t.locationId;
                          })()}
                        </td>
                        <td className="py-3.5">{t.address}</td>
                        <td className="py-3.5 text-right flex justify-end space-x-2">
                          <button
                            onClick={() => handleApprove(t.id, 'APPROVED')}
                            className="bg-green-600 hover:bg-green-700 text-white font-bold px-3 py-1 rounded text-xs transition-colors"
                          >
                            Approve
                          </button>
                          <button
                            onClick={() => handleApprove(t.id, 'REJECTED')}
                            className="bg-red-600 hover:bg-red-700 text-white font-bold px-3 py-1 rounded text-xs transition-colors"
                          >
                            Reject
                          </button>
                        </td>
                      </tr>
                    ))}
                    {pendingTheatres.length === 0 && (
                      <tr>
                        <td colSpan="4" className="py-4 text-center text-gray-500 font-semibold">No pending theatre approvals.</td>
                      </tr>
                    )}
                  </tbody>
                </table>
              </div>
            </div>
          ) : (
            <div className="bg-brand-gray border border-white/5 rounded-xl p-6">
              <h3 className="text-lg font-bold mb-4">Cinema Directory</h3>
              <div className="overflow-x-auto">
                <table className="w-full text-left text-sm border-collapse">
                  <thead>
                    <tr className="border-b border-white/5 text-gray-400 font-bold text-xs uppercase">
                      <th className="pb-3">Theatre Name</th>
                      <th className="pb-3">Location</th>
                      <th className="pb-3">Address</th>
                      <th className="pb-3">Status</th>
                    </tr>
                  </thead>
                  <tbody>
                    {approvedTheatres.map((t) => (
                      <tr key={t.id} className="border-b border-white/5 text-gray-300">
                        <td className="py-3.5 font-bold">{t.name}</td>
                        <td className="py-3.5">
                          {(() => {
                            const loc = locations.find(l => l.id === t.locationId);
                            return loc ? `${loc.city}, ${loc.state}` : t.locationId;
                          })()}
                        </td>
                        <td className="py-3.5">{t.address}</td>
                        <td className="py-3.5">
                          <span className="text-[10px] font-black uppercase bg-green-950 text-green-400 border border-green-500/30 px-2 py-0.5 rounded">
                            {t.status}
                          </span>
                        </td>
                      </tr>
                    ))}
                    {approvedTheatres.length === 0 && (
                      <tr>
                        <td colSpan="4" className="py-4 text-center text-gray-500 font-semibold">No approved theatres in registry.</td>
                      </tr>
                    )}
                  </tbody>
                </table>
              </div>
            </div>
          )}
        </main>
      </div>
    </div>
  );
};

export default AdminDashboard;
