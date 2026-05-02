import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import './Navbar.css';

export const Navbar = () => {
  const [menuOpen, setMenuOpen] = useState(false);
  const { user, isAuthenticated, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    setMenuOpen(false);
    navigate('/login');
  };

  return (
    <nav className="navbar">
      <div className="navbar-container container">
        <Link to="/" className="navbar-brand">
          🎫 Zicket
        </Link>

        <div className={`navbar-menu ${menuOpen ? 'active' : ''}`}>
          {isAuthenticated() ? (
            <>
              <Link to="/home" className="nav-link" onClick={() => setMenuOpen(false)}>
                Browse Events
              </Link>
              <Link to="/my-tickets" className="nav-link" onClick={() => setMenuOpen(false)}>
                My Tickets
              </Link>
              <Link to="/sell-ticket" className="nav-link" onClick={() => setMenuOpen(false)}>
                Sell Ticket
              </Link>
              <Link to="/purchased-tickets" className="nav-link" onClick={() => setMenuOpen(false)}>
                Purchased
              </Link>
              <Link to="/profile" className="nav-link" onClick={() => setMenuOpen(false)}>
                Profile
              </Link>
              <button className="btn btn-secondary btn-sm" onClick={handleLogout}>
                Logout
              </button>
            </>
          ) : (
            <>
              <Link to="/login" className="nav-link" onClick={() => setMenuOpen(false)}>
                Sign In
              </Link>
              <Link to="/signup" className="btn btn-primary btn-sm" onClick={() => setMenuOpen(false)}>
                Sign Up
              </Link>
            </>
          )}
        </div>

        <div className="hamburger" onClick={() => setMenuOpen(!menuOpen)}>
          <span></span>
          <span></span>
          <span></span>
        </div>
      </div>
    </nav>
  );
};

