import React, { useState, useEffect, useContext } from 'react';
import { useParams, useNavigate, useLocation } from 'react-router-dom';
import api from '../utils/api';
import { AuthContext } from '../context/AuthContext';
import { getApiErrorMessage } from '../utils/error';

const HotelDetailsPage = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const location = useLocation();
  const { user } = useContext(AuthContext);
  
  const [hotel, setHotel] = useState(null);
  const [rooms, setRooms] = useState([]);
  const [loading, setLoading] = useState(true);

  // Booking states
  const [checkIn, setCheckIn] = useState('');
  const [checkOut, setCheckOut] = useState('');
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  
  // Checkout Modal states
  const [showCheckoutModal, setShowCheckoutModal] = useState(false);
  const [selectedRoom, setSelectedRoom] = useState(null);
  const [paymentDetails, setPaymentDetails] = useState({
    address: '',
    phone: '',
    cardNumber: '',
    expiry: '',
    cvv: ''
  });

  useEffect(() => {
    const params = new URLSearchParams(location.search);
    setCheckIn(params.get('checkIn') || '');
    setCheckOut(params.get('checkOut') || '');
  }, [location.search]);

  useEffect(() => {
    const fetchHotelDetails = async () => {
      try {
        const hotelRes = await api.get(`/hotels/${id}`);
        setHotel(hotelRes.data);

        const roomQuery = checkIn && checkOut ? `?checkInDate=${checkIn}&checkOutDate=${checkOut}` : '';
        const roomRes = await api.get(`/hotels/${id}/rooms${roomQuery}`);
        setRooms(roomRes.data);
      } catch (err) {
        setError(getApiErrorMessage(err, 'Failed to load hotel details.'));
      } finally {
        setLoading(false);
      }
    };

    fetchHotelDetails();
  }, [id, checkIn, checkOut]);

  const handleInitiateBooking = (room) => {
    if (!user) {
      navigate('/login');
      return;
    }
    
    if (!checkIn || !checkOut) {
      setError('Please select check-in and check-out dates.');
      return;
    }

    if (checkIn >= checkOut) {
      setError('Check-out date must be after check-in date.');
      return;
    }

    setError('');
    setSelectedRoom(room);
    setShowCheckoutModal(true);
  };

  const submitBooking = async (e) => {
    e.preventDefault();
    try {
      setError('');
      setSuccess('');
      await api.post('/bookings', {
        roomId: selectedRoom.id,
        checkInDate: checkIn,
        checkOutDate: checkOut
      });
      setSuccess('Booking request submitted successfully. You will receive an email when it is confirmed.');
      setShowCheckoutModal(false);
      setSelectedRoom(null);
      setPaymentDetails({ address: '', phone: '', cardNumber: '', expiry: '', cvv: '' });
      
      const roomRes = await api.get(`/hotels/${id}/rooms?checkInDate=${checkIn}&checkOutDate=${checkOut}`);
      setRooms(roomRes.data);
    } catch (err) {
      setError(getApiErrorMessage(err, 'Failed to book room.'));
      setShowCheckoutModal(false);
    }
  };

  const calculateTotal = () => {
    if (!checkIn || !checkOut || !selectedRoom) return 0;
    const start = new Date(checkIn);
    const end = new Date(checkOut);
    const nights = Math.max(1, Math.ceil((end - start) / (1000 * 60 * 60 * 24)));
    return nights * selectedRoom.price;
  };

  if (loading) return <div>Loading hotel details...</div>;
  if (!hotel) return <div>Hotel not found.</div>;

  return (
    <div className="hotel-details-page">
      <div className="hotel-header">
        <h1>{hotel.name}</h1>
        <p className="location">{hotel.location}</p>
        <p className="description">{hotel.description}</p>
        <div className="amenities">
          <strong>Amenities: </strong> {hotel.amenities}
        </div>
      </div>

      <div className="booking-section">
        <h2>Available Rooms</h2>
        
        {success && <div className="alert alert-success">{success}</div>}
        {error && <div className="alert alert-error">{error}</div>}

        <div className="date-picker">
          <div>
            <label>Check-in Date: </label>
            <input type="date" value={checkIn} onChange={(e) => setCheckIn(e.target.value)} min={new Date().toISOString().split('T')[0]} />
          </div>
          <div>
            <label>Check-out Date: </label>
            <input type="date" value={checkOut} onChange={(e) => setCheckOut(e.target.value)} min={checkIn || new Date().toISOString().split('T')[0]} />
          </div>
        </div>

        {rooms.length === 0 ? (
          <p>No rooms available for the selected dates.</p>
        ) : (
          <div className="grid">
            {rooms.map(room => (
              <div key={room.id} className="card room-card">
                <h3>{room.roomType}</h3>
                <p className="price">₹{room.price} / night</p>
                <button 
                  onClick={() => handleInitiateBooking(room)} 
                  className="btn-primary"
                >
                  Book Now
                </button>
              </div>
            ))}
          </div>
        )}
      </div>

      {showCheckoutModal && selectedRoom && (
        <div className="modal-overlay">
          <div className="modal-content checkout-modal">
            <h2>Complete Your Booking</h2>
            
            <div className="checkout-summary">
              <h3>Booking Summary</h3>
              <p><strong>Room:</strong> {selectedRoom.roomType}</p>
              <p><strong>Dates:</strong> {checkIn} to {checkOut}</p>
              <p className="total-price"><strong>Total Amount:</strong> ₹{calculateTotal()}</p>
            </div>

            <form onSubmit={submitBooking} className="checkout-form">
              <h3>Billing & Contact Details</h3>
              <div className="form-group">
                <label>Full Address</label>
                <input type="text" required value={paymentDetails.address} onChange={e => setPaymentDetails({...paymentDetails, address: e.target.value})} placeholder="123 Main St, City, State" />
              </div>
              <div className="form-group">
                <label>Phone Number</label>
                <input type="tel" required value={paymentDetails.phone} onChange={e => setPaymentDetails({...paymentDetails, phone: e.target.value})} placeholder="+91 9876543210" />
              </div>

              <h3>Mock Payment (Do not enter real card)</h3>
              <div className="form-group">
                <label>Card Number</label>
                <input type="text" required maxLength="16" value={paymentDetails.cardNumber} onChange={e => setPaymentDetails({...paymentDetails, cardNumber: e.target.value})} placeholder="1234 5678 9101 1121" />
              </div>
              <div className="payment-row">
                <div className="form-group">
                  <label>Expiry Date</label>
                  <input type="text" required maxLength="5" value={paymentDetails.expiry} onChange={e => setPaymentDetails({...paymentDetails, expiry: e.target.value})} placeholder="MM/YY" />
                </div>
                <div className="form-group">
                  <label>CVV</label>
                  <input type="text" required maxLength="4" value={paymentDetails.cvv} onChange={e => setPaymentDetails({...paymentDetails, cvv: e.target.value})} placeholder="123" />
                </div>
              </div>

              <div className="modal-actions">
                <button type="button" className="btn-secondary" onClick={() => setShowCheckoutModal(false)}>Cancel</button>
                <button type="submit" className="btn-primary">Confirm Payment & Book</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default HotelDetailsPage;
