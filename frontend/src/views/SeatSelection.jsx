import React, { useState, useEffect, useContext } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import Navbar from '../components/Navbar';
import SeatGrid from '../components/SeatGrid';
import API from '../services/api';
import { AuthContext } from '../context/AuthContext';
import { AlertCircle, Film, CreditCard, ChevronRight, Check } from 'lucide-react';

const SeatSelection = () => {
  const { showId } = useParams();
  const { user } = useContext(AuthContext);
  const [show, setShow] = useState(null);
  const [seats, setSeats] = useState([]);
  const [selectedSeats, setSelectedSeats] = useState([]);
  const [bookingInfo, setBookingInfo] = useState(null);
  const [step, setStep] = useState(1); // 1: Select seats, 2: Payment/Confirmation
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const loadSeatMap = async () => {
    setLoading(true);
    try {
      const showRes = await API.get(`/booking/shows/${showId}`);
      setShow(showRes.data.data);

      const seatsRes = await API.get(`/booking/shows/${showId}/seats`);
      setSeats(seatsRes.data.data || []);
    } catch (err) {
      console.error(err);
      setError('Failed to load show seat configurations.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadSeatMap();
  }, [showId]);

  // Handle seat clicks
  const handleSeatClick = (seatNumber) => {
    if (selectedSeats.includes(seatNumber)) {
      setSelectedSeats(selectedSeats.filter((s) => s !== seatNumber));
    } else {
      setSelectedSeats([...selectedSeats, seatNumber]);
    }
  };

  // Calculate current estimated total price
  const calculateTotal = () => {
    if (!show) return 0;
    return selectedSeats.reduce((acc, seatNum) => {
      const seat = seats.find((s) => s.seatNumber === seatNum);
      const price = show.priceMap[seat?.seatType] || 100;
      return acc + price;
    }, 0);
  };

  // Step 1: Lock seats and initiate booking
  const handleInitiateBooking = async () => {
    if (selectedSeats.length === 0) return;
    setError('');
    setSubmitting(true);
    try {
      const response = await API.post('/booking/initiate', {
        showId: parseInt(showId, 10),
        seatNumbers: selectedSeats,
      });
      setBookingInfo(response.data.data);
      setStep(2);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to lock seats. They may have been booked by someone else.');
      // Refresh map
      loadSeatMap();
    } finally {
      setSubmitting(false);
    }
  };

  // Step 2: Pay and confirm booking
  const handleConfirmBooking = async () => {
    if (!bookingInfo) return;
    setError('');
    setSubmitting(true);
    try {
      await API.post(`/booking/confirm/${bookingInfo.id}`);
      navigate(`/booking/confirmation/${bookingInfo.id}`);
    } catch (err) {
      setError(err.response?.data?.message || 'Payment confirmation failed. Try booking again.');
      setStep(1);
      setSelectedSeats([]);
      loadSeatMap();
    } finally {
      setSubmitting(false);
    }
  };

  // Helper lists to identify row boundaries
  const rowNamesSet = new Set(seats.map((s) => s.seatNumber.match(/^([A-Z]+)/i)?.[1] || s.seatNumber.charAt(0)));
  const rowNames = Array.from(rowNamesSet).sort();
  const colCount = Math.max(...seats.map((s) => parseInt(s.seatNumber.match(/\d+$/)?.[0] || 0, 10)), 0);

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

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 pt-24">
        {show && (
          <div className="mb-6 flex flex-col md:flex-row items-center justify-between border-b border-white/5 pb-4">
            <div>
              <span className="text-brand-red text-xs font-black uppercase tracking-wider">Seat Selection</span>
              <h1 className="text-2xl font-black text-white">Select Your Seats</h1>
              <p className="text-xs text-gray-400 mt-1 font-semibold">
                Show Date: {show.showDate} | Time: {show.startTime.substring(0, 5)}
              </p>
            </div>
            {/* Breadcrumb Steps */}
            <div className="flex items-center space-x-2 text-xs font-bold uppercase mt-4 md:mt-0">
              <span className={step === 1 ? 'text-brand-red' : 'text-gray-500'}>1. Select Seats</span>
              <ChevronRight className="h-4 w-4 text-gray-500" />
              <span className={step === 2 ? 'text-brand-red' : 'text-gray-500'}>2. Confirmation</span>
            </div>
          </div>
        )}

        {error && (
          <div className="mb-6 bg-red-950/60 border border-red-500/40 text-red-200 px-4 py-3 rounded text-sm font-semibold flex items-center space-x-2">
            <AlertCircle className="h-5 w-5 text-red-500" />
            <span>{error}</span>
          </div>
        )}

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          {/* Main Layout Area */}
          <div className="lg:col-span-2">
            {step === 1 ? (
              <SeatGrid
                seats={seats}
                rowNames={rowNames}
                colCount={colCount}
                selectedSeats={selectedSeats}
                onSeatClick={handleSeatClick}
              />
            ) : (
              <div className="bg-brand-gray border border-white/5 rounded-xl p-8 space-y-6">
                <h3 className="text-xl font-bold flex items-center space-x-2">
                  <CreditCard className="h-5 w-5 text-brand-red" />
                  <span>Dummy Payment Gateway</span>
                </h3>
                <div className="p-4 bg-zinc-900 border border-white/5 rounded-lg text-sm text-gray-400 space-y-2">
                  <p>In a real deployment, we would route to Stripe or Razorpay.</p>
                  <p>Click <strong className="text-white">Pay and Confirm</strong> to finalize your booking.</p>
                </div>
                <div className="flex items-center justify-between border-t border-white/5 pt-4">
                  <span className="text-gray-400 text-sm">Total Tickets: {bookingInfo?.seatNumbers.length}</span>
                  <span className="text-xl font-black text-brand-red">${bookingInfo?.totalAmount.toFixed(2)}</span>
                </div>
              </div>
            )}
          </div>

          {/* Sidebar Area */}
          <div>
            <div className="bg-brand-gray border border-white/5 rounded-xl p-6 space-y-6">
              <h3 className="text-lg font-bold border-b border-white/5 pb-3 flex items-center space-x-2">
                <Film className="h-5 w-5 text-brand-red" />
                <span>Ticket Summary</span>
              </h3>

              <div className="space-y-4">
                <div className="flex items-center justify-between text-sm">
                  <span className="text-gray-400">Movie ID:</span>
                  <span className="font-semibold text-white">{show?.movieId}</span>
                </div>
                <div className="flex items-center justify-between text-sm">
                  <span className="text-gray-400">Screen ID:</span>
                  <span className="font-semibold text-white">{show?.screenId}</span>
                </div>
                <div className="flex items-center justify-between text-sm">
                  <span className="text-gray-400">Seats selected:</span>
                  <span className="font-bold text-white uppercase">{selectedSeats.join(', ') || 'None'}</span>
                </div>
                <div className="flex items-center justify-between border-t border-white/5 pt-3">
                  <span className="text-sm text-gray-400">Subtotal:</span>
                  <span className="text-lg font-black text-white">${calculateTotal().toFixed(2)}</span>
                </div>
              </div>

              {step === 1 ? (
                <button
                  onClick={handleInitiateBooking}
                  disabled={selectedSeats.length === 0 || submitting}
                  className="w-full bg-brand-red hover:bg-red-700 disabled:bg-red-900/40 text-white font-bold py-3 rounded transition-all hover:scale-105 shadow-lg shadow-brand-red/20 text-xs uppercase tracking-wider flex items-center justify-center"
                >
                  {submitting ? (
                    <div className="animate-spin rounded-full h-4 w-4 border-t-2 border-b-2 border-white"></div>
                  ) : (
                    <span>Proceed to Checkout</span>
                  )}
                </button>
              ) : (
                <div className="space-y-3">
                  <button
                    onClick={handleConfirmBooking}
                    disabled={submitting}
                    className="w-full bg-green-600 hover:bg-green-700 disabled:bg-green-950 text-white font-bold py-3 rounded transition-all hover:scale-105 shadow-lg shadow-green-600/20 text-xs uppercase tracking-wider flex items-center justify-center"
                  >
                    {submitting ? (
                      <div className="animate-spin rounded-full h-4 w-4 border-t-2 border-b-2 border-white"></div>
                    ) : (
                      <span>Pay and Confirm</span>
                    )}
                  </button>
                  <button
                    onClick={() => {
                      setStep(1);
                      setError('');
                    }}
                    className="w-full bg-brand-light text-gray-300 font-bold py-2 rounded text-xs transition-colors hover:bg-zinc-800"
                  >
                    Cancel
                  </button>
                </div>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default SeatSelection;
