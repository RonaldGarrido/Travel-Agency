import React, { useEffect, useContext } from 'react';
import { AuthContext, AuthProvider } from './context/AuthContext';
import { setAuthContext } from './api/axiosConfig';
import Router from './Router';

const AppContent = () => {
  const authContext = useContext(AuthContext);

  useEffect(() => {
    setAuthContext(authContext);
  }, [authContext]);

  if (authContext.loading) {
    return <div>Cargando...</div>;
  }

  return <Router />;
};

function App() {
  return (
    <AuthProvider>
      <AppContent />
    </AuthProvider>
  );
}

export default App;
