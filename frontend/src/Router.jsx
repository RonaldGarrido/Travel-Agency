import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import ProtectedRoute from './components/ProtectedRoute';
import Layout from './components/Layout';

// Páginas públicas
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import PackagesPage from './pages/PackagesPage';
import PackageDetailPage from './pages/PackageDetailPage';

// Páginas autenticadas
import MyReservationsPage from './pages/MyReservationsPage';
import ReservationPage from './pages/ReservationPage';
import PaymentPage from './pages/PaymentPage';

// Páginas de admin
import AdminDashboard from './pages/AdminDashboard';
import AdminPackagesPage from './pages/admin/AdminPackagesPage';
import AdminUsersPage from './pages/admin/AdminUsersPage';
import AdminReportsPage from './pages/admin/AdminReportsPage';

export default function Router() {
  return (
    <BrowserRouter>
	<Layout>
      <Routes>
        {/* Públicas */}
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
        <Route path="/packages" element={<PackagesPage />} />
        <Route path="/packages/:id" element={<PackageDetailPage />} />

        {/* Redirige /catalog → /packages para compatibilidad */}
        <Route path="/catalog" element={<Navigate to="/packages" />} />

        {/* Autenticadas */}
        <Route
          path="/my-reservations"
          element={
            <ProtectedRoute>
              <MyReservationsPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/reservations/new/:packageId"
          element={
            <ProtectedRoute>
              <ReservationPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/payments/:reservationId"
          element={
            <ProtectedRoute>
              <PaymentPage />
            </ProtectedRoute>
          }
        />

        {/* Admin */}
        <Route
          path="/admin"
          element={
            <ProtectedRoute requiredRole="ADMIN">
              <AdminDashboard />
            </ProtectedRoute>
          }
        />
        <Route
          path="/admin/packages"
          element={
            <ProtectedRoute requiredRole="ADMIN">
              <AdminPackagesPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/admin/users"
          element={
            <ProtectedRoute requiredRole="ADMIN">
              <AdminUsersPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/admin/reports"
          element={
            <ProtectedRoute requiredRole="ADMIN">
              <AdminReportsPage />
            </ProtectedRoute>
          }
        />

        {/* Raíz */}
        <Route path="/" element={<Navigate to="/packages" />} />

        {/* Ruta no encontrada */}
        <Route path="*" element={<Navigate to="/packages" />} />
      </Routes>
	</Layout>
    </BrowserRouter>
  );
}
