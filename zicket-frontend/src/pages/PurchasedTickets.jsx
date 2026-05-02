import React, { useState, useEffect } from 'react';
import { ticketAPI } from '../services/api';
import { TicketCard } from '../components/TicketCard';
import { downloadResponseBlob } from '../utils/download';
import './PurchasedTickets.css';

export const PurchasedTickets = () => {
  const [tickets, setTickets] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchPurchasedTickets();
  }, []);

  const fetchPurchasedTickets = async () => {
    try {
      setError('');
      const token = localStorage.getItem('authToken');
      console.log('[PurchasedTickets] Token exists:', !!token);
      console.log('[PurchasedTickets] Fetching purchased tickets...');
      
      const response = await ticketAPI.getPurchasedTickets();
      console.log('[PurchasedTickets] Success - loaded tickets:', response.data?.length || 0);
      setTickets(response.data || []);
    } catch (err) {
      console.error('[PurchasedTickets] Error fetching tickets:', {
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
        setError('Failed to load purchased tickets');
      }
    } finally {
      setLoading(false);
    }
  };

  const handleDownloadTicket = async (ticketId) => {
    try {
      const response = await ticketAPI.viewTicketFile(ticketId);
      downloadResponseBlob(response, `ticket-${ticketId}`);
    } catch (err) {
      alert('Failed to download ticket');
      console.error(err);
    }
  };

  return (
    <div className="purchased-tickets-page">
      <div className="container">
        <h1>🛍️ My Purchased Tickets</h1>
        <p className="subtitle">Tickets you've purchased from other sellers</p>

        {error && <div className="alert alert-error">{error}</div>}

        {loading ? (
          <div className="flex-center" style={{ minHeight: '400px' }}>
            <div className="spinner"></div>
          </div>
        ) : tickets.length > 0 ? (
          <>
            <p className="ticket-count">
              You have purchased {tickets.length} ticket(s)
            </p>
            <div className="tickets-grid">
              {tickets.map((ticket) => (
                <div key={ticket.ticketId} className="ticket-wrapper">
                  <TicketCard
                    ticket={ticket}
                    onAction={() => handleDownloadTicket(ticket.ticketId)}
                    actionLabel="📥 Download"
                  />
                </div>
              ))}
            </div>
          </>
        ) : (
          <div className="empty-state">
            <p>😴 You haven't purchased any tickets yet</p>
            <p style={{ color: 'var(--text-light)', marginBottom: '1.5rem' }}>
              Browse events and find tickets to purchase
            </p>
          </div>
        )}
      </div>
    </div>
  );
};

