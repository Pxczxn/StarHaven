import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import AdminLayout from '../layouts/AdminLayout';
import Login from '../pages/login';
import Dashboard from '../pages/dashboard';
import RoomTypes from '../pages/roomTypes';
import Rooms from '../pages/rooms';
import Orders from '../pages/orders';
import Customers from '../pages/customers';
import Finance from '../pages/finance';
import Operations from '../pages/operations';
import Users from '../pages/users';
import Settings from '../pages/settings';
import useAuthStore from '../store/authStore';

// 路由守卫组件
const PrivateRoute = ({ children }) => {
  const { isAuthenticated } = useAuthStore();
  return isAuthenticated() ? children : <Navigate to="/login" replace />;
};

const AppRouter = () => {
  return (
    <BrowserRouter>
      <Routes>
        {/* 登录页 */}
        <Route path="/login" element={<Login />} />

        {/* 后台管理 */}
        <Route
          path="/"
          element={
            <PrivateRoute>
              <AdminLayout />
            </PrivateRoute>
          }
        >
          <Route index element={<Navigate to="/dashboard" replace />} />
          <Route path="dashboard" element={<Dashboard />} />
          <Route path="room-types" element={<RoomTypes />} />
          <Route path="rooms" element={<Rooms />} />
          <Route path="orders" element={<Orders />} />
          <Route path="customers" element={<Customers />} />
          <Route path="finance" element={<Finance />} />
          <Route path="operations" element={<Operations />} />
          <Route path="users" element={<Users />} />
          <Route path="settings" element={<Settings />} />
        </Route>

        {/* 404 */}
        <Route path="*" element={<Navigate to="/login" replace />} />
      </Routes>
    </BrowserRouter>
  );
};

export default AppRouter;
