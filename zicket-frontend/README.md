# Zicket Frontend - React Application

A modern, interactive React frontend for the Zicket ticket reselling platform.

## 📁 Project Structure

```
zicket-frontend/
├── public/                 # Static files
│   └── index.html         # HTML template
├── src/
│   ├── components/        # Reusable components
│   │   ├── Auth.jsx       # Login & Signup components
│   │   ├── Auth.css
│   │   ├── Navbar.jsx     # Navigation bar
│   │   ├── Navbar.css
│   │   ├── TicketCard.jsx # Ticket display card
│   │   ├── TicketCard.css
│   │   └── ProtectedRoute.jsx # Auth guard
│   ├── context/           # React Context
│   │   └── AuthContext.js # Auth state management
│   ├── pages/             # Page components
│   │   ├── Home.jsx       # Browse tickets
│   │   ├── Home.css
│   │   ├── TicketDetail.jsx # Ticket details
│   │   ├── TicketDetail.css
│   │   ├── Payment.jsx    # Payment processing
│   │   ├── Payment.css
│   │   ├── MyTickets.jsx  # User's tickets for sale
│   │   ├── MyTickets.css
│   │   ├── PurchasedTickets.jsx # Bought tickets
│   │   ├── PurchasedTickets.css
│   │   ├── Profile.jsx    # User profile
│   │   └── Profile.css
│   ├── services/          # API communication
│   │   └── api.js         # Axios API client
│   ├── styles/            # Global styles
│   │   └── index.css      # Global CSS
│   ├── App.jsx            # Main app component
│   └── index.js           # Entry point
├── .env                   # Environment variables
├── package.json           # Dependencies
└── README.md
```

## 🚀 Getting Started

### Prerequisites
- Node.js (v14+)
- npm or yarn
- Backend API running on `http://localhost:8080`

### Installation

1. **Navigate to the frontend directory**
   ```bash
   cd zicket-frontend
   ```

2. **Install dependencies**
   ```bash
   npm install
   ```

3. **Configure environment variables**
   
   Edit `.env` file:
   ```
   REACT_APP_API_URL=http://localhost:8080
   ```

4. **Start development server**
   ```bash
   npm start
   ```

   The app will open at `http://localhost:3000`

5. **Build for production**
   ```bash
   npm run build
   ```

## 📱 Features

### 1. **Authentication**
- User Signup with email validation
- User Login with JWT token
- Protected routes for authenticated users
- Auto-logout on token expiry

### 2. **Home/Browse**
- View all available tickets
- Filter by event type (Concert, Sports, Theater, Comedy)
- Search and browse functionality
- Responsive grid layout

### 3. **Ticket Details**
- View full ticket information
- Event details (date, location, stand)
- Price display
- Purchase button

### 4. **Payment**
- Secure payment form
- Seller username input
- Card details (12-digit card number, 3-digit CVV)
- Order summary
- Payment processing

### 5. **User Dashboard**
- **My Tickets**: View tickets you're selling
- **Purchased Tickets**: View tickets you bought
- Download/view ticket attachments

### 6. **Profile Management**
- View profile information
- Edit username and password
- Delete account
- Logout

## 🔑 Key Components

### API Service (`src/services/api.js`)
Centralized API client with:
- JWT token injection in headers
- Request/response interceptors
- Error handling
- Separate modules for public, user, ticket, payment, and admin endpoints

### Authentication Context (`src/context/AuthContext.js`)
- Global auth state management
- Signup/login/logout functions
- Token persistence in localStorage
- Protected route validation

### Protected Route Component
- Redirects to login if not authenticated
- Shows loading spinner while checking auth
- Guards all user-specific pages

## 🎨 Styling

The application uses a custom CSS design system with:
- **Color Scheme**: Purple/Indigo primary colors
- **Responsive Design**: Mobile-first approach
- **Utility Classes**: Flexbox, grid, spacing utilities
- **Component-Scoped Styles**: Each component has its own CSS file

### CSS Variables
```css
--primary-color: #6366f1
--secondary-color: #8b5cf6
--success-color: #10b981
--error-color: #ef4444
--dark-bg: #1f2937
--light-bg: #f9fafb
```

## 🔄 Data Flow

### User Registration & Login
```
Login Component
    ↓
AuthContext.login()
    ↓
API → /public/login
    ↓
JWT Token stored in localStorage
    ↓
User redirected to /home
```

### Browse & Purchase Tickets
```
Home Component
    ↓
API → /public/getAllTickets()
    ↓
Display tickets in grid
    ↓
User clicks ticket → TicketDetail page
    ↓
User clicks "Buy" → Payment page
    ↓
API → /payments/{ticketId}
    ↓
Success → /purchased-tickets
```

### File Download
```
PurchasedTickets Component
    ↓
User clicks "Download"
    ↓
API → /ticket/view-ticket/{ticketId}
    ↓
Blob response → Browser download
```

## 📡 API Endpoints Used

### Public
- `POST /public/signup` - Register new user
- `POST /public/login` - Authenticate user
- `GET /public` - Get all tickets
- `GET /public/get-concert-tickets` - Get concert events
- `GET /public/get-sports-tickets` - Get sports events
- `GET /public/get-comedy-tickets` - Get comedy events
- `GET /public/get-theater-tickets` - Get theater events

### Authenticated
- `GET /ticket/get-my-tickets` - User's tickets for sale
- `GET /ticket/get-purchased-tickets` - User's purchased tickets
- `GET /ticket/get-event-info/{ticketId}` - Ticket details
- `POST /ticket` - Create new ticket
- `PUT /ticket/id/{ticketId}` - Update ticket
- `DELETE /ticket/id/{ticketId}` - Delete ticket
- `GET /ticket/view-ticket/{ticketId}` - Download ticket file
- `POST /payments/{ticketId}` - Process payment
- `PUT /user` - Update profile
- `DELETE /user` - Delete account

## ⚙️ Configuration

### Environment Variables
- `REACT_APP_API_URL` - Backend API base URL (default: http://localhost:8080)

### CORS
The backend must allow CORS requests from the frontend origin.

## 🐛 Troubleshooting

### API Connection Issues
1. Verify backend is running on `http://localhost:8080`
2. Check `.env` file has correct `REACT_APP_API_URL`
3. Restart dev server after changing `.env`

### Authentication Issues
1. Clear browser localStorage
2. Clear browser cookies
3. Check backend JWT secret is consistent

### Payment Errors
- Card number must be exactly 12 digits
- CVV must be exactly 3 digits
- Seller username must exist

## 📦 Dependencies

- **react**: ^18.2.0 - UI library
- **react-dom**: ^18.2.0 - DOM rendering
- **react-router-dom**: ^6.0.0 - Client-side routing
- **axios**: ^1.6.0 - HTTP client

## 🚦 Development Workflow

1. **Run Backend**
   ```bash
   cd Zicket
   ./mvnw spring-boot:run
   ```

2. **Run Frontend**
   ```bash
   cd zicket-frontend
   npm start
   ```

3. **Access Application**
   - Frontend: http://localhost:3000
   - Backend API: http://localhost:8080

## 📝 Notes

- JWT tokens expire after 60 seconds (set in backend)
- Tokens are stored in localStorage for persistence
- All API calls include Authorization header automatically
- File downloads are handled as blob responses
- Payment information is validated on both client and server

## 🔒 Security Considerations

- JWT tokens stored in localStorage (consider using httpOnly cookies for production)
- Password fields are never logged
- Sensitive API calls require authentication
- CORS must be properly configured
- Input validation on both client and server

---

**Built with React 18, React Router 6, and Axios**

