import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { ticketAPI } from '../services/api';
import { TicketCard } from '../components/TicketCard';
import { downloadResponseBlob } from '../utils/download';
import './MyTickets.css';

export const MyTickets = () => {
  const navigate = useNavigate();
  const [tickets, setTickets] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchMyTickets();
  }, []);

  const fetchMyTickets = async () => {
    try {
      setError('');
      const token = localStorage.getItem('authToken');
      console.log('[MyTickets] Token exists:', !!token);
      console.log('[MyTickets] Fetching my tickets...');
      
      const response = await ticketAPI.getMyTickets();
      console.log('[MyTickets] Success - loaded tickets:', response.data?.length || 0);
      setTickets(response.data || []);
    } catch (err) {
      console.error('[MyTickets] Error fetching tickets:', {
        status: err.response?.status,
        message: err.message,
        data: err.response?.data
      });
      
      if (err.response?.status === 404) {
        setTickets([]);
      } else if (err.response?.status === 401) {
        setError('Session expired. Please login again.');
      } else if (err.response?.status === 403) {
        setError('Access denied. Please make sure you are logged in.');
      } else {
        setError('Failed to load your tickets');
      }
    } finally {
      setLoading(false);
    }
  };

  const handleDeleteTicket = async (ticketId) => {
    if (!window.confirm('Are you sure you want to delete this ticket?')) {
      return;
    }

    try {
      await ticketAPI.deleteTicket(ticketId);
      setTickets(tickets.filter((t) => t.ticketId !== ticketId));
      alert('Ticket deleted successfully');
    } catch (err) {
      setError('Failed to delete ticket');
      console.error(err);
    }
  };

  const handleDownloadTicket = async (ticketId) => {
    try {
      const response = await ticketAPI.viewTicketFile(ticketId);
      downloadResponseBlob(response, `ticket-${ticketId}`);
    } catch (err) {
      alert('Ticket file is not available for download');
      console.error(err);
    }
  };

  return (
    <div className="my-tickets-page">
      <div className="container">
        <div className="page-header">
          <div>
            <h1>My Tickets for Sale</h1>
            <p className="subtitle">Tickets you've created and are selling</p>
          </div>
          <button className="btn btn-primary" onClick={() => navigate('/sell-ticket')}>
            List Ticket
          </button>
        </div>

        {error && <div className="alert alert-error">{error}</div>}

        {loading ? (
          <div className="flex-center" style={{ minHeight: '400px' }}>
            <div className="spinner"></div>
          </div>
        ) : tickets.length > 0 ? (
          <>
            <p className="ticket-count">You have {tickets.length} ticket(s) for sale</p>
            <div className="tickets-grid">
              {tickets.map((ticket) => (
                <div key={ticket.ticketId} className="ticket-wrapper">
                  <TicketCard
                    ticket={ticket}
                    onAction={() => handleDownloadTicket(ticket.ticketId)}
                    actionLabel="Download"
                    secondaryAction={() => handleDeleteTicket(ticket.ticketId)}
                    secondaryActionLabel="Delete"
                  />
                </div>
              ))}
            </div>
          </>
        ) : (
          <div className="empty-state">
            <p>You haven't created any tickets for sale yet</p>
            <p style={{ color: 'var(--text-light)', marginBottom: '1.5rem' }}>
              Start by listing your first ticket
            </p>
            <button className="btn btn-primary" onClick={() => navigate('/sell-ticket')}>
              List Your First Ticket
            </button>
          </div>
        )}
      </div>
    </div>
  );
};
