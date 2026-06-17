import React, { useState, useEffect, useContext } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import Navbar from '../components/Navbar';
import API from '../services/api';
import { LocationContext } from '../context/LocationContext';
import { Star, Clock, Globe2, Film, ChevronRight, Calendar, AlertCircle } from 'lucide-react';

const MovieDetails = () => {
  const { id } = useParams();
  const { selectedLocation } = useContext(LocationContext);
  const [movie, setMovie] = useState(null);
  const [theatres, setTheatres] = useState([]);
  const [shows, setShows] = useState([]);
  const [loading, setLoading] = useState(true);
  const [selectedDate, setSelectedDate] = useState(new Date().toISOString().split('T')[0]);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchMovieData = async () => {
      setLoading(true);
      try {
        const movieRes = await API.get(`/movies/${id}`);
        setMovie(movieRes.data.data);

        // Fetch shows for this movie
        const showsRes = await API.get(`/booking/shows/movie/${id}`);
        const allShows = showsRes.data.data || [];
        setShows(allShows);

        if (selectedLocation) {
          // Fetch theatres in this location
          const theatresRes = await API.get(`/theatres/location/${selectedLocation.id}`, {
            params: { status: 'APPROVED' },
          });
          setTheatres(theatresRes.data.data || []);
        }
      } catch (error) {
        console.error('Failed to load movie details', error);
      } finally {
        setLoading(false);
      }
    };
    fetchMovieData();
  }, [id, selectedLocation]);

  if (loading) {
    return (
      <div className="min-h-screen bg-brand-dark flex items-center justify-center">
        <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-brand-red"></div>
      </div>
    );
  }

  if (!movie) {
    return (
      <div className="min-h-screen bg-brand-dark text-white flex flex-col items-center justify-center">
        <AlertCircle className="h-12 w-12 text-brand-red mb-2" />
        <p className="font-bold">Movie not found.</p>
        <button onClick={() => navigate('/')} className="mt-4 bg-brand-red px-4 py-2 rounded text-sm">Back to Home</button>
      </div>
    );
  }

  // Filter shows by theatre and date
  const getTheatreShows = (theatreId) => {
    return shows.filter(
      (show) => show.theatreId === theatreId && show.showDate === selectedDate
    );
  };

  // Create date selections (today, tomorrow, day after)
  const dateOptions = [...Array(5)].map((_, i) => {
    const d = new Date();
    d.setDate(d.getDate() + i);
    return {
      label: d.toLocaleDateString('en-US', { weekday: 'short', day: 'numeric', month: 'short' }),
      value: d.toISOString().split('T')[0],
    };
  });

  return (
    <div className="min-h-screen bg-brand-dark text-white pb-24">
      <Navbar />

      {/* Movie Details Banner */}
      <div className="relative pt-16 min-h-[50vh] flex items-center">
        {/* Blurred background backdrop */}
        <div className="absolute inset-0">
          <img src={movie.poster} alt="" className="w-full h-full object-cover opacity-20 filter blur-lg scale-105" />
          <div className="absolute inset-0 bg-gradient-to-t from-brand-dark via-transparent to-black/35"></div>
        </div>

        {/* Content */}
        <div className="relative max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12 z-10 w-full">
          <div className="flex flex-col md:flex-row gap-8 items-center md:items-start">
            {/* Poster Card */}
            <div className="w-64 shrink-0 rounded-lg overflow-hidden border border-white/5 shadow-2xl">
              <img src={movie.poster} alt={movie.title} className="w-full h-auto object-cover" />
            </div>

            {/* Details Content */}
            <div className="flex-1 space-y-6 text-center md:text-left">
              <div className="space-y-2">
                <span className="bg-brand-red text-white text-xs font-black uppercase tracking-wider px-2 py-0.5 rounded">
                  {movie.status === 'NOW_SHOWING' ? 'Now Showing' : 'Upcoming'}
                </span>
                <h1 className="text-3xl md:text-5xl font-black tracking-tight">{movie.title}</h1>
              </div>

              {movie.rating && (
                <div className="flex items-center justify-center md:justify-start space-x-1 text-yellow-400 font-bold">
                  <Star className="h-5 w-5 fill-yellow-400" />
                  <span className="text-lg">{movie.rating.toFixed(1)}/10</span>
                </div>
              )}

              <p className="text-gray-300 text-sm md:text-base max-w-2xl leading-relaxed">{movie.description}</p>

              <div className="flex flex-wrap items-center justify-center md:justify-start gap-6 text-sm text-gray-400 border-t border-b border-white/5 py-4">
                <div className="flex items-center space-x-2">
                  <Clock className="h-4 w-4 text-brand-red" />
                  <span>{movie.duration} Mins</span>
                </div>
                <div className="flex items-center space-x-2">
                  <Globe2 className="h-4 w-4 text-brand-red" />
                  <span>{movie.languages.join(' / ')}</span>
                </div>
                <div className="flex items-center space-x-2">
                  <Film className="h-4 w-4 text-brand-red" />
                  <span>{movie.genres.join(', ')}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Showtime listings section */}
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 mt-12">
        <h2 className="text-2xl font-black uppercase tracking-wider mb-8 flex items-center space-x-2">
          <span>Book Tickets</span>
          <ChevronRight className="h-5 w-5 text-brand-red" />
        </h2>

        {/* Date Selector */}
        <div className="flex items-center space-x-3 overflow-x-auto pb-4 border-b border-white/5">
          <Calendar className="h-5 w-5 text-brand-red mr-2" />
          {dateOptions.map((opt) => (
            <button
              key={opt.value}
              onClick={() => setSelectedDate(opt.value)}
              className={`px-4 py-2 rounded text-xs font-bold whitespace-nowrap transition-all ${
                selectedDate === opt.value
                  ? 'bg-brand-red text-white shadow-lg shadow-brand-red/20'
                  : 'bg-brand-gray text-gray-400 hover:bg-brand-light hover:text-white'
              }`}
            >
              {opt.label}
            </button>
          ))}
        </div>

        {/* Theatres & Shows */}
        <div className="mt-8 space-y-6">
          {!selectedLocation ? (
            <div className="p-8 text-center bg-brand-gray border border-white/5 rounded-xl text-gray-500 font-semibold">
              Please select a city location from the Navigation Bar to see showtimes.
            </div>
          ) : movie.status !== 'NOW_SHOWING' ? (
            <div className="p-8 text-center bg-brand-gray border border-white/5 rounded-xl text-gray-400 font-semibold">
              This movie is upcoming. Show scheduling is not active.
            </div>
          ) : theatres.length === 0 ? (
            <div className="p-8 text-center bg-brand-gray border border-white/5 rounded-xl text-gray-500 font-semibold">
              No active cinemas found in {selectedLocation.city}.
            </div>
          ) : (
            theatres.map((theatre) => {
              const theatreShows = getTheatreShows(theatre.id);
              if (theatreShows.length === 0) return null;

              return (
                <div
                  key={theatre.id}
                  className="bg-brand-gray border border-white/5 rounded-xl p-6 flex flex-col md:flex-row gap-6 md:items-center justify-between"
                >
                  <div className="space-y-1">
                    <h3 className="text-lg font-bold text-white">{theatre.name}</h3>
                    <p className="text-xs text-gray-400">{theatre.address}</p>
                  </div>

                  <div className="flex flex-wrap gap-3">
                    {theatreShows.map((show) => (
                      <button
                        key={show.id}
                        onClick={() => navigate(`/booking/shows/${show.id}/seats`)}
                        className="bg-brand-light border border-white/10 hover:border-brand-red text-brand-red hover:bg-brand-red hover:text-white px-4 py-2 rounded font-bold text-xs transition-all tracking-wide"
                      >
                        {show.startTime.substring(0, 5)}
                      </button>
                    ))}
                  </div>
                </div>
              );
            }).filter(Boolean).length === 0 && (
              <div className="p-8 text-center bg-brand-gray border border-white/5 rounded-xl text-gray-500 font-semibold">
                No active showtimes scheduled for {selectedDate}.
              </div>
            )
          )}
        </div>
      </div>
    </div>
  );
};

export default MovieDetails;
