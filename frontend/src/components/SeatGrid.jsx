import React from 'react';
import Seat from './Seat';

const SeatGrid = ({ seats, rowNames, colCount, selectedSeats, onSeatClick }) => {
  // Group seats by row
  const seatsByRow = {};
  rowNames.forEach((rowName) => {
    seatsByRow[rowName] = [];
  });

  seats.forEach((seat) => {
    // Determine row name from seatNumber, e.g. "A12" -> "A", or "A-12" -> "A"
    const rowMatch = seat.seatNumber.match(/^([A-Z]+)-?(\d+)$/i);
    const rowName = rowMatch ? rowMatch[1] : seat.seatNumber.charAt(0);
    
    if (seatsByRow[rowName]) {
      seatsByRow[rowName].push(seat);
    }
  });

  // Sort seats in each row by their column number extracted from seat number
  Object.keys(seatsByRow).forEach((rowName) => {
    seatsByRow[rowName].sort((a, b) => {
      const matchA = a.seatNumber.match(/\d+$/);
      const matchB = b.seatNumber.match(/\d+$/);
      const colA = matchA ? parseInt(matchA[0], 10) : 0;
      const colB = matchB ? parseInt(matchB[0], 10) : 0;
      return colA - colB;
    });
  });

  return (
    <div className="flex flex-col items-center overflow-x-auto p-6 bg-zinc-950 rounded-xl border border-white/5">
      {/* Screen Direction indicator */}
      <div className="w-full max-w-lg mb-12 flex flex-col items-center">
        <div className="w-full h-1 bg-gradient-to-r from-transparent via-cyan-500 to-transparent shadow-[0_0_15px_#06b6d4] rounded"></div>
        <p className="text-[10px] text-gray-500 font-bold uppercase tracking-widest mt-2">All eyes this way (Screen)</p>
      </div>

      {/* Grid rows */}
      <div className="space-y-2">
        {rowNames.map((rowName) => (
          <div key={rowName} className="flex items-center space-x-2">
            {/* Row Letter */}
            <span className="w-6 text-sm font-bold text-gray-500 text-center uppercase mr-2">{rowName}</span>

            {/* Row Seats */}
            <div className="flex items-center">
              {seatsByRow[rowName]?.map((seat) => (
                <Seat
                  key={seat.id}
                  seatNumber={seat.seatNumber}
                  seatType={seat.seatType}
                  status={seat.status}
                  isSelected={selectedSeats.includes(seat.seatNumber)}
                  onClick={() => onSeatClick(seat.seatNumber)}
                />
              ))}
            </div>

            {/* Row Letter (Right Side) */}
            <span className="w-6 text-sm font-bold text-gray-500 text-center uppercase ml-2">{rowName}</span>
          </div>
        ))}
      </div>

      {/* Legend */}
      <div className="flex items-center space-x-8 mt-12 text-xs font-semibold text-gray-400 border-t border-white/5 pt-6 w-full justify-center">
        <div className="flex items-center space-x-2">
          <div className="h-4 w-4 bg-brand-light border border-white/10 rounded"></div>
          <span>Available</span>
        </div>
        <div className="flex items-center space-x-2">
          <div className="h-4 w-4 bg-brand-red border border-white rounded shadow-[0_0_5px_#E50914]"></div>
          <span>Selected</span>
        </div>
        <div className="flex items-center space-x-2">
          <div className="h-4 w-4 bg-yellow-950/80 border border-yellow-800/30 rounded"></div>
          <span>Locked</span>
        </div>
        <div className="flex items-center space-x-2">
          <div className="h-4 w-4 bg-red-950/60 border border-red-900/30 rounded"></div>
          <span>Booked</span>
        </div>
        <div className="flex items-center space-x-2">
          <div className="h-4 w-4 border-2 border-yellow-600/80 rounded bg-brand-light"></div>
          <span>VIP Tier</span>
        </div>
      </div>
    </div>
  );
};

export default SeatGrid;
