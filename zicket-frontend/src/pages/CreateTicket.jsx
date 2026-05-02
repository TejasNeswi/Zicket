import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { ticketAPI } from '../services/api';
import './CreateTicket.css';

export const CreateTicket = () => {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    eventName: '',
    eventType: 'CONCERT',
    date: '',
    location: '',
    stand: '',
    price: '',
  });
  const [file, setFile] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((current) => ({
      ...current,
      [name]: value,
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');

    const hasEmptyField = Object.values(formData).some((value) => !String(value).trim());
    if (hasEmptyField) {
      setError('Please fill in all ticket details.');
      return;
    }

    setLoading(true);

    try {
      const response = await ticketAPI.createTicket(formData);
      const createdTicket = response.data;

      if (file && createdTicket?.ticketId) {
        await ticketAPI.uploadTicketFile(createdTicket.ticketId, file);
      }

      setSuccess('Ticket listed successfully.');
      setTimeout(() => {
        navigate('/my-tickets');
      }, 900);
    } catch (err) {
      setError(err.response?.data || 'Could not create ticket. Try a different stand if this one already exists.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="create-ticket-page">
      <div className="container">
        <button className="btn btn-secondary btn-sm" onClick={() => navigate('/my-tickets')}>
          Back to My Tickets
        </button>

        <div className="create-ticket-container">
          <div className="create-ticket-form">
            <h1>List a Ticket</h1>
            <p className="subtitle">Create a ticket listing that other users can buy.</p>

            {error && <div className="alert alert-error">{error}</div>}
            {success && <div className="alert alert-success">{success}</div>}

            <form onSubmit={handleSubmit}>
              <div className="form-group">
                <label className="form-label">Event Name</label>
                <input
                  type="text"
                  name="eventName"
                  className="form-input"
                  value={formData.eventName}
                  onChange={handleChange}
                  placeholder="Coldplay Live"
                  required
                />
              </div>

              <div className="form-grid">
                <div className="form-group">
                  <label className="form-label">Event Type</label>
                  <select
                    name="eventType"
                    className="form-select"
                    value={formData.eventType}
                    onChange={handleChange}
                    required
                  >
                    <option value="CONCERT">Concert</option>
                    <option value="SPORTS">Sports</option>
                    <option value="THEATER">Theater</option>
                    <option value="COMEDY">Comedy</option>
                  </select>
                </div>

                <div className="form-group">
                  <label className="form-label">Date</label>
                  <input
                    type="date"
                    name="date"
                    className="form-input"
                    value={formData.date}
                    onChange={handleChange}
                    required
                  />
                </div>
              </div>

              <div className="form-grid">
                <div className="form-group">
                  <label className="form-label">Location</label>
                  <input
                    type="text"
                    name="location"
                    className="form-input"
                    value={formData.location}
                    onChange={handleChange}
                    placeholder="Bangalore"
                    required
                  />
                </div>

                <div className="form-group">
                  <label className="form-label">Stand / Seat</label>
                  <input
                    type="text"
                    name="stand"
                    className="form-input"
                    value={formData.stand}
                    onChange={handleChange}
                    placeholder="A12"
                    required
                  />
                </div>
              </div>

              <div className="form-group">
                <label className="form-label">Price</label>
                <input
                  type="number"
                  name="price"
                  className="form-input"
                  value={formData.price}
                  onChange={handleChange}
                  placeholder="4500"
                  min="1"
                  required
                />
              </div>

              <div className="form-group">
                <label className="form-label">Ticket File</label>
                <input
                  type="file"
                  className="form-input"
                  onChange={(e) => setFile(e.target.files?.[0] || null)}
                />
                <small>Optional. Upload a PDF or image after the listing is created.</small>
              </div>

              <button type="submit" className="btn btn-primary btn-large" disabled={loading}>
                {loading ? 'Listing Ticket...' : 'List Ticket'}
              </button>
            </form>
          </div>
        </div>
      </div>
    </div>
  );
};
