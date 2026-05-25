import React, { useState, useEffect, useContext } from 'react';
import api from '../utils/api';
import { AuthContext } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';
import { getApiErrorMessage } from '../utils/error';

const DashboardPage = () => {
  const { user } = useContext(AuthContext);
  const navigate = useNavigate();
  const [bookings, setBookings] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [filter, setFilter] = useState('ALL');

  useEffect(() => {
    if (!user) {
      navigate('/login');
      return;
    }

    fetchBookings();
  }, [user, navigate]);

  const fetchBookings = async () => {
    try {
      setError('');
      const response = await api.get('/bookings');
      setBookings(response.data);
    } catch (err) {
      setError(getApiErrorMessage(err, 'Failed to fetch bookings.'));
    } finally {
      setLoading(false);
    }
  };

  const handleCancel = async (id) => {
    if (window.confirm('Are you sure you want to cancel this booking?')) {
      try {
        setError('');
        setSuccess('');
        await api.delete(`/bookings/${id}`);
        setSuccess('Booking cancelled successfully. A cancellation email has been sent.');
        fetchBookings(); // refresh list
      } catch (err) {
        setError(getApiErrorMessage(err, 'Failed to cancel booking.'));
      }
    }
  };

  const filteredBookings = bookings.filter(booking => {
    if (filter === 'ALL') return true;
    return booking.status === filter;
  });

  if (loading) return <div>Loading...</div>;

  return (
    <div className="dashboard-page">
      <h2>My Bookings</h2>
      {success && <div className="alert alert-success">{success}</div>}
      {error && <div className="alert alert-error">{error}</div>}
      
      {bookings.length === 0 ? (
        <p>You have no bookings yet.</p>
      ) : (
        <>
          <div className="filter-section" style={{ marginBottom: '1.5rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
            <label style={{ fontWeight: 'bold' }}>Filter by Status: </label>
            <select 
              value={filter} 
              onChange={(e) => setFilter(e.target.value)}
              style={{ padding: '0.5rem', borderRadius: '4px', border: '1px solid #ccc' }}
            >
              <option value="ALL">All Bookings</option>
              <option value="PENDING">Pending</option>
              <option value="CONFIRMED">Confirmed</option>
              <option value="CANCELLED">Cancelled</option>
            </select>
          </div>

          {filteredBookings.length === 0 ? (
            <p>No bookings found matching "{filter}".</p>
          ) : (
            <div className="booking-list">
              {filteredBookings.map(booking => (
                <div key={booking.id} className="card booking-card">
                  <div className="booking-info">
                    <h3>Booking ID: #{booking.id}</h3>
                    <p><strong>Status:</strong> <span className={`status ${booking.status.toLowerCase()}`}>{booking.status}</span></p>
                    <p><strong>Check-in:</strong> {booking.checkInDate}</p>
                    <p><strong>Check-out:</strong> {booking.checkOutDate}</p>
                  </div>
                  <div className="booking-actions">
                    {booking.status !== 'CANCELLED' && (
                      <button onClick={() => handleCancel(booking.id)} className="btn-danger">Cancel Booking</button>
                    )}
                  </div>
                </div>
              ))}
            </div>
          )}
        </>
      )}
    </div>
  );
};

export default DashboardPage;
