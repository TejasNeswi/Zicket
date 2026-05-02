import React, { createContext, useContext, useState, useEffect } from 'react';
import { publicAPI } from '../services/api';

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // Load user from localStorage on mount
  useEffect(() => {
    const storedUser = localStorage.getItem('user');
    const storedToken = localStorage.getItem('authToken');
    if (storedUser && storedToken) {
      setUser(JSON.parse(storedUser));
    }
    setLoading(false);
  }, []);

  const signup = async (username, email, password) => {
    try {
      setError(null);
      setLoading(true);
      await publicAPI.signup({ username, email, password });
      // Auto-login after signup
      return await login(username, password);
    } catch (err) {
      const errorMsg = err.response?.data || 'Signup failed';
      setError(errorMsg);
      throw err;
    } finally {
      setLoading(false);
    }
  };

  const login = async (username, password) => {
    try {
      setError(null);
      setLoading(true);
      const response = await publicAPI.login({ username, password });
      const token = response.data;

      // Store token
      localStorage.setItem('authToken', token);

      // Decode JWT to extract user info (basic parsing)
      // In production, store user data separately
      const userData = { username };
      localStorage.setItem('user', JSON.stringify(userData));
      setUser(userData);

      return userData;
    } catch (err) {
      const errorMsg = err.response?.data || 'Login failed';
      setError(errorMsg);
      throw err;
    } finally {
      setLoading(false);
    }
  };

  const logout = () => {
    localStorage.removeItem('authToken');
    localStorage.removeItem('user');
    setUser(null);
    setError(null);
  };

  const isAuthenticated = () => {
    return !!user && !!localStorage.getItem('authToken');
  };

  return (
    <AuthContext.Provider
      value={{
        user,
        loading,
        error,
        signup,
        login,
        logout,
        isAuthenticated,
        setError,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within AuthProvider');
  }
  return context;
};

