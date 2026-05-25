import React, { useState, useEffect } from 'react';
import api from '../utils/api';
import { getApiErrorMessage } from '../utils/error';

const AdminDashboardPage = () => {
  const [activeTab, setActiveTab] = useState('bookings');
  
  const [bookings, setBookings] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  // New Hotel State
  const [newHotel, setNewHotel] = useState({
    name: '', location: '', description: '', amenities: ''
  });
  const [newRooms, setNewRooms] = useState([
    { roomType: '', price: '' }
  ]);

  // Existing Hotel State
  const [existingHotels, setExistingHotels] = useState([]);
  const [selectedHotelId, setSelectedHotelId] = useState('');
  const [addRoomType, setAddRoomType] = useState('');
  const [addRoomPrice, setAddRoomPrice] = useState('');

  const fetchHotels = async () => {
    try {
      const res = await api.get('/hotels');
      setExistingHotels(res.data);
    } catch (err) {
      console.error(err);
    }
  };

  const fetchAllBookings = async () => {
    try {
      setError('');
      const response = await api.get('/bookings/admin/all');
      setBookings(response.data);
      setLoading(false);
    } catch (err) {
      setError(getApiErrorMessage(err, 'Failed to fetch bookings. Are you an Admin?'));
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchAllBookings();
    fetchHotels();
  }, []);

  const updateStatus = async (id, status) => {
    try {
      setError('');
      await api.put(`/bookings/${id}/status`, { status });
      setSuccess(`Booking status updated to ${status}.`);
      fetchAllBookings();
    } catch (err) {
      setError(getApiErrorMessage(err, 'Failed to update status.'));
    }
  };

  const handleAddRoom = () => {
    setNewRooms([...newRooms, { roomType: '', price: '' }]);
  };

  const handleRoomChange = (index, field, value) => {
    const updated = [...newRooms];
    updated[index][field] = value;
    setNewRooms(updated);
  };

  const handleCreateHotel = async (e) => {
    e.preventDefault();
    try {
      setError('');
      const payload = {
        ...newHotel,
        rooms: newRooms.filter(r => r.roomType && r.price)
      };
      await api.post('/hotels', payload);
      setSuccess('Hotel created successfully!');
      setNewHotel({ name: '', location: '', description: '', amenities: '' });
      setNewRooms([{ roomType: '', price: '' }]);
      fetchHotels();
    } catch (err) {
      setError(getApiErrorMessage(err, 'Failed to create hotel.'));
    }
  };

  const handleAddRoomToExisting = async (e) => {
    e.preventDefault();
    if (!selectedHotelId) {
      setError('Please select a hotel first.');
      return;
    }
    try {
      setError('');
      await api.post(`/hotels/${selectedHotelId}/rooms`, {
        roomType: addRoomType,
        price: addRoomPrice
      });
      setSuccess('Room successfully added to the existing hotel!');
      setAddRoomType('');
      setAddRoomPrice('');
    } catch (err) {
      setError(getApiErrorMessage(err, 'Failed to add room.'));
    }
  };

  if (loading) return <div className="loading">Loading Admin Panel...</div>;

  return (
    <div className="dashboard-container">
      <h2>Admin Panel</h2>
      
      <div className="admin-tabs" style={{ display: 'flex', gap: '1rem', marginBottom: '2rem' }}>
        <button 
          className={`btn-primary ${activeTab !== 'bookings' ? 'outline' : ''}`}
          onClick={() => { setActiveTab('bookings'); setError(''); setSuccess(''); }}
        >
          Manage Bookings
        </button>
        <button 
          className={`btn-primary ${activeTab !== 'hotels' ? 'outline' : ''}`}
          onClick={() => { setActiveTab('hotels'); setError(''); setSuccess(''); }}
        >
          Add New Hotel
        </button>
      </div>

      {success && <div className="alert alert-success">{success}</div>}
      {error && <div className="alert alert-error">{error}</div>}

      {activeTab === 'bookings' && (
        <div>
          {bookings.length === 0 ? (
            <p>No bookings found in the system.</p>
          ) : (
            <div className="bookings-list">
              {bookings.map(booking => (
                <div key={booking.id} className="booking-card">
                  <div className="booking-header">
                    <h3>{booking.room?.hotel?.name} - {booking.room?.roomType}</h3>
                    <span className={`status-badge ${booking.status.toLowerCase()}`}>
                      {booking.status}
                    </span>
                  </div>
                  <div className="booking-details">
                    <p><strong>Booking ID:</strong> #{booking.id}</p>
                    <p><strong>User:</strong> {booking.user?.name} ({booking.user?.email})</p>
                    <p><strong>Check-in:</strong> {booking.checkInDate}</p>
                    <p><strong>Check-out:</strong> {booking.checkOutDate}</p>
                  </div>
                  {booking.status === 'PENDING' && (
                    <div style={{ marginTop: '1rem', display: 'flex', gap: '1rem' }}>
                      <button 
                        onClick={() => updateStatus(booking.id, 'CONFIRMED')}
                        className="btn-primary"
                        style={{ backgroundColor: '#28a745' }}
                      >
                        Approve
                      </button>
                      <button 
                        onClick={() => updateStatus(booking.id, 'CANCELLED')}
                        className="btn-primary"
                        style={{ backgroundColor: '#dc3545' }}
                      >
                        Reject
                      </button>
                    </div>
                  )}
                </div>
              ))}
            </div>
          )}
        </div>
      )}

      {activeTab === 'hotels' && (
        <div className="card" style={{ maxWidth: '800px', margin: '0 auto' }}>
          <h3>Create New Hotel</h3>
          <form onSubmit={handleCreateHotel} className="auth-form" style={{ marginTop: '1rem' }}>
            <div className="form-group">
              <label>Hotel Name</label>
              <input type="text" value={newHotel.name} onChange={e => setNewHotel({...newHotel, name: e.target.value})} required />
            </div>
            <div className="form-group">
              <label>Location</label>
              <input type="text" value={newHotel.location} onChange={e => setNewHotel({...newHotel, location: e.target.value})} required />
            </div>
            <div className="form-group">
              <label>Description</label>
              <textarea value={newHotel.description} onChange={e => setNewHotel({...newHotel, description: e.target.value})} required rows="3" style={{ width: '100%', padding: '0.5rem', borderRadius: '4px', border: '1px solid #ccc' }} />
            </div>
            <div className="form-group">
              <label>Amenities (comma separated)</label>
              <input type="text" value={newHotel.amenities} onChange={e => setNewHotel({...newHotel, amenities: e.target.value})} />
            </div>
            
            <h4 style={{ marginTop: '1.5rem', marginBottom: '1rem' }}>Room Types</h4>
            {newRooms.map((room, idx) => (
              <div key={idx} style={{ display: 'flex', gap: '1rem', marginBottom: '1rem' }}>
                <div className="form-group" style={{ flex: 1 }}>
                  <input type="text" placeholder="Room Type (e.g. Deluxe)" value={room.roomType} onChange={e => handleRoomChange(idx, 'roomType', e.target.value)} required />
                </div>
                <div className="form-group" style={{ flex: 1 }}>
                  <input type="number" placeholder="Price per night (₹)" value={room.price} onChange={e => handleRoomChange(idx, 'price', e.target.value)} required />
                </div>
              </div>
            ))}
            <button type="button" onClick={handleAddRoom} className="btn-primary outline" style={{ marginBottom: '1.5rem' }}>+ Add Another Room Type</button>
            
            <button type="submit" className="btn-primary w-full">Save Hotel</button>
          </form>
        </div>
      )}

      {activeTab === 'hotels' && (
        <div className="card" style={{ maxWidth: '800px', margin: '2rem auto 0 auto' }}>
          <h3>Add Room to Existing Hotel</h3>
          <form onSubmit={handleAddRoomToExisting} className="auth-form" style={{ marginTop: '1rem' }}>
            <div className="form-group">
              <label>Select Hotel</label>
              <select 
                value={selectedHotelId} 
                onChange={e => setSelectedHotelId(e.target.value)} 
                required
                style={{ width: '100%', padding: '0.5rem', borderRadius: '4px', border: '1px solid #ccc' }}
              >
                <option value="">-- Select a Hotel --</option>
                {existingHotels.map(h => (
                  <option key={h.id} value={h.id}>{h.name} - {h.location}</option>
                ))}
              </select>
            </div>
            <div style={{ display: 'flex', gap: '1rem', marginBottom: '1rem' }}>
              <div className="form-group" style={{ flex: 1 }}>
                <label>Room Type</label>
                <input type="text" placeholder="e.g. Presidential Suite" value={addRoomType} onChange={e => setAddRoomType(e.target.value)} required />
              </div>
              <div className="form-group" style={{ flex: 1 }}>
                <label>Price per night (₹)</label>
                <input type="number" placeholder="Price" value={addRoomPrice} onChange={e => setAddRoomPrice(e.target.value)} required />
              </div>
            </div>
            <button type="submit" className="btn-primary w-full">Add Room</button>
          </form>
        </div>
      )}
    </div>
  );
};

export default AdminDashboardPage;
