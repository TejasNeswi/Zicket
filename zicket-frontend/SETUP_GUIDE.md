# 🎫 Zicket React Frontend - Setup & Usage Guide

Complete guide to running the Zicket ticket reselling platform frontend.

## Quick Start

```bash
# 1. Navigate to frontend directory
cd zicket-frontend

# 2. Install dependencies
npm install

# 3. Start development server
npm start

# Frontend opens at http://localhost:3000
```

## Prerequisites

- **Node.js** v14 or higher
- **npm** v6 or higher
- **Backend API** running on http://localhost:8080

## Project Structure

### Components (Reusable)
- `Auth.jsx` - Login & Signup forms
- `Navbar.jsx` - Navigation bar
- `TicketCard.jsx` - Reusable ticket card
- `ProtectedRoute.jsx` - Auth guard

### Pages (Full pages)
- `Home.jsx` - Browse & filter tickets
- `TicketDetail.jsx` - View ticket details
- `Payment.jsx` - Checkout page
- `MyTickets.jsx` - Seller's tickets
- `PurchasedTickets.jsx` - Buyer's tickets
- `Profile.jsx` - User profile & settings

### Core Files
- `App.jsx` - Main app with routing
- `AuthContext.js` - Global auth state
- `api.js` - API client with interceptors
- `index.css` - Global styles

## Available Routes

### Public Routes
- `/login` - Login page
- `/signup` - Registration page

### Protected Routes (Require Login)
- `/home` - Browse all tickets
- `/ticket/:ticketId` - View ticket details
- `/payment/:ticketId` - Payment checkout
- `/my-tickets` - Your tickets for sale
- `/purchased-tickets` - Your purchased tickets
- `/profile` - Profile & settings

## Configuration

### Environment Variables

Create `.env` file in project root:
```
REACT_APP_API_URL=http://localhost:8080
```

**Note**: Backend URL must match your Spring Boot server location.

## Features

### 1. Authentication
- Sign up with email validation
- Sign in with JWT token
- Persistent login (localStorage)
- Auto-logout on token expiry
- Protected routes

### 2. Ticket Browsing
- View all available tickets
- Filter by event type:
  - 🎵 Concert
  - ⚽ Sports
  - 🎭 Theater
  - 🎪 Comedy
- Responsive grid layout
- Ticket cards with key info

### 3. Ticket Purchase
- View ticket details
- Review ticket information
- Proceed to checkout
- Enter payment details
- Complete purchase
- Receive confirmation

### 4. Ticket Management
- View tickets you're selling
- View tickets you purchased
- Download ticket files
- Delete your listings

### 5. Profile Management
- View account information
- Edit username/password
- Delete account
- Logout

## API Integration

### Login Flow
```
User Input → Login Form
    ↓
POST /public/login (username, password)
    ↓
Backend: Authenticate user
    ↓
Response: JWT token
    ↓
Store in localStorage
    ↓
Redirect to /home
```

### Payment Flow
```
User Input → Payment Form
    ↓
POST /payments/{ticketId}
    ├─ seller username
    ├─ card number (12 digits)
    └─ CVV (3 digits)
    ↓
Backend: Validate & process
    ├─ Check payment details
    ├─ Verify seller owns ticket
    ├─ Save payment record
    ├─ Transfer ticket ownership
    └─ Send confirmation email
    ↓
Response: Success
    ↓
Redirect to /purchased-tickets
```

## Styling

### CSS Design System
```css
/* Colors */
--primary-color: #6366f1 (Indigo)
--secondary-color: #8b5cf6 (Purple)
--success-color: #10b981 (Green)
--error-color: #ef4444 (Red)

/* Spacing */
1rem, 2rem, 3rem, etc.

/* Responsive */
Mobile: < 480px
Tablet: 480px - 768px
Desktop: > 768px
```

### Utility Classes
```html
<!-- Buttons -->
<button class="btn btn-primary">Primary</button>
<button class="btn btn-secondary">Secondary</button>
<button class="btn btn-danger">Danger</button>
<button class="btn btn-sm">Small</button>

<!-- Layout -->
<div class="grid">...</div>
<div class="grid-cols-2">...</div>
<div class="flex-center">...</div>

<!-- Spacing -->
<div class="mt-2 mb-3 p-4">...</div>

<!-- Text -->
<p class="text-center text-muted">...</p>
```

## Available Scripts

```bash
# Start development server
npm start

# Build for production
npm run build

# Run tests (if configured)
npm test

# Eject configuration (one-way operation)
npm run eject
```

## Component Examples

### Using TicketCard
```jsx
<TicketCard 
  ticket={ticketObject}
  onAction={handleBuyClick}
  actionLabel="Buy"
/>
```

### Using ProtectedRoute
```jsx
<ProtectedRoute>
  <YourPage />
</ProtectedRoute>
```

### Using API
```jsx
import { ticketAPI } from '../services/api';

const response = await ticketAPI.getMyTickets();
```

## Error Handling

### Common Errors

**"Cannot find module"**
- Run `npm install` again
- Delete `node_modules` and reinstall

**"API connection error"**
- Verify backend is running on port 8080
- Check `.env` has correct API URL
- Restart dev server after changing `.env`

**"Unauthorized (401)"**
- Token expired (auto-logout)
- Login again
- Check localStorage for token

**"Payment validation error"**
- Card number must be 12 digits
- CVV must be 3 digits
- Seller username must be valid

## Best Practices

### When Adding Features
1. Create in `/components` or `/pages`
2. Add CSS file for styling
3. Import from `AuthContext` for auth
4. Use API client from `api.js`
5. Handle loading and error states

### Component Template
```jsx
import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import './ComponentName.css';

export const ComponentName = () => {
  const [state, setState] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    // Fetch data
  }, []);

  return (
    <div className="component-name">
      {/* JSX */}
    </div>
  );
};
```

## Deployment

### Netlify
```bash
npm run build
# Drag & drop /build folder to Netlify
```

### Vercel
```bash
npm install -g vercel
vercel
```

### GitHub Pages
```bash
npm install --save-dev gh-pages
# Add to package.json:
"homepage": "https://username.github.io/repo"
"deploy": "npm run build && gh-pages -d build"
```

## Performance Tips

1. **Lazy Load Routes** (already done with React Router)
2. **Minimize Bundle** - Only import what you need
3. **Optimize Images** - Compress before uploading
4. **Cache API Responses** - Implement in context if needed
5. **Use Production Build** - `npm run build` for deployment

## Security Notes

- JWT tokens stored in localStorage (consider httpOnly in production)
- API calls automatically include auth token
- Forms validated on client & server
- Sensitive data never logged
- CORS configured on backend

## Troubleshooting

| Problem | Solution |
|---------|----------|
| Port 3000 in use | Kill process or use `PORT=3001 npm start` |
| CORS error | Ensure backend CORS allows frontend URL |
| Blank page | Check browser console for errors |
| API not responding | Verify backend is running |
| Token not persisting | Check localStorage is enabled |

## Resources

- React Documentation: https://react.dev
- React Router: https://reactrouter.com
- Axios: https://axios-http.com
- CSS Tips: https://web.dev

## Support

For issues:
1. Check console for error messages
2. Review API responses in Network tab
3. Verify backend is running
4. Check environment variables
5. Review README.md for additional info

---

**Version**: 1.0.0 | Last Updated: 2024

