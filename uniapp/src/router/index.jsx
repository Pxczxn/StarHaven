import { createBrowserRouter } from 'react-router-dom';
import Home from '../pages/Home';
import Rooms from '../pages/Rooms';
import RoomDetail from '../pages/RoomDetail';
import Booking from '../pages/Booking';
import BookingSuccess from '../pages/BookingSuccess';
import OrderQuery from '../pages/OrderQuery';
import OrderDetail from '../pages/OrderDetail';
import Contact from '../pages/Contact';

const router = createBrowserRouter([
  {
    path: '/',
    element: <Home />,
  },
  {
    path: '/rooms',
    element: <Rooms />,
  },
  {
    path: '/rooms/:id',
    element: <RoomDetail />,
  },
  {
    path: '/booking',
    element: <Booking />,
  },
  {
    path: '/booking/success',
    element: <BookingSuccess />,
  },
  {
    path: '/orders/query',
    element: <OrderQuery />,
  },
  {
    path: '/orders/:orderNo',
    element: <OrderDetail />,
  },
  {
    path: '/contact',
    element: <Contact />,
  },
]);

export default router;
