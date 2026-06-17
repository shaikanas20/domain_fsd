import React from 'react';
import { Link } from 'react-router-dom';
import { Star } from 'lucide-react';

const MovieCard = ({ movie }) => {
  return (
    <Link
      to={`/movies/${movie.id}`}
      className="group flex flex-col bg-brand-gray border border-white/5 rounded-lg overflow-hidden transition-all hover:scale-105 hover:shadow-2xl hover:border-brand-red duration-300"
    >
      <div className="relative aspect-[2/3] w-full overflow-hidden bg-brand-light">
        <img
          src={movie.poster}
          alt={movie.title}
          className="h-full w-full object-cover group-hover:scale-110 transition-transform duration-500"
          onError={(e) => {
            e.target.src = 'https://images.unsplash.com/photo-1440404653325-ab127d49abc1?q=80&w=600';
          }}
        />
        {movie.rating && (
          <div className="absolute top-2 right-2 bg-black/80 px-2 py-1 rounded flex items-center space-x-1 text-xs font-bold text-yellow-400">
            <Star className="h-3 w-3 fill-yellow-400" />
            <span>{movie.rating.toFixed(1)}</span>
          </div>
        )}
        <div className="absolute bottom-2 left-2 bg-brand-red px-2 py-0.5 rounded text-[10px] font-bold uppercase tracking-wider">
          {movie.status === 'NOW_SHOWING' ? 'Now Showing' : 'Upcoming'}
        </div>
      </div>

      <div className="p-4 flex-1 flex flex-col justify-between">
        <div>
          <h3 className="font-bold text-white group-hover:text-brand-red transition-colors line-clamp-1">
            {movie.title}
          </h3>
          <p className="text-xs text-gray-400 mt-1 line-clamp-2">{movie.description}</p>
        </div>
        <div className="mt-3 border-t border-white/5 pt-2 flex items-center justify-between text-[11px] text-gray-500 font-semibold">
          <span>{movie.genres.join(', ')}</span>
          <span>{movie.languages.join(' / ')}</span>
        </div>
      </div>
    </Link>
  );
};

export default MovieCard;
