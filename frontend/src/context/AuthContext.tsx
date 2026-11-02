import React, { createContext, useContext, useState, useEffect } from 'react';
import { User, UserRole } from '../types';
import apiClient from '../api/client';

interface AuthContextType {
  user: User | null;
  token: string | null;
  role: UserRole | null;
  isLoading: boolean;
  login: (usernameOrEmail: string, password: string) => Promise<void>;
  register: (payload: any) => Promise<void>;
  logout: () => void;
  quickSwitchUser: (username: string) => Promise<void>;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<User | null>(null);
  const [token, setToken] = useState<string | null>(localStorage.getItem('goldtrust_jwt'));
  const [isLoading, setIsLoading] = useState<boolean>(true);

  useEffect(() => {
    const savedUser = localStorage.getItem('goldtrust_user');
    if (token && savedUser) {
      try {
        setUser(JSON.parse(savedUser));
      } catch (e) {
        localStorage.removeItem('goldtrust_user');
      }
    }
    setIsLoading(false);
  }, [token]);

  const login = async (usernameOrEmail: string, password: string) => {
    const resp = await apiClient.post('/auth/login', { usernameOrEmail, password });
    if (resp.data?.success && resp.data?.data) {
      const data = resp.data.data;
      const u: User = {
        id: data.userId,
        userId: data.userId,
        username: data.username,
        email: data.email,
        fullName: data.fullName,
        roles: data.roles,
        customerId: data.customerId,
        jewellerId: data.jewellerId,
      };
      setToken(data.token);
      setUser(u);
      localStorage.setItem('goldtrust_jwt', data.token);
      localStorage.setItem('goldtrust_user', JSON.stringify(u));
    }
  };

  const register = async (payload: any) => {
    const resp = await apiClient.post('/auth/register', payload);
    if (resp.data?.success && resp.data?.data) {
      const data = resp.data.data;
      const u: User = {
        id: data.userId,
        userId: data.userId,
        username: data.username,
        email: data.email,
        fullName: data.fullName,
        roles: data.roles,
        customerId: data.customerId,
        jewellerId: data.jewellerId,
      };
      setToken(data.token);
      setUser(u);
      localStorage.setItem('goldtrust_jwt', data.token);
      localStorage.setItem('goldtrust_user', JSON.stringify(u));
    }
  };

  const logout = () => {
    setToken(null);
    setUser(null);
    localStorage.removeItem('goldtrust_jwt');
    localStorage.removeItem('goldtrust_user');
  };

  const quickSwitchUser = async (username: string) => {
    await login(username, 'password123');
  };

  const currentRole: UserRole | null = user?.roles?.[0] || null;

  return (
    <AuthContext.Provider
      value={{
        user,
        token,
        role: currentRole,
        isLoading,
        login,
        register,
        logout,
        quickSwitchUser,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
