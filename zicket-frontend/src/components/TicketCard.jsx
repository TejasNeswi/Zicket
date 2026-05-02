import React from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import './TicketCard.css';

export const TicketCard = ({
  ticket,
  onAction,
  actionLabel = 'View',
  secondaryAction,
  secondaryActionLabel,
}) => {
  const navigate = useNavigate();
  const { isAuthenticated } = useAuth();

  const getEventTypeColor = (eventType) => {
    const colors = {
      CONCERT: '#ec4899',
      SPORTS: '#3b82f6',
      THEATER: '#a855f7',
      COMEDY: '#f59e0b',
    };
    return colors[eventType] || '#6366f1';
  };

  const handleViewDetails = () => {
    const ticketPath = `/ticket/${ticket.ticketId}`;

    if (!isAuthenticated()) {
      navigate('/login', { state: { from: ticketPath } });
      return;
    }

    navigate(ticketPath);
  };

  return (
    <div className="ticket-card">
      <div
        className="ticket-header"
        style={{ backgroundColor: getEventTypeColor(ticket.eventType) }}
      >
        <span className="event-type">{ticket.eventType}</span>
        <span className={`status ${ticket.status ? 'available' : 'sold'}`}>
          {ticket.status ? '✓ Available' : '✗ Sold'}
        </span>
      </div>

      <div className="ticket-body">
        <h3 className="ticket-name">{ticket.eventName}</h3>

        <div className="ticket-details">
          <div className="detail-item">
            <span className="detail-label">📅 Date</span>
            <span className="detail-value">{ticket.date}</span>
          </div>
          <div className="detail-item">
            <span className="detail-label">📍 Location</span>
            <span className="detail-value">{ticket.location}</span>
          </div>
          <div className="detail-item">
            <span className="detail-label">🎫 Stand</span>
            <span className="detail-value">{ticket.stand}</span>
          </div>
          <div className="detail-item">
            <span className="detail-label">💰 Price</span>
            <span className="detail-value highlight">₹{ticket.price}</span>
          </div>
        </div>

        <div className="ticket-actions">
          <button
            type="button"
            className="btn btn-secondary btn-sm"
            onClick={handleViewDetails}
          >
            View Details
          </button>
          {onAction && (
            <button
              className="btn btn-primary btn-sm"
              onClick={() => onAction(ticket)}
            >
              {actionLabel}
            </button>
          )}
          {secondaryAction && (
            <button
              className="btn btn-danger btn-sm"
              onClick={() => secondaryAction(ticket)}
            >
              {secondaryActionLabel}
            </button>
          )}
        </div>
      </div>
    </div>
  );
};

