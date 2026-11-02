import axios from 'axios';

export const apiClient = axios.create({
  baseURL: '/api/v1',
  headers: {
    'Content-Type': 'application/json',
  },
});

apiClient.interceptors.request.use((config) => {
  const token = localStorage.getItem('goldtrust_jwt');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Auto clear token on unauthorized
      if (!window.location.pathname.includes('/login')) {
        localStorage.removeItem('goldtrust_jwt');
        localStorage.removeItem('goldtrust_user');
      }
    }
    return Promise.reject(error);
  }
);

export default apiClient;

