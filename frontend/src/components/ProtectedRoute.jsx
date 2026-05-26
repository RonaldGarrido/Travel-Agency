import React, { useContext } from 'react';
import { Navigate } from 'react-router-dom';
import { AuthContext } from '../context/AuthContext';

export default function ProtectedRoute({ children, requiredRole = null }) {
  const { user, loading } = useContext(AuthContext);

  if (loading) {
    return <div>Cargando...</div>;
  }

  if (!user) {
    return <Navigate to="/login" />;
  }

  // user.roles es un array — se usa .includes() para verificar el rol
  if (requiredRole && !user.roles?.includes(requiredRole)) {
    return <Navigate to="/catalog" />;
  }

  return children;
}
