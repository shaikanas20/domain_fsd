import React from 'react';

const Seat = ({ seatNumber, seatType, status, isSelected, onClick, currentUserId }) => {
  let bgClass = 'bg-brand-light text-gray-400 border-white/10 hover:bg-zinc-700';
  let cursorClass = 'cursor-pointer';
  let disabled = false;

  if (status === 'BOOKED') {
    bgClass = 'bg-red-950/60 text-red-600 border-red-900/30 cursor-not-allowed';
    disabled = true;
  } else if (status === 'LOCKED') {
    bgClass = 'bg-yellow-950/80 text-yellow-500 border-yellow-800/30 cursor-not-allowed';
    disabled = true;
  }

  if (isSelected) {
    bgClass = 'bg-brand-red text-white border-white scale-105 shadow-[0_0_10px_#E50914]';
  }

  // Set distinct border color or badge for seat types
  let typeBorder = 'border';
  if (seatType === 'VIP') {
    typeBorder = 'border-2 border-yellow-600/80';
  } else if (seatType === 'PREMIUM') {
    typeBorder = 'border-2 border-purple-600/80';
  }

  return (
    <button
      onClick={onClick}
      disabled={disabled}
      title={`${seatNumber} (${seatType})`}
      className={`h-9 w-9 m-0.5 rounded text-[10px] font-black tracking-tighter flex items-center justify-center transition-all ${bgClass} ${typeBorder} ${cursorClass}`}
    >
      {seatNumber}
    </button>
  );
};

export default Seat;
