import React, { useState, useEffect } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import Navbar from '../components/Navbar';
import API from '../services/api';
import { CheckCircle, Calendar, Film, Armchair, Ticket } from 'lucide-react';

const BookingConfirmation = () => {
  const { bookingId } = useParams();
  const [booking, setBooking] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    const fetchBookingDetails = async () => {
      setLoading(true);
      try {
        const response = await API.get('/booking/history');
        const matches = response.data.data || [];
        const match = matches.find((b) => b.id.toString() === bookingId.toString());
        if (match) {
          setBooking(match);
        } else {
          setError('Booking record not found.');
        }
      } catch (err) {
        console.error(err);
        setError('Failed to load transaction confirmation.');
      } finally {
        setLoading(false);
      }
    };
    fetchBookingDetails();
  }, [bookingId]);

  if (loading) {
    return (
      <div className="min-h-screen bg-brand-dark flex items-center justify-center">
        <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-brand-red"></div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-brand-dark text-white pb-24">
      <Navbar />

      <div className="max-w-2xl mx-auto px-4 sm:px-6 pt-28">
        {error ? (
          <div className="bg-brand-gray border border-white/5 rounded-xl p-8 text-center space-y-4">
            <p className="text-gray-400 font-semibold">{error}</p>
            <Link to="/" className="inline-block bg-brand-red px-6 py-2 rounded font-bold text-xs uppercase">
              Go to Dashboard
            </Link>
          </div>
        ) : (
          <div className="space-y-8">
            {/* Header Success Card */}
            <div className="bg-brand-gray border border-white/5 rounded-xl p-8 flex flex-col items-center text-center space-y-4">
              <CheckCircle className="h-16 w-16 text-green-500 animate-bounce" />
              <h1 className="text-3xl font-black">Booking Confirmed!</h1>
              <p className="text-sm text-gray-400 font-semibold">
                Your ticket reservation was processed successfully. SMS & Email alerts have been sent.
              </p>
            </div>

            {/* Ticket Card */}
            <div className="bg-zinc-900 border border-white/5 rounded-xl overflow-hidden shadow-2xl relative">
              {/* Ticket Jagged Border elements (Visual Polish) */}
              <div className="absolute left-0 right-0 top-1/2 -translate-y-1/2 flex justify-between px-0.5 z-20">
                <div className="h-6 w-3 bg-brand-dark rounded-r-full -ml-3"></div>
                <div className="h-6 w-3 bg-brand-dark rounded-l-full -mr-3"></div>
              </div>

              {/* Top half */}
              <div className="p-6 border-b border-dashed border-white/10 space-y-4">
                <div className="flex items-center space-x-3">
                  <Ticket className="h-6 w-6 text-brand-red" />
                  <span className="text-xs font-black uppercase tracking-wider text-gray-400">Cinematic Entry Ticket</span>
                </div>
                <div className="space-y-1">
                  <p className="text-xs text-gray-500 font-bold uppercase">Transaction ID</p>
                  <p className="font-mono text-sm text-white font-bold">#CR-TX-{booking?.id}</p>
                </div>
              </div>

              {/* Bottom half */}
              <div className="p-6 grid grid-cols-2 gap-y-6 gap-x-4 bg-zinc-950/40">
                <div className="space-y-1">
                  <span className="flex items-center space-x-1 text-xs text-gray-500 font-bold uppercase">
                    <Film className="h-3 w-3 text-brand-red" />
                    <span>Show ID</span>
                  </span>
                  <p className="text-sm font-bold text-white">{booking?.showId}</p>
                </div>

                <div className="space-y-1">
                  <span className="flex items-center space-x-1 text-xs text-gray-500 font-bold uppercase">
                    <Calendar className="h-3 w-3 text-brand-red" />
                    <span>Booked On</span>
                  </span>
                  <p className="text-sm font-bold text-white">{new Date(booking?.createdAt).toLocaleString()}</p>
                </div>

                <div className="space-y-1">
                  <span className="flex items-center space-x-1 text-xs text-gray-500 font-bold uppercase">
                    <Armchair className="h-3 w-3 text-brand-red" />
                    <span>Seat Numbers</span>
                  </span>
                  <p className="text-sm font-black text-brand-red uppercase">{booking?.seatNumbers.join(', ')}</p>
                </div>

                <div className="space-y-1">
                  <span className="text-xs text-gray-500 font-bold uppercase">Amount Charged</span>
                  <p className="text-lg font-black text-white">${booking?.totalAmount.toFixed(2)}</p>
                </div>
              </div>
            </div>

            {/* Action Buttons */}
            <div className="flex space-x-4">
              <Link
                to="/history"
                className="flex-1 bg-brand-light hover:bg-zinc-800 text-white font-bold py-3 rounded text-center text-xs uppercase tracking-wider transition-colors"
              >
                View Booking History
              </Link>
              <Link
                to="/"
                className="flex-1 bg-brand-red hover:bg-red-700 text-white font-bold py-3 rounded text-center text-xs uppercase tracking-wider transition-colors"
              >
                Back to Home
              </Link>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default BookingConfirmation;
