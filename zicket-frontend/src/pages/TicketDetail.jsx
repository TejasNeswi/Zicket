import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { ticketAPI } from '../services/api';
import './TicketDetail.css';

export const TicketDetail = () => {
  const { ticketId } = useParams();
  const navigate = useNavigate();
  const [ticket, setTicket] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchTicketDetail();
  }, [ticketId]);

  const fetchTicketDetail = async () => {
    try {
      setError('');
      const response = await ticketAPI.getTicketInfo(ticketId);
      setTicket(response.data);
    } catch (err) {
      setError('Failed to load ticket details');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleBuyTicket = () => {
    navigate(`/payment/${ticketId}`);
  };

  if (loading) {
    return (
      <div className="flex-center" style={{ minHeight: '100vh' }}>
        <div className="spinner"></div>
      </div>
    );
  }

  if (error || !ticket) {
    return (
      <div className="container" style={{ minHeight: '100vh', padding: '3rem 0' }}>
        <div className="alert alert-error">{error || 'Ticket not found'}</div>
        <button className="btn btn-primary" onClick={() => navigate('/home')}>
          Back to Tickets
        </button>
      </div>
    );
  }

  return (
    <div className="ticket-detail-page">
      <div className="container">
        <button className="btn btn-secondary btn-sm" onClick={() => navigate(-1)}>
          ← Back
        </button>

        <div className="detail-container">
          <div className="detail-header">
            <div className={`event-badge ${ticket.eventType.toLowerCase()}`}>
              {ticket.eventType}
            </div>
            <h1>{ticket.eventName}</h1>
            <div className={`status-badge ${ticket.status ? 'available' : 'sold-out'}`}>
              {ticket.status ? '✓ Available' : '✗ Sold Out'}
            </div>
          </div>

          <div className="detail-grid">
            <div className="detail-card">
              <h3>Event Information</h3>
              <div className="info-row">
                <span className="label">📅 Date:</span>
                <span className="value">{ticket.date}</span>
              </div>
              <div className="info-row">
                <span className="label">⏰ Posted at:</span>
                <span className="value">
                  {new Date(ticket.time).toLocaleString()}
                </span>
              </div>
              <div className="info-row">
                <span className="label">📍 Location:</span>
                <span className="value">{ticket.location}</span>
              </div>
              <div className="info-row">
                <span className="label">🎫 Stand/Seat:</span>
                <span className="value">{ticket.stand}</span>
              </div>
            </div>

            <div className="detail-card pricing-card">
              <h3>Pricing</h3>
              <div className="price-display">
                <span className="currency">₹</span>
                <span className="amount">{ticket.price}</span>
              </div>

              {ticket.status ? (
                <button
                  className="btn btn-primary btn-large"
                  onClick={handleBuyTicket}
                >
                  🛒 Buy This Ticket
                </button>
              ) : (
                <button className="btn btn-danger btn-large" disabled>
                  Sold Out
                </button>
              )}
            </div>
          </div>

          {ticket.fileId && (
            <div className="detail-card">
              <h3>Ticket Document</h3>
              <p>The ticket file is available from My Tickets for the seller, and from Purchased after a buyer completes payment.</p>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

