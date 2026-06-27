import { createBrowserRouter } from 'react-router-dom';
import Home from '../pages/Home';
import Rooms from '../pages/Rooms';
import RoomDetail from '../pages/RoomDetail';
import Booking from '../pages/Booking';
import BookingSuccess from '../pages/BookingSuccess';
import OrderQuery from '../pages/OrderQuery';
import OrderDetail from '../pages/OrderDetail';
import Contact from '../pages/Contact';
import Login from '../pages/Auth/Login';
import Register from '../pages/Auth/Register';
import Profile from '../pages/Auth/Profile';
import UserLayout from '../layouts/UserLayout';
import { GuestRoute, TenantRoute } from './guards';

const router = createBrowserRouter([
  {
    path: '/',
    element: <UserLayout />,
    children: [
      {
        index: true,
        element: <Home />,
      },
      {
        path: 'rooms',
        element: <Rooms />,
      },
      {
        path: 'rooms/:id',
        element: <RoomDetail />,
      },
      {
        path: 'booking',
        element: (
          <TenantRoute>
            <Booking />
          </TenantRoute>
        ),
      },
      {
        path: 'booking/success',
        element: <BookingSuccess />,
      },
      {
        path: 'orders/query',
        element: <OrderQuery />,
      },
      {
        path: 'orders/:orderNo',
        element: <OrderDetail />,
      },
      {
        path: 'contact',
        element: <Contact />,
      },
      {
        path: 'login',
        element: (
          <GuestRoute>
            <Login />
          </GuestRoute>
        ),
      },
      {
        path: 'register',
        element: (
          <GuestRoute>
            <Register />
          </GuestRoute>
        ),
      },
      {
        path: 'profile',
        element: (
          <TenantRoute>
            <Profile />
          </TenantRoute>
        ),
      },
    ],
  },
]);

export default router;
