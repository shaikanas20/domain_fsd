import React, { useState, useEffect } from 'react';
import Navbar from '../components/Navbar';
import API from '../services/api';
import { AlertCircle, Calendar, Armchair, Ticket, XCircle } from 'lucide-react';

const BookingHistory = () => {
  const [bookings, setBookings] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [cancellingId, setCancellingId] = useState(null);

  const fetchHistory = async () => {
    try {
      const response = await API.get('/booking/history');
      // Sort bookings by creation date descending
      const list = response.data.data || [];
      list.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
      setBookings(list);
    } catch (err) {
      console.error(err);
      setError('Failed to fetch booking history.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchHistory();
  }, []);

  const handleCancelBooking = async (bookingId) => {
    if (!window.confirm('Are you sure you want to cancel this booking? This will release your seats.')) {
      return;
    }
    setError('');
    setCancellingId(bookingId);
    try {
      await API.post(`/booking/cancel/${bookingId}`);
      // Refresh list
      fetchHistory();
    } catch (err) {
      setError(err.response?.data?.message || 'Cancellation request failed.');
    } finally {
      setCancellingId(null);
    }
  };

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

      <div className="max-w-4xl mx-auto px-4 sm:px-6 pt-24">
        <div className="mb-6">
          <span className="text-brand-red text-xs font-black uppercase tracking-wider">Transaction Records</span>
          <h1 className="text-3xl font-black text-white">Booking History</h1>
        </div>

        {error && (
          <div className="mb-6 bg-red-950/60 border border-red-500/40 text-red-200 px-4 py-3 rounded text-sm font-semibold flex items-center space-x-2">
            <AlertCircle className="h-5 w-5 text-red-500" />
            <span>{error}</span>
          </div>
        )}

        <div className="space-y-6">
          {bookings.length === 0 ? (
            <div className="bg-brand-gray border border-white/5 rounded-xl p-12 text-center text-gray-500 font-semibold space-y-3">
              <Ticket className="h-12 w-12 mx-auto text-zinc-700 animate-bounce" />
              <p>You haven't made any ticket reservations yet.</p>
            </div>
          ) : (
            bookings.map((booking) => (
              <div
                key={booking.id}
                className="bg-brand-gray border border-white/5 rounded-xl p-6 flex flex-col md:flex-row gap-6 justify-between items-start md:items-center relative overflow-hidden"
              >
                {/* Visual Accent Badge */}
                <div
                  className={`absolute top-0 left-0 bottom-0 w-1.5 ${
                    booking.status === 'CONFIRMED'
                      ? 'bg-green-500'
                      : booking.status === 'INITIATED'
                      ? 'bg-yellow-500'
                      : 'bg-zinc-700'
                  }`}
                ></div>

                <div className="space-y-4 pl-2">
                  <div className="flex items-center space-x-3">
                    <span className="text-xs font-mono text-gray-400 font-bold">#CR-TX-{booking.id}</span>
                    <span
                      className={`text-[10px] font-black uppercase px-2 py-0.5 rounded ${
                        booking.status === 'CONFIRMED'
                          ? 'bg-green-950 text-green-400 border border-green-500/30'
                          : booking.status === 'INITIATED'
                          ? 'bg-yellow-950 text-yellow-400 border border-yellow-500/30'
                          : 'bg-zinc-900 text-zinc-500 border border-white/5'
                      }`}
                    >
                      {booking.status}
                    </span>
                  </div>

                  <div className="grid grid-cols-2 md:grid-cols-3 gap-y-2 gap-x-8">
                    <div className="space-y-0.5">
                      <p className="text-[10px] text-gray-500 uppercase font-black">Show ID</p>
                      <p className="text-sm font-bold text-white">{booking.showId}</p>
                    </div>

                    <div className="space-y-0.5">
                      <p className="text-[10px] text-gray-500 uppercase font-black">Booked Seats</p>
                      <p className="text-sm font-bold text-white uppercase">{booking.seatNumbers.join(', ')}</p>
                    </div>

                    <div className="space-y-0.5">
                      <p className="text-[10px] text-gray-500 uppercase font-black">Total Paid</p>
                      <p className="text-sm font-bold text-white">${booking.totalAmount.toFixed(2)}</p>
                    </div>
                  </div>
                </div>

                {/* Operations side */}
                <div className="w-full md:w-auto flex flex-col items-end space-y-2 border-t md:border-t-0 border-white/5 pt-4 md:pt-0">
                  <span className="text-xs text-gray-400 font-semibold flex items-center space-x-1">
                    <Calendar className="h-3.5 w-3.5 text-brand-red" />
                    <span>{new Date(booking.createdAt).toLocaleString()}</span>
                  </span>
                  
                  {booking.status === 'CONFIRMED' && (
                    <button
                      onClick={() => handleCancelBooking(booking.id)}
                      disabled={cancellingId === booking.id}
                      className="w-full md:w-auto bg-red-950/40 border border-red-500/20 hover:bg-brand-red hover:text-white text-brand-red px-3 py-1.5 rounded text-xs font-bold transition-all flex items-center justify-center space-x-1"
                    >
                      {cancellingId === booking.id ? (
                        <div className="animate-spin rounded-full h-3 w-3 border-t-2 border-b-2 border-brand-red"></div>
                      ) : (
                        <>
                          <XCircle className="h-3.5 w-3.5" />
                          <span>Cancel Ticket</span>
                        </>
                      )}
                    </button>
                  )}
                </div>
              </div>
            ))
          )}
        </div>
      </div>
    </div>
  );
};

export default BookingHistory;
