import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { ticketAPI, paymentAPI } from '../services/api';
import './Payment.css';

export const Payment = () => {
  const { ticketId } = useParams();
  const navigate = useNavigate();
  const [ticket, setTicket] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [processing, setProcessing] = useState(false);

  const [formData, setFormData] = useState({
    cardNo: '',
    cvv: '',
    from: '',
  });

  useEffect(() => {
    fetchTicketDetail();
  }, [ticketId]);

  const fetchTicketDetail = async () => {
    try {
      const response = await ticketAPI.getTicketInfo(ticketId);
      setTicket(response.data);
    } catch (err) {
      setError('Failed to load ticket details');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: value,
    });
  };

  const validateForm = () => {
    if (!formData.cardNo || !formData.cvv || !formData.from) {
      setError('All fields are required');
      return false;
    }

    if (formData.cardNo.length !== 12) {
      setError('Card number must be 12 digits');
      return false;
    }

    if (formData.cvv.length !== 3) {
      setError('CVV must be 3 digits');
      return false;
    }

    if (!formData.from.trim()) {
      setError('Seller username is required');
      return false;
    }

    return true;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');

    if (!validateForm()) return;

    setProcessing(true);

    try {
      await paymentAPI.processPayment(ticketId, formData);
      setSuccess('Payment successful! You have purchased the ticket.');
      setTimeout(() => {
        navigate('/purchased-tickets');
      }, 2000);
    } catch (err) {
      const errorMsg =
        err.response?.data ||
        'Payment processing failed. Please try again.';
      setError(errorMsg);
      console.error(err);
    } finally {
      setProcessing(false);
    }
  };

  if (loading) {
    return (
      <div className="flex-center" style={{ minHeight: '100vh' }}>
        <div className="spinner"></div>
      </div>
    );
  }

  if (!ticket) {
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
    <div className="payment-page">
      <div className="container">
        <button className="btn btn-secondary btn-sm" onClick={() => navigate(-1)}>
          ← Back
        </button>

        <div className="payment-container">
          <div className="payment-form-section">
            <h2>Complete Your Purchase</h2>

            {error && <div className="alert alert-error">{error}</div>}
            {success && <div className="alert alert-success">{success}</div>}

            <form onSubmit={handleSubmit}>
              <div className="form-group">
                <label className="form-label">Seller Username</label>
                <input
                  type="text"
                  name="from"
                  className="form-input"
                  value={formData.from}
                  onChange={handleChange}
                  placeholder="Enter seller's username"
                  required
                />
                <small>The person who is selling this ticket</small>
              </div>

              <h4 style={{ marginTop: '2rem', marginBottom: '1rem' }}>
                Payment Information
              </h4>

              <div className="form-group">
                <label className="form-label">Card Number</label>
                <input
                  type="text"
                  name="cardNo"
                  className="form-input"
                  value={formData.cardNo}
                  onChange={handleChange}
                  placeholder="Enter 12-digit card number"
                  maxLength="12"
                  pattern="\d{12}"
                  required
                />
                <small>Must be exactly 12 digits</small>
              </div>

              <div className="form-group">
                <label className="form-label">CVV</label>
                <input
                  type="text"
                  name="cvv"
                  className="form-input"
                  value={formData.cvv}
                  onChange={handleChange}
                  placeholder="Enter 3-digit CVV"
                  maxLength="3"
                  pattern="\d{3}"
                  required
                />
                <small>3-digit security code on back of card</small>
              </div>

              <button
                type="submit"
                className="btn btn-primary btn-large"
                disabled={processing}
              >
                {processing ? 'Processing...' : `Pay ₹${ticket.price}`}
              </button>
            </form>
          </div>

          <div className="order-summary">
            <h3>Order Summary</h3>
            <div className="summary-item">
              <span className="label">Event:</span>
              <span className="value">{ticket.eventName}</span>
            </div>
            <div className="summary-item">
              <span className="label">Type:</span>
              <span className="value">{ticket.eventType}</span>
            </div>
            <div className="summary-item">
              <span className="label">Date:</span>
              <span className="value">{ticket.date}</span>
            </div>
            <div className="summary-item">
              <span className="label">Location:</span>
              <span className="value">{ticket.location}</span>
            </div>
            <div className="summary-item">
              <span className="label">Stand:</span>
              <span className="value">{ticket.stand}</span>
            </div>
            <hr />
            <div className="summary-item total">
              <span className="label">Total:</span>
              <span className="value">₹{ticket.price}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

