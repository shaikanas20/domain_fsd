import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import { LocationProvider } from './context/LocationContext';
import ProtectedRoute from './components/ProtectedRoute';

// Views
import Login from './views/Login';
import Signup from './views/Signup';
import Dashboard from './views/Dashboard';
import MovieDetails from './views/MovieDetails';
import SeatSelection from './views/SeatSelection';
import BookingConfirmation from './views/BookingConfirmation';
import BookingHistory from './views/BookingHistory';
import UserProfile from './views/UserProfile';
import OwnerDashboard from './views/owner/OwnerDashboard';
import AdminDashboard from './views/admin/AdminDashboard';

function App() {
  return (
    <AuthProvider>
      <LocationProvider>
        <Router>
          <Routes>
            {/* Public Routes */}
            <Route path="/login" element={<Login />} />
            <Route path="/signup" element={<Signup />} />
            <Route path="/" element={<Dashboard />} />
            <Route path="/movies/:id" element={<MovieDetails />} />

            {/* Protected Customer Routes */}
            <Route
              path="/booking/shows/:showId/seats"
              element={
                <ProtectedRoute allowedRoles={['USER', 'THEATRE_OWNER', 'ADMIN']}>
                  <SeatSelection />
                </ProtectedRoute>
              }
            />
            <Route
              path="/booking/confirmation/:bookingId"
              element={
                <ProtectedRoute allowedRoles={['USER', 'THEATRE_OWNER', 'ADMIN']}>
                  <BookingConfirmation />
                </ProtectedRoute>
              }
            />
            <Route
              path="/history"
              element={
                <ProtectedRoute allowedRoles={['USER', 'THEATRE_OWNER', 'ADMIN']}>
                  <BookingHistory />
                </ProtectedRoute>
              }
            />
            <Route
              path="/profile"
              element={
                <ProtectedRoute allowedRoles={['USER', 'THEATRE_OWNER', 'ADMIN']}>
                  <UserProfile />
                </ProtectedRoute>
              }
            />

            {/* Protected Theatre Owner Panel */}
            <Route
              path="/owner"
              element={
                <ProtectedRoute allowedRoles={['THEATRE_OWNER']}>
                  <OwnerDashboard />
                </ProtectedRoute>
              }
            />

            {/* Protected Site Admin Panel */}
            <Route
              path="/admin"
              element={
                <ProtectedRoute allowedRoles={['ADMIN']}>
                  <AdminDashboard />
                </ProtectedRoute>
              }
            />

            {/* Fallback redirection */}
            <Route path="*" element={<Navigate to="/" replace />} />
          </Routes>
        </Router>
      </LocationProvider>
    </AuthProvider>
  );
}

export default App;
