import React, { useContext } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { AuthContext } from '../context/AuthContext';
import { Hotel } from 'lucide-react';

const Navbar = () => {
  const { user, logout } = useContext(AuthContext);
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <nav className="navbar">
      <div className="navbar-brand">
        <Link to="/" className="navbar-logo">
          <Hotel size={24} /> HotelBooking
        </Link>
      </div>
      <div className="navbar-links">
        <Link to="/">Home</Link>
        {user ? (
          <>
            <span style={{ marginRight: '1rem', fontWeight: '500', color: '#555' }}>
              Welcome, {user.name || user.email?.split('@')[0]}
            </span>
            {user.role === 'ROLE_ADMIN' ? (
              <Link to="/admin">Admin Panel</Link>
            ) : (
              <Link to="/dashboard">My Bookings</Link>
            )}
            <button onClick={handleLogout} className="btn-logout">Logout</button>
          </>
        ) : (
          <>
            <Link to="/login">Login</Link>
            <Link to="/register" className="btn-primary">Sign Up</Link>
          </>
        )}
      </div>
    </nav>
  );
};

export default Navbar;
