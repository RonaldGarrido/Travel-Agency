import React, { createContext, useState, useCallback, useEffect, useContext } from 'react';
import axios from 'axios';

export const AuthContext = createContext();

// Hook de conveniencia — usado en todos los componentes
export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

const decodeToken = (token) => {
  try {
    const payload = token.split('.')[1];
    const decoded = JSON.parse(atob(payload));
    return decoded;
  } catch (e) {
    return null;
  }
};

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [token, setToken] = useState(localStorage.getItem('access_token') || null);
  const [refreshTokenValue, setRefreshTokenValue] = useState(
    localStorage.getItem('refresh_token') || null
  );

  const logout = useCallback(() => {
    localStorage.removeItem('access_token');
    localStorage.removeItem('refresh_token');
    localStorage.removeItem('token_expires_at');
    setToken(null);
    setRefreshTokenValue(null);
    setUser(null);
  }, []);

  // Restaurar sesión al montar
  useEffect(() => {
    if (token) {
      const decoded = decodeToken(token);
      if (decoded && decoded.exp * 1000 > Date.now()) {
        setUser({
          id: decoded.sub,
          email: decoded.email,
          fullName: decoded.name || decoded.preferred_username,
          // roles es siempre un array — usado en toda la app con .includes()
          roles: decoded.realm_access?.roles || []
        });
      } else {
        logout();
      }
    }
    setLoading(false);
  }, []);

  const login = useCallback(async (username, password) => {
    try {
      const response = await axios.post('/api/auth/login', null, {
        params: { username, password }
      });

      const { access_token, refresh_token, expires_in } = response.data;

      localStorage.setItem('access_token', access_token);
      localStorage.setItem('refresh_token', refresh_token);
      localStorage.setItem('token_expires_at', Date.now() + expires_in * 1000);

      setToken(access_token);
      setRefreshTokenValue(refresh_token);

      const decoded = decodeToken(access_token);
      setUser({
        id: decoded.sub,
        email: decoded.email,
        fullName: decoded.name || decoded.preferred_username,
        roles: decoded.realm_access?.roles || []
      });

      return true;
    } catch (error) {
      console.error('Login failed:', error);
      return false;
    }
  }, []);

  const refreshAccessToken = useCallback(async () => {
    if (!refreshTokenValue) return false;

    try {
      const response = await axios.post('/api/auth/refresh', null, {
        params: { refreshToken: refreshTokenValue }
      });

      const { access_token, expires_in } = response.data;

      localStorage.setItem('access_token', access_token);
      localStorage.setItem('token_expires_at', Date.now() + expires_in * 1000);

      setToken(access_token);

      const decoded = decodeToken(access_token);
      setUser({
        id: decoded.sub,
        email: decoded.email,
        fullName: decoded.name || decoded.preferred_username,
        roles: decoded.realm_access?.roles || []
      });

      return true;
    } catch (error) {
      console.error('Token refresh failed:', error);
      logout();
      return false;
    }
  }, [refreshTokenValue, logout]);

  // Comprueba si el usuario tiene un rol determinado (usa el array roles)
  const hasRole = useCallback((role) => {
    return user?.roles?.includes(role) || false;
  }, [user]);

  return (
    <AuthContext.Provider
      value={{
        user,
        token,
        loading,
        login,
        logout,
        refreshAccessToken,
        hasRole,
        isAuthenticated: !!user
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};
