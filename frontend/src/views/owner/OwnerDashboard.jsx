import React, { useState, useEffect, useContext } from 'react';
import Navbar from '../../components/Navbar';
import Sidebar from '../../components/Sidebar';
import API from '../../services/api';
import { AuthContext } from '../../context/AuthContext';
import { LayoutGrid, Film, Tv, CalendarRange, Landmark, ListOrdered, CheckCircle2, XSquare } from 'lucide-react';

const OwnerDashboard = () => {
  const { user } = useContext(AuthContext);
  const [activeTab, setActiveTab] = useState('analytics');

  // Locations & Theatres lists
  const [locations, setLocations] = useState([]);
  const [theatres, setTheatres] = useState([]);
  const [movies, setMovies] = useState([]);
  const [layouts, setLayouts] = useState([]);
  const [screens, setScreens] = useState([]);       // all screens (for analytics)
  const [showScreens, setShowScreens] = useState([]); // screens for the selected theatre in Schedule Shows tab
  const [shows, setShows] = useState([]);

  // Form states
  const [theatreForm, setTheatreForm] = useState({ name: '', locationId: '', address: '' });
  const [movieForm, setMovieForm] = useState({
    title: '', description: '', genres: '', duration: '', rating: '',
    languages: '', releaseDate: '', poster: '', trailer: '', status: 'NOW_SHOWING'
  });
  const [layoutForm, setLayoutForm] = useState({ name: '', rowNames: '', colCount: '' });
  const [screenForm, setScreenForm] = useState({ theatreId: '', name: '', seatLayoutId: '' });
  const [showForm, setShowForm] = useState({
    movieId: '', screenId: '', theatreId: '', showDate: '',
    startTime: '', endTime: '', vipPrice: '', normalPrice: ''
  });

  const [message, setMessage] = useState('');
  const [error, setError] = useState('');

  const loadData = async () => {
    try {
      const locRes = await API.get('/locations');
      setLocations(locRes.data.data || []);

      const movieRes = await API.get('/movies', { params: { size: 100 } });
      setMovies(movieRes.data.data.content || []);

      const layoutRes = await API.get('/seatlayouts');
      setLayouts(layoutRes.data.data || []);

      const theatreRes = await API.get('/theatres');
      const allTheatres = theatreRes.data.data || [];
      setTheatres(allTheatres);

      // Fetch screens for ALL theatres to compute the analytics total count
      if (allTheatres.length > 0) {
        const screenRequests = allTheatres.map(t =>
          API.get(`/screens/theatre/${t.id}`).then(r => r.data.data || []).catch(() => [])
        );
        const allScreenArrays = await Promise.all(screenRequests);
        setScreens(allScreenArrays.flat());
      }
    } catch (err) {
      console.error(err);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  const handleTheatreSubmit = async (e) => {
    e.preventDefault();
    setError(''); setMessage('');
    try {
      await API.post('/theatres', theatreForm);
      setMessage('Theatre registration request submitted successfully. Awaiting Admin approval.');
      setTheatreForm({ name: '', locationId: '', address: '' });
      loadData();
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to register theatre.');
    }
  };

  const handleMovieSubmit = async (e) => {
    e.preventDefault();
    setError(''); setMessage('');
    try {
      const genresList = movieForm.genres.split(',').map(g => g.trim());
      const languagesList = movieForm.languages.split(',').map(l => l.trim());

      await API.post('/movies', {
        ...movieForm,
        genres: genresList,
        languages: languagesList,
        duration: parseInt(movieForm.duration, 10),
        rating: parseFloat(movieForm.rating)
      });
      setMessage('Movie added successfully to catalog.');
      setMovieForm({
        title: '', description: '', genres: '', duration: '', rating: '',
        languages: '', releaseDate: '', poster: '', trailer: '', status: 'NOW_SHOWING'
      });
      loadData();
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to add movie.');
    }
  };

  const handleLayoutSubmit = async (e) => {
    e.preventDefault();
    setError(''); setMessage('');
    try {
      const rowList = layoutForm.rowNames.split(',').map(r => r.trim().toUpperCase());
      const col = parseInt(layoutForm.colCount, 10);

      // Initialize all grid coordinates as NORMAL for default config
      const seats = [];
      rowList.forEach(row => {
        for (let c = 1; c <= col; c++) {
          seats.push({ rowName: row, colIndex: c, seatType: 'NORMAL' });
        }
      });

      await API.post('/seatlayouts', {
        name: layoutForm.name,
        rowNames: rowList,
        colCount: col,
        seats: seats
      });
      setMessage('Seat layout configuration defined successfully.');
      setLayoutForm({ name: '', rowNames: '', colCount: '' });
      loadData();
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to create seat layout.');
    }
  };

  const handleScreenSubmit = async (e) => {
    e.preventDefault();
    setError(''); setMessage('');
    try {
      await API.post('/screens', screenForm);
      setMessage('Cinema screen registered successfully.');
      setScreenForm({ theatreId: '', name: '', seatLayoutId: '' });
      loadData();
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to create screen.');
    }
  };

  const handleShowSubmit = async (e) => {
    e.preventDefault();
    setError(''); setMessage('');
    try {
      // Look up layout seats for the screen using showScreens (screens for the selected theatre in shows tab)
      const screenObj = showScreens.find(s => s.id === showForm.screenId);
      if (!screenObj) {
        throw new Error('Please register a screen first.');
      }
      const layoutObj = layouts.find(l => l.id === screenObj.seatLayoutId);
      if (!layoutObj) {
        throw new Error('Seat layout metadata missing.');
      }

      const seatsToInit = layoutObj.seats.map(s => ({
        seatNumber: `${s.rowName}${s.colIndex}`,
        seatType: s.seatType
      }));

      const priceMap = {
        NORMAL: parseFloat(showForm.normalPrice),
        VIP: parseFloat(showForm.vipPrice)
      };

      await API.post('/booking/shows', {
        movieId: showForm.movieId,
        screenId: showForm.screenId,
        theatreId: showForm.theatreId,
        showDate: showForm.showDate,
        startTime: showForm.startTime + ':00',
        endTime: showForm.endTime + ':00',
        priceMap: priceMap,
        seats: seatsToInit
      });

      setMessage('Show scheduled successfully.');
      setShowForm({
        movieId: '', screenId: '', theatreId: '', showDate: '',
        startTime: '', endTime: '', vipPrice: '', normalPrice: ''
      });
      setShowScreens([]);
    } catch (err) {
      setError(err.message || err.response?.data?.message || 'Failed to schedule show.');
    }
  };

  // Handler for the 'Manage Screens' tab — only updates screenForm
  const selectTheatreForScreen = (theatreId) => {
    setScreenForm({ ...screenForm, theatreId, name: '', seatLayoutId: '' });
  };

  // Handler for the 'Schedule Shows' tab — updates showForm and fetches screens for that theatre
  const selectTheatreForShow = async (theatreId) => {
    setShowForm({ ...showForm, theatreId, screenId: '' });
    setShowScreens([]);
    if (!theatreId) return;
    try {
      const screensRes = await API.get(`/screens/theatre/${theatreId}`);
      setShowScreens(screensRes.data.data || []);
    } catch (err) {
      console.error(err);
    }
  };

  const sidebarLinks = [
    { to: '#analytics', label: 'Analytics Panel', icon: <ListOrdered className="h-4 w-4" /> },
    { to: '#theatre', label: 'Register Theatre', icon: <Landmark className="h-4 w-4" /> },
    { to: '#movies', label: 'Add Movie', icon: <Film className="h-4 w-4" /> },
    { to: '#layouts', label: 'Seat Layouts', icon: <LayoutGrid className="h-4 w-4" /> },
    { to: '#screens', label: 'Manage Screens', icon: <Tv className="h-4 w-4" /> },
    { to: '#shows', label: 'Manage Shows', icon: <CalendarRange className="h-4 w-4" /> }
  ];

  return (
    <div className="min-h-screen bg-brand-dark text-white">
      <Navbar />
      <div className="flex pt-16 min-h-screen">
        {/* Sidebar Navigation */}
        <aside className="w-64 bg-brand-gray border-r border-white/5 p-4 space-y-2 shrink-0">
          <div className="px-4 py-3 border-b border-white/5 mb-4">
            <h4 className="font-bold text-sm text-yellow-500 uppercase tracking-widest">Theatre Owner</h4>
            <p className="text-[10px] text-gray-500 mt-0.5">CineReserve Portal</p>
          </div>
          {sidebarLinks.map((link) => (
            <button
              key={link.to}
              onClick={() => {
                setActiveTab(link.to.replace('#', ''));
                setError(''); setMessage('');
              }}
              className={`w-full flex items-center space-x-3 px-4 py-3 rounded-lg text-sm font-semibold transition-all ${
                activeTab === link.to.replace('#', '')
                  ? 'bg-brand-red text-white shadow-lg shadow-brand-red/20'
                  : 'text-gray-400 hover:bg-brand-light hover:text-white'
              }`}
            >
              {link.icon}
              <span>{link.label}</span>
            </button>
          ))}
        </aside>

        {/* Content Pane */}
        <main className="flex-1 p-8 overflow-y-auto">
          {message && (
            <div className="mb-6 bg-green-950/60 border border-green-500/40 text-green-200 px-4 py-3 rounded text-sm font-semibold flex items-center space-x-2">
              <CheckCircle2 className="h-5 w-5 text-green-500" />
              <span>{message}</span>
            </div>
          )}

          {error && (
            <div className="mb-6 bg-red-950/60 border border-red-500/40 text-red-200 px-4 py-3 rounded text-sm font-semibold flex items-center space-x-2">
              <XSquare className="h-5 w-5 text-brand-red" />
              <span>{error}</span>
            </div>
          )}

          {/* Analytics View */}
          {activeTab === 'analytics' && (
            <div className="space-y-6">
              <h2 className="text-2xl font-black">Analytics Overview</h2>
              <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                <div className="bg-brand-gray border border-white/5 rounded-xl p-6">
                  <h4 className="text-xs text-gray-400 font-bold uppercase">Registered Theatres</h4>
                  <p className="text-3xl font-black mt-2 text-white">{theatres.length}</p>
                </div>
                <div className="bg-brand-gray border border-white/5 rounded-xl p-6">
                  <h4 className="text-xs text-gray-400 font-bold uppercase">Active Screens</h4>
                  <p className="text-3xl font-black mt-2 text-yellow-500">{screens.length}</p>
                </div>
                <div className="bg-brand-gray border border-white/5 rounded-xl p-6">
                  <h4 className="text-xs text-gray-400 font-bold uppercase">Movies Listed</h4>
                  <p className="text-3xl font-black mt-2 text-brand-red">{movies.length}</p>
                </div>
              </div>

              {/* Theatres Listing */}
              <div className="bg-brand-gray border border-white/5 rounded-xl p-6 mt-8">
                <h3 className="text-lg font-bold mb-4">Cinema approval status roster</h3>
                <div className="overflow-x-auto">
                  <table className="w-full text-left text-sm border-collapse">
                    <thead>
                      <tr className="border-b border-white/5 text-gray-400 font-bold text-xs uppercase">
                        <th className="pb-3">Name</th>
                        <th className="pb-3">Location</th>
                        <th className="pb-3">Address</th>
                        <th className="pb-3">Status</th>
                      </tr>
                    </thead>
                    <tbody>
                      {theatres.map((t) => (
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
                            <span
                              className={`text-[10px] font-black uppercase px-2 py-0.5 rounded ${
                                t.status === 'APPROVED' ? 'bg-green-950 text-green-400' : 'bg-yellow-950 text-yellow-500'
                              }`}
                            >
                              {t.status}
                            </span>
                          </td>
                        </tr>
                      ))}
                      {theatres.length === 0 && (
                        <tr>
                          <td colSpan="4" className="py-4 text-center text-gray-500 font-semibold">No cinemas registered yet.</td>
                        </tr>
                      )}
                    </tbody>
                  </table>
                </div>
              </div>
            </div>
          )}

          {/* Register Theatre Form */}
          {activeTab === 'theatre' && (
            <div className="max-w-xl bg-brand-gray border border-white/5 rounded-xl p-8 space-y-6">
              <h2 className="text-2xl font-black border-b border-white/5 pb-4">Register Cinema Theatre</h2>
              <form onSubmit={handleTheatreSubmit} className="space-y-4">
                <div className="space-y-1">
                  <label className="text-xs font-bold text-gray-400 uppercase">Theatre Name</label>
                  <input
                    type="text"
                    required
                    value={theatreForm.name}
                    onChange={(e) => setTheatreForm({ ...theatreForm, name: e.target.value })}
                    placeholder="Grand Cinema IMAX"
                    className="w-full bg-brand-light text-white px-4 py-2.5 rounded outline-none border border-transparent focus:border-brand-red text-sm"
                  />
                </div>
                <div className="space-y-1">
                  <label className="text-xs font-bold text-gray-400 uppercase">Location Zone</label>
                  <select
                    required
                    value={theatreForm.locationId}
                    onChange={(e) => setTheatreForm({ ...theatreForm, locationId: e.target.value })}
                    className="w-full bg-brand-light text-white px-4 py-2.5 rounded outline-none border border-transparent focus:border-brand-red text-sm"
                  >
                    <option value="">Select Location</option>
                    {locations.map(loc => (
                      <option key={loc.id} value={loc.id}>{loc.city}, {loc.state}</option>
                    ))}
                  </select>
                </div>
                <div className="space-y-1">
                  <label className="text-xs font-bold text-gray-400 uppercase">Street Address</label>
                  <input
                    type="text"
                    required
                    value={theatreForm.address}
                    onChange={(e) => setTheatreForm({ ...theatreForm, address: e.target.value })}
                    placeholder="123 Broad St, Midtown"
                    className="w-full bg-brand-light text-white px-4 py-2.5 rounded outline-none border border-transparent focus:border-brand-red text-sm"
                  />
                </div>
                <button
                  type="submit"
                  className="bg-brand-red hover:bg-red-700 text-white font-bold px-6 py-2.5 rounded text-xs uppercase tracking-wider transition-all"
                >
                  Submit Registration
                </button>
              </form>
            </div>
          )}

          {/* Add Movie Form */}
          {activeTab === 'movies' && (
            <div className="max-w-2xl bg-brand-gray border border-white/5 rounded-xl p-8 space-y-6">
              <h2 className="text-2xl font-black border-b border-white/5 pb-4">Add Movie to Catalog</h2>
              <form onSubmit={handleMovieSubmit} className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div className="space-y-1">
                  <label className="text-xs font-bold text-gray-400 uppercase">Movie Title</label>
                  <input
                    type="text"
                    required
                    value={movieForm.title}
                    onChange={(e) => setMovieForm({ ...movieForm, title: e.target.value })}
                    placeholder="Inception"
                    className="w-full bg-brand-light text-white px-4 py-2.5 rounded outline-none border border-transparent focus:border-brand-red text-sm"
                  />
                </div>
                <div className="space-y-1">
                  <label className="text-xs font-bold text-gray-400 uppercase">Release Date</label>
                  <input
                    type="date"
                    required
                    value={movieForm.releaseDate}
                    onChange={(e) => setMovieForm({ ...movieForm, releaseDate: e.target.value })}
                    className="w-full bg-brand-light text-white px-4 py-2.5 rounded outline-none border border-transparent focus:border-brand-red text-sm"
                  />
                </div>
                <div className="md:col-span-2 space-y-1">
                  <label className="text-xs font-bold text-gray-400 uppercase">Description</label>
                  <textarea
                    required
                    value={movieForm.description}
                    onChange={(e) => setMovieForm({ ...movieForm, description: e.target.value })}
                    placeholder="Write a brief overview..."
                    className="w-full bg-brand-light text-white px-4 py-2.5 rounded outline-none border border-transparent focus:border-brand-red text-sm h-24 resize-none"
                  ></textarea>
                </div>
                <div className="space-y-1">
                  <label className="text-xs font-bold text-gray-400 uppercase">Genres (comma separated)</label>
                  <input
                    type="text"
                    required
                    value={movieForm.genres}
                    onChange={(e) => setMovieForm({ ...movieForm, genres: e.target.value })}
                    placeholder="Sci-Fi, Action, Thriller"
                    className="w-full bg-brand-light text-white px-4 py-2.5 rounded outline-none border border-transparent focus:border-brand-red text-sm"
                  />
                </div>
                <div className="space-y-1">
                  <label className="text-xs font-bold text-gray-400 uppercase">Languages (comma separated)</label>
                  <input
                    type="text"
                    required
                    value={movieForm.languages}
                    onChange={(e) => setMovieForm({ ...movieForm, languages: e.target.value })}
                    placeholder="English, French"
                    className="w-full bg-brand-light text-white px-4 py-2.5 rounded outline-none border border-transparent focus:border-brand-red text-sm"
                  />
                </div>
                <div className="space-y-1">
                  <label className="text-xs font-bold text-gray-400 uppercase">Duration (mins)</label>
                  <input
                    type="number"
                    required
                    value={movieForm.duration}
                    onChange={(e) => setMovieForm({ ...movieForm, duration: e.target.value })}
                    placeholder="148"
                    className="w-full bg-brand-light text-white px-4 py-2.5 rounded outline-none border border-transparent focus:border-brand-red text-sm"
                  />
                </div>
                <div className="space-y-1">
                  <label className="text-xs font-bold text-gray-400 uppercase">Critic Rating (0.0 to 10.0)</label>
                  <input
                    type="number"
                    step="0.1"
                    required
                    value={movieForm.rating}
                    onChange={(e) => setMovieForm({ ...movieForm, rating: e.target.value })}
                    placeholder="8.8"
                    className="w-full bg-brand-light text-white px-4 py-2.5 rounded outline-none border border-transparent focus:border-brand-red text-sm"
                  />
                </div>
                <div className="space-y-1">
                  <label className="text-xs font-bold text-gray-400 uppercase">Poster Image URL</label>
                  <input
                    type="url"
                    required
                    value={movieForm.poster}
                    onChange={(e) => setMovieForm({ ...movieForm, poster: e.target.value })}
                    placeholder="https://image-source.com/poster.jpg"
                    className="w-full bg-brand-light text-white px-4 py-2.5 rounded outline-none border border-transparent focus:border-brand-red text-sm"
                  />
                </div>
                <div className="space-y-1">
                  <label className="text-xs font-bold text-gray-400 uppercase">Catalog Status</label>
                  <select
                    value={movieForm.status}
                    onChange={(e) => setMovieForm({ ...movieForm, status: e.target.value })}
                    className="w-full bg-brand-light text-white px-4 py-2.5 rounded outline-none border border-transparent focus:border-brand-red text-sm"
                  >
                    <option value="NOW_SHOWING">Now Showing</option>
                    <option value="UPCOMING">Upcoming</option>
                  </select>
                </div>
                <div className="md:col-span-2 pt-4">
                  <button
                    type="submit"
                    className="bg-brand-red hover:bg-red-700 text-white font-bold px-6 py-2.5 rounded text-xs uppercase tracking-wider transition-all"
                  >
                    Add Movie to Catalog
                  </button>
                </div>
              </form>
            </div>
          )}

          {/* Seat Layout configuration */}
          {activeTab === 'layouts' && (
            <div className="max-w-xl bg-brand-gray border border-white/5 rounded-xl p-8 space-y-6">
              <h2 className="text-2xl font-black border-b border-white/5 pb-4">Define Seat Layout Config</h2>
              <form onSubmit={handleLayoutSubmit} className="space-y-4">
                <div className="space-y-1">
                  <label className="text-xs font-bold text-gray-400 uppercase">Layout Identifier Name</label>
                  <input
                    type="text"
                    required
                    value={layoutForm.name}
                    onChange={(e) => setLayoutForm({ ...layoutForm, name: e.target.value })}
                    placeholder="Standard 100 Seat Hall"
                    className="w-full bg-brand-light text-white px-4 py-2.5 rounded outline-none border border-transparent focus:border-brand-red text-sm"
                  />
                </div>
                <div className="space-y-1">
                  <label className="text-xs font-bold text-gray-400 uppercase">Row Names (comma separated letters)</label>
                  <input
                    type="text"
                    required
                    value={layoutForm.rowNames}
                    onChange={(e) => setLayoutForm({ ...layoutForm, rowNames: e.target.value })}
                    placeholder="A, B, C, D, E"
                    className="w-full bg-brand-light text-white px-4 py-2.5 rounded outline-none border border-transparent focus:border-brand-red text-sm"
                  />
                </div>
                <div className="space-y-1">
                  <label className="text-xs font-bold text-gray-400 uppercase">Column seat count per Row</label>
                  <input
                    type="number"
                    required
                    value={layoutForm.colCount}
                    onChange={(e) => setLayoutForm({ ...layoutForm, colCount: e.target.value })}
                    placeholder="12"
                    className="w-full bg-brand-light text-white px-4 py-2.5 rounded outline-none border border-transparent focus:border-brand-red text-sm"
                  />
                </div>
                <button
                  type="submit"
                  className="bg-brand-red hover:bg-red-700 text-white font-bold px-6 py-2.5 rounded text-xs uppercase tracking-wider transition-all"
                >
                  Create Configuration
                </button>
              </form>
            </div>
          )}

          {/* Manage Screens */}
          {activeTab === 'screens' && (
            <div className="max-w-xl bg-brand-gray border border-white/5 rounded-xl p-8 space-y-6">
              <h2 className="text-2xl font-black border-b border-white/5 pb-4">Manage Screens</h2>
              <form onSubmit={handleScreenSubmit} className="space-y-4">
                <div className="space-y-1">
                  <label className="text-xs font-bold text-gray-400 uppercase">Select Theatre</label>
                  <select
                    required
                    value={screenForm.theatreId}
                    onChange={(e) => selectTheatreForScreen(e.target.value)}
                    className="w-full bg-brand-light text-white px-4 py-2.5 rounded outline-none border border-transparent focus:border-brand-red text-sm"
                  >
                    <option value="">Select Theatre</option>
                    {theatres.filter(t => t.status === 'APPROVED').map(t => (
                      <option key={t.id} value={t.id}>{t.name}</option>
                    ))}
                  </select>
                </div>
                <div className="space-y-1">
                  <label className="text-xs font-bold text-gray-400 uppercase">Screen Label Name</label>
                  <input
                    type="text"
                    required
                    value={screenForm.name}
                    onChange={(e) => setScreenForm({ ...screenForm, name: e.target.value })}
                    placeholder="Screen 01 (Dolby Atmos)"
                    className="w-full bg-brand-light text-white px-4 py-2.5 rounded outline-none border border-transparent focus:border-brand-red text-sm"
                  />
                </div>
                <div className="space-y-1">
                  <label className="text-xs font-bold text-gray-400 uppercase">Select Seat Layout Configuration</label>
                  <select
                    required
                    value={screenForm.seatLayoutId}
                    onChange={(e) => setScreenForm({ ...screenForm, seatLayoutId: e.target.value })}
                    className="w-full bg-brand-light text-white px-4 py-2.5 rounded outline-none border border-transparent focus:border-brand-red text-sm"
                  >
                    <option value="">Select Layout</option>
                    {layouts.map(lay => (
                      <option key={lay.id} value={lay.id}>{lay.name}</option>
                    ))}
                  </select>
                </div>
                <button
                  type="submit"
                  className="bg-brand-red hover:bg-red-700 text-white font-bold px-6 py-2.5 rounded text-xs uppercase tracking-wider transition-all"
                >
                  Register Screen
                </button>
              </form>
            </div>
          )}

          {/* Schedule Shows */}
          {activeTab === 'shows' && (
            <div className="max-w-2xl bg-brand-gray border border-white/5 rounded-xl p-8 space-y-6">
              <h2 className="text-2xl font-black border-b border-white/5 pb-4">Schedule Show slots</h2>
              <form onSubmit={handleShowSubmit} className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div className="space-y-1">
                  <label className="text-xs font-bold text-gray-400 uppercase">Select Cinema Theatre</label>
                  <select
                    required
                    value={showForm.theatreId}
                    onChange={(e) => selectTheatreForShow(e.target.value)}
                    className="w-full bg-brand-light text-white px-4 py-2.5 rounded outline-none border border-transparent focus:border-brand-red text-sm"
                  >
                    <option value="">Select Theatre</option>
                    {theatres.filter(t => t.status === 'APPROVED').map(t => (
                      <option key={t.id} value={t.id}>{t.name}</option>
                    ))}
                  </select>
                </div>
                <div className="space-y-1">
                  <label className="text-xs font-bold text-gray-400 uppercase">Select Screen</label>
                  <select
                    required
                    value={showForm.screenId}
                    onChange={(e) => setShowForm({ ...showForm, screenId: e.target.value })}
                    className="w-full bg-brand-light text-white px-4 py-2.5 rounded outline-none border border-transparent focus:border-brand-red text-sm"
                  >
                    <option value="">Select Screen</option>
                    {showScreens.map(scr => (
                      <option key={scr.id} value={scr.id}>{scr.name}</option>
                    ))}
                  </select>
                </div>
                <div className="space-y-1">
                  <label className="text-xs font-bold text-gray-400 uppercase">Select Movie</label>
                  <select
                    required
                    value={showForm.movieId}
                    onChange={(e) => setShowForm({ ...showForm, movieId: e.target.value })}
                    className="w-full bg-brand-light text-white px-4 py-2.5 rounded outline-none border border-transparent focus:border-brand-red text-sm"
                  >
                    <option value="">Select Movie</option>
                    {movies.filter(m => m.status === 'NOW_SHOWING').map(mov => (
                      <option key={mov.id} value={mov.id}>{mov.title}</option>
                    ))}
                  </select>
                </div>
                <div className="space-y-1">
                  <label className="text-xs font-bold text-gray-400 uppercase">Show Date</label>
                  <input
                    type="date"
                    required
                    value={showForm.showDate}
                    onChange={(e) => setShowForm({ ...showForm, showDate: e.target.value })}
                    className="w-full bg-brand-light text-white px-4 py-2.5 rounded outline-none border border-transparent focus:border-brand-red text-sm"
                  />
                </div>
                <div className="space-y-1">
                  <label className="text-xs font-bold text-gray-400 uppercase">Start Time</label>
                  <input
                    type="time"
                    required
                    value={showForm.startTime}
                    onChange={(e) => setShowForm({ ...showForm, startTime: e.target.value })}
                    className="w-full bg-brand-light text-white px-4 py-2.5 rounded outline-none border border-transparent focus:border-brand-red text-sm"
                  />
                </div>
                <div className="space-y-1">
                  <label className="text-xs font-bold text-gray-400 uppercase">End Time</label>
                  <input
                    type="time"
                    required
                    value={showForm.endTime}
                    onChange={(e) => setShowForm({ ...showForm, endTime: e.target.value })}
                    className="w-full bg-brand-light text-white px-4 py-2.5 rounded outline-none border border-transparent focus:border-brand-red text-sm"
                  />
                </div>
                <div className="space-y-1">
                  <label className="text-xs font-bold text-gray-400 uppercase">Normal Seat Price ($)</label>
                  <input
                    type="number"
                    required
                    value={showForm.normalPrice}
                    onChange={(e) => setShowForm({ ...showForm, normalPrice: e.target.value })}
                    placeholder="12.50"
                    className="w-full bg-brand-light text-white px-4 py-2.5 rounded outline-none border border-transparent focus:border-brand-red text-sm"
                  />
                </div>
                <div className="space-y-1">
                  <label className="text-xs font-bold text-gray-400 uppercase">VIP Seat Price ($)</label>
                  <input
                    type="number"
                    required
                    value={showForm.vipPrice}
                    onChange={(e) => setShowForm({ ...showForm, vipPrice: e.target.value })}
                    placeholder="25.00"
                    className="w-full bg-brand-light text-white px-4 py-2.5 rounded outline-none border border-transparent focus:border-brand-red text-sm"
                  />
                </div>
                <div className="md:col-span-2 pt-4">
                  <button
                    type="submit"
                    className="bg-brand-red hover:bg-red-700 text-white font-bold px-6 py-2.5 rounded text-xs uppercase tracking-wider transition-all"
                  >
                    Schedule Show Slot
                  </button>
                </div>
              </form>
            </div>
          )}
        </main>
      </div>
    </div>
  );
};

export default OwnerDashboard;
