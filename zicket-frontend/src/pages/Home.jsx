import React, { useState, useEffect } from 'react';
import { publicAPI } from '../services/api';
import { TicketCard } from '../components/TicketCard';
import './Home.css';

export const Home = () => {
  const [tickets, setTickets] = useState([]);
  const [filteredTickets, setFilteredTickets] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [selectedEventType, setSelectedEventType] = useState('ALL');

  const eventTypes = ['ALL', 'CONCERT', 'SPORTS', 'THEATER', 'COMEDY'];

  useEffect(() => {
    fetchTickets();
  }, []);

  const fetchTickets = async () => {
    try {
      setError('');
      const response = await publicAPI.getAllTickets();
      setTickets(response.data);
      setFilteredTickets(response.data);
    } catch (err) {
      setError('Failed to load tickets. Please try again.');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleFilterChange = async (eventType) => {
    setSelectedEventType(eventType);
    setLoading(true);

    try {
      let response;
      if (eventType === 'ALL') {
        response = await publicAPI.getAllTickets();
      } else if (eventType === 'CONCERT') {
        response = await publicAPI.getConcertTickets();
      } else if (eventType === 'SPORTS') {
        response = await publicAPI.getSportsTickets();
      } else if (eventType === 'THEATER') {
        response = await publicAPI.getTheaterTickets();
      } else if (eventType === 'COMEDY') {
        response = await publicAPI.getComedyTickets();
      }

      setFilteredTickets(response.data);
    } catch (err) {
      setError('Failed to filter tickets');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="home-page">
      <div className="container">
        <div className="hero-section">
          <h1>🎉 Discover Amazing Events</h1>
          <p>Browse and purchase tickets from the largest selection of live events</p>
        </div>

        {error && <div className="alert alert-error">{error}</div>}

        <div className="filter-section">
          <h3>Filter by Event Type</h3>
          <div className="filter-buttons">
            {eventTypes.map((type) => (
              <button
                key={type}
                className={`filter-btn ${selectedEventType === type ? 'active' : ''}`}
                onClick={() => handleFilterChange(type)}
              >
                {type === 'ALL' ? '🎪 All Events' : type}
              </button>
            ))}
          </div>
        </div>

        {loading ? (
          <div className="flex-center" style={{ minHeight: '400px' }}>
            <div className="spinner"></div>
          </div>
        ) : filteredTickets.length > 0 ? (
          <>
            <p className="ticket-count">
              Found {filteredTickets.length} {selectedEventType === 'ALL' ? 'tickets' : `${selectedEventType} tickets`}
            </p>
            <div className="tickets-grid">
              {filteredTickets.map((ticket) => (
                <TicketCard key={ticket.ticketId} ticket={ticket} />
              ))}
            </div>
          </>
        ) : (
          <div className="empty-state">
            <p>😴 No tickets available in this category</p>
            <button
              className="btn btn-primary"
              onClick={() => handleFilterChange('ALL')}
            >
              View All Tickets
            </button>
          </div>
        )}
      </div>
    </div>
  );
};

