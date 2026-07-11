import axios from 'axios';

const API_BASE_URL = import.meta.env.VITE_BACKEND_URL || 'http://localhost:8080/api';

const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Add token to requests
apiClient.interceptors.request.use((config) => {
  const token = localStorage.getItem('authToken');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export const walletAPI = {
  getWallets: () => apiClient.get('/wallets'),
  getWallet: (id: number) => apiClient.get(`/wallets/${id}`),
  getBalance: (id: number) => apiClient.get(`/wallets/${id}/balance`),
};

export const tradeAPI = {
  buyUsdc: (data: any) => apiClient.post('/transactions/buy', data),
  sellUsdc: (data: any) => apiClient.post('/transactions/sell', data),
  getTransactions: (page = 0, size = 20) =>
    apiClient.get(`/transactions?page=${page}&size=${size}`),
  getTransaction: (id: number) => apiClient.get(`/transactions/${id}`),
  getOrders: (page = 0, size = 20) => apiClient.get(`/transactions/orders?page=${page}&size=${size}`),
  getOrder: (id: number) => apiClient.get(`/transactions/orders/${id}`),
  cancelOrder: (id: number) => apiClient.delete(`/transactions/orders/${id}`),
};

export const authAPI = {
  register: (data: any) => apiClient.post('/auth/register', data),
  login: (data: any) => apiClient.post('/auth/login', data),
  logout: () => apiClient.post('/auth/logout'),
};

export default apiClient;
