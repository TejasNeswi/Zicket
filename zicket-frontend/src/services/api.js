import axios from 'axios';

// Backend API base URL
const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080';

// Create axios instance with default config
const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Add JWT token to all requests if available
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('authToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
      console.log('[API] Adding auth token to request:', config.url);
    } else {
      console.warn('[API] No auth token found in localStorage for URL:', config.url);
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Handle response errors
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    console.error('[API] Response error:', {
      url: error.config?.url,
      status: error.response?.status,
      message: error.message,
      data: error.response?.data
    });
    
    if (error.response?.status === 401) {
      // Token expired or invalid
      console.warn('[API] Received 401 - clearing auth and redirecting to login');
      localStorage.removeItem('authToken');
      localStorage.removeItem('user');
      window.location.href = '/login';
    }
    
    if (error.response?.status === 403) {
      // Forbidden - may indicate authentication issue
      console.error('[API] Received 403 Forbidden - check token validity');
    }
    
    return Promise.reject(error);
  }
);

// Public endpoints
export const publicAPI = {
  signup: (userData) => apiClient.post('/public/signup', userData),
  login: (credentials) => apiClient.post('/public/login', credentials),
  healthCheck: () => apiClient.get('/public/health-check'),
  getAllTickets: () => apiClient.get('/public'),
  getConcertTickets: () => apiClient.get('/public/get-concert-tickets'),
  getSportsTickets: () => apiClient.get('/public/get-sports-tickets'),
  getComedyTickets: () => apiClient.get('/public/get-comedy-tickets'),
  getTheaterTickets: () => apiClient.get('/public/get-theater-tickets'),
};

// User endpoints
export const userAPI = {
  updateProfile: (userData) => apiClient.put('/user', userData),
  deleteAccount: () => apiClient.delete('/user'),
};

// Ticket endpoints
export const ticketAPI = {
  getMyTickets: () => apiClient.get('/ticket/get-my-tickets'),
  getPurchasedTickets: () => apiClient.get('/ticket/get-purchased-tickets'),
  getTicketInfo: (ticketId) => apiClient.get(`/ticket/get-event-info/${ticketId}`),
  createTicket: (ticketData) => apiClient.post('/ticket', ticketData),
  updateTicket: (ticketId, ticketData) => apiClient.put(`/ticket/id/${ticketId}`, ticketData),
  deleteTicket: (ticketId) => apiClient.delete(`/ticket/id/${ticketId}`),
  uploadTicketFile: (ticketId, file) => {
    const formData = new FormData();
    formData.append('file', file);
    return apiClient.post(`/ticket/upload-ticket/${ticketId}`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
  },
  updateTicketFile: (ticketId, file) => {
    const formData = new FormData();
    formData.append('file', file);
    return apiClient.put(`/ticket/update-ticketfile/${ticketId}`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
  },
  viewTicketFile: (ticketId) => apiClient.get(`/ticket/view-ticket/${ticketId}`, {
    responseType: 'blob',
  }),
};

// Payment endpoints
export const paymentAPI = {
  processPayment: (ticketId, paymentData) =>
    apiClient.post(`/payments/${ticketId}`, paymentData),
  getPaymentInfo: (paymentId) =>
    apiClient.get(`/payments/get-payment-info/${paymentId}`),
};

// Admin endpoints
export const adminAPI = {
  getAllUsers: () => apiClient.get('/admin/get-all-users'),
  getAllTickets: () => apiClient.get('/admin/get-all-tickets'),
  getAllPayments: () => apiClient.get('/admin/get-all-payments'),
  addUser: (userData) => apiClient.post('/admin/add-user', userData),
};

export default apiClient;

