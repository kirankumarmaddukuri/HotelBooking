import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import api from '../utils/api';
import { Search } from 'lucide-react';
import { getApiErrorMessage } from '../utils/error';

const HomePage = () => {
  const [hotels, setHotels] = useState([]);
  const [location, setLocation] = useState('');
  const [checkIn, setCheckIn] = useState('');
  const [checkOut, setCheckOut] = useState('');
  const [error, setError] = useState('');

  useEffect(() => {
    fetchHotels();
  }, []);

  const fetchHotels = async (loc = '', startDate = '', endDate = '') => {
    try {
      setError('');
      const params = new URLSearchParams();
      if (loc) params.append('location', loc);
      if (startDate && endDate) {
        params.append('checkInDate', startDate);
        params.append('checkOutDate', endDate);
      }
      const url = params.toString() ? `/hotels?${params.toString()}` : '/hotels';
      const response = await api.get(url);
      setHotels(response.data);
    } catch (error) {
      setError(getApiErrorMessage(error, 'Failed to fetch hotels.'));
    }
  };

  const handleSearch = (e) => {
    e.preventDefault();

    if ((checkIn && !checkOut) || (!checkIn && checkOut)) {
      setError('Please select both check-in and check-out dates.');
      return;
    }

    if (checkIn && checkOut && checkIn >= checkOut) {
      setError('Check-out date must be after check-in date.');
      return;
    }

    fetchHotels(location, checkIn, checkOut);
  };

  return (
    <div className="home-page">
      <header className="hero">
        <h1>Find Your Perfect Stay</h1>
        <form onSubmit={handleSearch} className="search-form">
          <input 
            type="text" 
            placeholder="Search by location..." 
            value={location}
            onChange={(e) => setLocation(e.target.value)}
            className="search-input"
          />
          <input
            type="date"
            value={checkIn}
            onChange={(e) => setCheckIn(e.target.value)}
            min={new Date().toISOString().split('T')[0]}
          />
          <input
            type="date"
            value={checkOut}
            onChange={(e) => setCheckOut(e.target.value)}
            min={checkIn || new Date().toISOString().split('T')[0]}
          />
          <button type="submit" className="btn-search"><Search size={18} /> Search</button>
        </form>
        {error && <div className="alert alert-error search-alert">{error}</div>}
      </header>

      <section className="hotel-list">
        <h2>Available Hotels</h2>
        {hotels.length === 0 ? (
          <p>No hotels found in this location.</p>
        ) : (
          <div className="grid">
            {hotels.map((hotel) => (
              <div key={hotel.id} className="card hotel-card">
                <h3>{hotel.name}</h3>
                <p className="location">{hotel.location}</p>
                <p className="description">{hotel.description}</p>
                <Link
                  to={`/hotel/${hotel.id}${checkIn && checkOut ? `?checkIn=${checkIn}&checkOut=${checkOut}` : ''}`}
                  className="btn-primary w-full text-center block"
                >
                  View Details
                </Link>
              </div>
            ))}
          </div>
        )}
      </section>
    </div>
  );
};

export default HomePage;
