import React, { useEffect, useContext } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { AuthContext } from '../context/AuthContext';

const OAuth2RedirectHandler = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { setUser } = useContext(AuthContext);

  useEffect(() => {
    const params = new URLSearchParams(location.search);
    const token = params.get('token');
    const email = params.get('email');
    const name = params.get('name');
    const role = params.get('role');
    const id = params.get('id');

    if (token) {
      const userData = { token, email, name, role, id };
      localStorage.setItem('user', JSON.stringify(userData));
      setUser(userData); // We need to make sure setUser is exported from AuthContext
      navigate('/');
    } else {
      navigate('/login');
    }
  }, [location, navigate, setUser]);

  return (
    <div style={{ display: 'flex', justifyContent: 'center', marginTop: '50px' }}>
      <h2>Authenticating with Google...</h2>
    </div>
  );
};

export default OAuth2RedirectHandler;
