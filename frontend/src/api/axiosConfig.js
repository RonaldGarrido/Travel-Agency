import axios from 'axios';

// Instancia dedicada — evita contaminar el axios global
const api = axios.create({
  baseURL: `/api`,
  headers: {
    'Content-Type': 'application/json'
  }
});

let authContext = null;

export const setAuthContext = (context) => {
  authContext = context;
};

// Interceptor de solicitud: adjunta el token JWT si existe
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('access_token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Interceptor de respuesta: refresca el token en 401 y redirige si falla
api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    if (error.response?.status === 401 && !originalRequest._retry && authContext) {
      originalRequest._retry = true;

      try {
        const success = await authContext.refreshAccessToken();
        if (success) {
          const newToken = localStorage.getItem('access_token');
          originalRequest.headers.Authorization = `Bearer ${newToken}`;
          return api(originalRequest);
        }
      } catch (refreshError) {
        console.error('Token refresh failed:', refreshError);
        if (authContext) authContext.logout();
        window.location.href = '/login';
        return Promise.reject(refreshError);
      }
    }

    if (error.response?.status === 401) {
      if (authContext) authContext.logout();
      window.location.href = '/login';
    }

    return Promise.reject(error);
  }
);

export default api;
