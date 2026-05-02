import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { userAPI } from '../services/api';
import './Profile.css';

export const Profile = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const [isEditing, setIsEditing] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const [formData, setFormData] = useState({
    username: user?.username || '',
    password: '',
    newPassword: '',
    confirmPassword: '',
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: value,
    });
  };

  const handleUpdateProfile = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');

    if (formData.newPassword !== formData.confirmPassword) {
      setError('New passwords do not match');
      return;
    }

    if (formData.newPassword && formData.newPassword.length < 6) {
      setError('New password must be at least 6 characters');
      return;
    }

    setLoading(true);

    try {
      const updateData = {
        username: formData.username,
        password: formData.newPassword || formData.password,
      };

      await userAPI.updateProfile(updateData);
      setSuccess('Profile updated successfully!');
      setFormData({
        ...formData,
        password: '',
        newPassword: '',
        confirmPassword: '',
      });
      setIsEditing(false);
    } catch (err) {
      setError(err.response?.data || 'Failed to update profile');
    } finally {
      setLoading(false);
    }
  };

  const handleDeleteAccount = async () => {
    if (
      !window.confirm(
        'Are you sure you want to delete your account? This action cannot be undone.'
      )
    ) {
      return;
    }

    setLoading(true);

    try {
      await userAPI.deleteAccount();
      setSuccess('Account deleted successfully');
      setTimeout(() => {
        logout();
        navigate('/login');
      }, 1500);
    } catch (err) {
      setError(err.response?.data || 'Failed to delete account');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="profile-page">
      <div className="container">
        <h1>👤 My Profile</h1>

        {error && <div className="alert alert-error">{error}</div>}
        {success && <div className="alert alert-success">{success}</div>}

        <div className="profile-grid">
          <div className="profile-card">
            <div className="profile-header">
              <div className="profile-avatar">
                {user?.username?.charAt(0).toUpperCase() || 'U'}
              </div>
              <div>
                <h2>{user?.username || 'User'}</h2>
                <p className="text-muted">Zicket Member</p>
              </div>
            </div>

            <div className="profile-info">
              <div className="info-item">
                <span className="label">Username</span>
                <span className="value">{user?.username || 'N/A'}</span>
              </div>
              <div className="info-item">
                <span className="label">Account Status</span>
                <span className="value status-active">✓ Active</span>
              </div>
              <div className="info-item">
                <span className="label">Member Since</span>
                <span className="value">
                  {new Date().toLocaleDateString()}
                </span>
              </div>
            </div>
          </div>

          <div className="actions-card">
            <h3>Account Actions</h3>
            <div className="actions-grid">
              <button
                className="btn btn-primary"
                onClick={() => setIsEditing(!isEditing)}
              >
                {isEditing ? '❌ Cancel' : '✏️ Edit Profile'}
              </button>
              <button className="btn btn-secondary" onClick={() => logout()}>
                🚪 Logout
              </button>
              <button
                className="btn btn-danger"
                onClick={handleDeleteAccount}
                disabled={loading}
              >
                🗑️ Delete Account
              </button>
            </div>
          </div>
        </div>

        {isEditing && (
          <div className="edit-section">
            <h3>Edit Your Profile</h3>
            <form onSubmit={handleUpdateProfile}>
              <div className="form-group">
                <label className="form-label">Username</label>
                <input
                  type="text"
                  name="username"
                  className="form-input"
                  value={formData.username}
                  onChange={handleChange}
                  required
                />
              </div>

              <div className="form-group">
                <label className="form-label">New Password (optional)</label>
                <input
                  type="password"
                  name="newPassword"
                  className="form-input"
                  value={formData.newPassword}
                  onChange={handleChange}
                  placeholder="Leave blank to keep current password"
                />
              </div>

              <div className="form-group">
                <label className="form-label">Confirm New Password</label>
                <input
                  type="password"
                  name="confirmPassword"
                  className="form-input"
                  value={formData.confirmPassword}
                  onChange={handleChange}
                  placeholder="Confirm new password"
                />
              </div>

              <button
                type="submit"
                className="btn btn-primary btn-large"
                disabled={loading}
              >
                {loading ? 'Updating...' : 'Update Profile'}
              </button>
            </form>
          </div>
        )}
      </div>
    </div>
  );
};

