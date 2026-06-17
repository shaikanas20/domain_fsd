import React, { useState, useEffect, useContext } from 'react';
import Navbar from '../components/Navbar';
import MovieCard from '../components/MovieCard';
import API from '../services/api';
import { LocationContext } from '../context/LocationContext';
import { Play, Calendar, Film } from 'lucide-react';

const Dashboard = () => {
  const { selectedLocation } = useContext(LocationContext);
  const [movies, setMovies] = useState([]);
  const [searchQuery, setSearchQuery] = useState('');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchMovies = async () => {
      setLoading(true);
      try {
        const response = await API.get('/movies', {
          params: { size: 50 },
        });
        setMovies(response.data.data.content || []);
      } catch (error) {
        console.error('Failed to fetch movies', error);
      } finally {
        setLoading(false);
      }
    };
    fetchMovies();
  }, []);

  const handleSearch = (query) => {
    setSearchQuery(query.toLowerCase());
  };

  const filteredMovies = movies.filter((m) =>
    m.title.toLowerCase().includes(searchQuery) ||
    m.genres.some((g) => g.toLowerCase().includes(searchQuery))
  );

  const nowShowing = filteredMovies.filter((m) => m.status === 'NOW_SHOWING');
  const upcoming = filteredMovies.filter((m) => m.status === 'UPCOMING');

  const heroMovie = nowShowing[0] || movies[0];

  return (
    <div className="min-h-screen bg-brand-dark text-white pb-16">
      <Navbar onSearchChange={handleSearch} />

      {/* Hero Banner */}
      {heroMovie ? (
        <div className="relative h-[65vh] w-full overflow-hidden pt-16">
          <div className="absolute inset-0">
            <img
              src={heroMovie.poster}
              alt={heroMovie.title}
              className="w-full h-full object-cover opacity-35 filter blur-[2px] scale-105"
            />
            {/* Gradient overlays */}
            <div className="absolute inset-0 bg-gradient-to-t from-brand-dark via-transparent to-black/60"></div>
            <div className="absolute inset-0 bg-gradient-to-r from-brand-dark via-brand-dark/20 to-transparent"></div>
          </div>

          <div className="relative max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-full flex flex-col justify-end pb-12 z-10">
            <div className="max-w-xl space-y-4">
              <span className="bg-brand-red text-white text-xs font-black uppercase tracking-widest px-2.5 py-1 rounded">
                Featured Release
              </span>
              <h1 className="text-4xl md:text-6xl font-black tracking-tight">{heroMovie.title}</h1>
              <p className="text-gray-300 text-sm md:text-base line-clamp-3 leading-relaxed">{heroMovie.description}</p>
              <div className="flex items-center space-x-3 text-xs text-gray-400 font-bold">
                <span>{heroMovie.genres.join(' • ')}</span>
                <span>•</span>
                <span>{heroMovie.duration} Mins</span>
                <span>•</span>
                <span>{heroMovie.languages.join(', ')}</span>
              </div>
              <div className="flex space-x-4 pt-2">
                <a
                  href={`/movies/${heroMovie.id}`}
                  className="bg-brand-red hover:bg-red-700 text-white font-bold px-6 py-2.5 rounded flex items-center space-x-2 text-sm shadow-lg shadow-brand-red/20 transition-all hover:scale-105"
                >
                  <Play className="h-4 w-4 fill-white" />
                  <span>Book Tickets</span>
                </a>
              </div>
            </div>
          </div>
        </div>
      ) : (
        <div className="h-[40vh] flex items-center justify-center pt-16 text-gray-500 font-semibold text-sm">
          {loading ? (
            <div className="animate-spin rounded-full h-8 w-8 border-t-2 border-b-2 border-brand-red"></div>
          ) : (
            <div className="flex flex-col items-center space-y-2">
              <Film className="h-12 w-12 text-zinc-700" />
              <span>No movies featured at the moment.</span>
            </div>
          )}
        </div>
      )}

      {/* Location Filter Banner */}
      {selectedLocation && (
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 mt-8">
          <div className="bg-brand-gray border border-white/5 rounded-lg p-4 flex items-center justify-between">
            <span className="text-sm font-semibold text-gray-400">
              Showing cinemas and showtimes in <span className="text-white font-bold">{selectedLocation.city}</span>.
            </span>
          </div>
        </div>
      )}

      {/* Main Grid Content */}
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 mt-12 space-y-12">
        {/* Now Showing */}
        <div>
          <h2 className="text-xl font-black uppercase tracking-wider text-white border-l-4 border-brand-red pl-3 mb-6">
            Now Showing
          </h2>
          {loading ? (
            <div className="grid grid-cols-2 md:grid-cols-4 lg:grid-cols-5 gap-6">
              {[...Array(5)].map((_, i) => (
                <div key={i} className="aspect-[2/3] rounded-lg bg-zinc-900 animate-pulse border border-white/5"></div>
              ))}
            </div>
          ) : nowShowing.length > 0 ? (
            <div className="grid grid-cols-2 md:grid-cols-4 lg:grid-cols-5 gap-6">
              {nowShowing.map((movie) => (
                <MovieCard key={movie.id} movie={movie} />
              ))}
            </div>
          ) : (
            <p className="text-sm text-zinc-500 font-semibold">No movies currently showing.</p>
          )}
        </div>

        {/* Upcoming */}
        <div>
          <h2 className="text-xl font-black uppercase tracking-wider text-white border-l-4 border-brand-red pl-3 mb-6">
            Upcoming Releases
          </h2>
          {loading ? (
            <div className="grid grid-cols-2 md:grid-cols-4 lg:grid-cols-5 gap-6">
              {[...Array(5)].map((_, i) => (
                <div key={i} className="aspect-[2/3] rounded-lg bg-zinc-900 animate-pulse border border-white/5"></div>
              ))}
            </div>
          ) : upcoming.length > 0 ? (
            <div className="grid grid-cols-2 md:grid-cols-4 lg:grid-cols-5 gap-6">
              {upcoming.map((movie) => (
                <MovieCard key={movie.id} movie={movie} />
              ))}
            </div>
          ) : (
            <p className="text-sm text-zinc-500 font-semibold">No upcoming releases listed.</p>
          )}
        </div>
      </div>
    </div>
  );
};

export default Dashboard;
