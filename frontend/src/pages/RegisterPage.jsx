import React, { useState, useContext } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { AuthContext } from '../context/AuthContext';
import { getApiErrorMessage } from '../utils/error';

const RegisterPage = () => {
  const { register } = useContext(AuthContext);
  const navigate = useNavigate();

  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await register(name, email, password);
      navigate('/login');
    } catch (err) {
      setError(getApiErrorMessage(err, 'Failed to register.'));
    }
  };

  return (
    <div className="auth-page">
      <div className="auth-container card">
        <h2>Sign Up</h2>
        {error && <div className="alert alert-error">{error}</div>}
        <form onSubmit={handleSubmit} className="auth-form">
          <div className="form-group">
            <label>Full Name</label>
            <input type="text" value={name} onChange={e => setName(e.target.value)} required />
          </div>
          <div className="form-group">
            <label>Email</label>
            <input type="email" value={email} onChange={e => setEmail(e.target.value)} required />
          </div>
          <div className="form-group">
            <label>Password (Min 6 chars)</label>
            <input type="password" value={password} onChange={e => setPassword(e.target.value)} required minLength={6}/>
          </div>
          <button type="submit" className="btn-primary w-full">Register</button>
        </form>
        
        <div style={{ textAlign: 'center', margin: '1.5rem 0' }}>
          <p style={{ marginBottom: '1rem', color: '#666' }}>— OR —</p>
          <a href="http://localhost:8080/oauth2/authorization/google" className="btn-primary w-full block text-center" style={{ backgroundColor: '#DB4437' }}>
            Sign up with Google
          </a>
        </div>

        <p className="auth-link">
          Already have an account? <Link to="/login">Login</Link>
        </p>
      </div>
    </div>
  );
};

export default RegisterPage;
