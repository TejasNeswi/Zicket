# 🎫 Zicket Frontend - Sequential Build Guide

This document explains how the React frontend was built sequentially to match the backend logic.

## Build Sequence Overview

```
1. API Service Layer
   ↓
2. Authentication Context
   ↓
3. Global Styles & Utilities
   ↓
4. Auth Components (Login/Signup)
   ↓
5. Navigation Component
   ↓
6. Protected Route Guard
   ↓
7. Reusable Components (TicketCard)
   ↓
8. Pages (Home, Details, Payment, etc.)
   ↓
9. Main App Router
```

---

## 📋 Step-by-Step Build Analysis

### Step 1: API Service Layer (`src/services/api.js`)

**Why First?**
- Foundation for all data communication
- Other components depend on this

**Key Logic:**
```javascript
// Intercepts all requests to add JWT token
apiClient.interceptors.request.use((config) => {
  const token = localStorage.getItem('authToken');
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

// Intercepts 401 responses (token expired)
apiClient.interceptors.response.use(
  response => response,
  error => {
    if (error.response?.status === 401) {
      // Clear auth and redirect to login
      localStorage.removeItem('authToken');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);
```

**Backend Mapping:**
- Groups endpoints by module (public, user, ticket, payment, admin)
- Matches backend controller structure

---

### Step 2: Authentication Context (`src/context/AuthContext.js`)

**Why Early?**
- Manages global auth state
- Required by ProtectedRoute and Navbar

**Key Logic:**
```javascript
// Signup flow
signup() → publicAPI.signup() → success → auto-login()

// Login flow
login() → publicAPI.login() → receive JWT → store in localStorage → set user state

// Logout flow
logout() → clear localStorage → clear state → redirect to login
```

**Backend Mapping:**
```
Frontend: signup(username, email, password)
Backend: POST /public/signup → saves user

Frontend: login(username, password)
Backend: POST /public/login → validates → returns JWT (60-second expiry)
```

---

### Step 3: Global Styles

**CSS Design System:**
- Color variables for consistency
- Utility classes (btn, card, alert, grid, etc.)
- Responsive breakpoints
- Reusable component patterns

---

### Step 4: Auth Components

**Login Component Flow:**
```
User enters username/password
    ↓
handleSubmit() → authContext.login()
    ↓
API POST /public/login → backend authenticates
    ↓
Success: JWT token returned
    ↓
Store token in localStorage
    ↓
Redirect to /home
```

**Signup Component Flow:**
```
User enters credentials
    ↓
Validation: passwords match, length ≥ 6
    ↓
handleSubmit() → authContext.signup()
    ↓
API POST /public/signup → backend creates user
    ↓
Auto-login with same credentials
    ↓
Redirect to /home
```

**Backend Mapping:**
```
Frontend signup → POST /public/signup
  Request: { username, email, password }
  Response: HTTP 201

Frontend login → POST /public/login  
  Request: { username, password }
  Response: JWT token string (HTTP 200)
  OR HTTP 400 for invalid credentials
```

---

### Step 5: Navigation Component

**Purpose:** Route between pages, show/hide links based on auth state

**Key Logic:**
```javascript
if (isAuthenticated()) {
  // Show: Browse Events, My Tickets, Purchased, Profile, Logout
} else {
  // Show: Sign In, Sign Up
}
```

---

### Step 6: Protected Route Component

**Purpose:** Guard routes that require authentication

**Flow:**
```javascript
if (loading) → show spinner
if (!isAuthenticated()) → redirect to /login
else → render children (protected page)
```

---

### Step 7: Ticket Card Component

**Reusable ticket display**

**Maps to Backend Ticket Entity:**
```javascript
{
  ticketId: "000001",           // String ID
  eventName: "Concert XYZ",
  eventType: "CONCERT",          // ENUM
  date: "2024-06-15",
  location: "City Hall",
  stand: "A1",
  price: "500",
  status: true,                  // Available?
  time: "2024-04-19T10:30:00",  // LocalDateTime
  fileId: "60e5ec33..."          // GridFS reference
}
```

---

### Step 8: Pages

#### **Home Page (`/home`)**

**Backend Logic Flow:**
```
GET /public/getAllTickets()
  ↓
TicketRepository.findAll() → MongoDB "tickets" collection
  ↓
Returns List<Ticket>
```

**Frontend Implementation:**
```javascript
useEffect(() => fetchTickets(), [])

fetchTickets() → publicAPI.getAllTickets()
  ↓
Store response in tickets state
  ↓
Render TicketCard for each ticket in grid
```

**Filtering:**
- Concert: `GET /public/get-concert-tickets`
- Sports: `GET /public/get-sports-tickets`
- Theater: `GET /public/get-theater-tickets`
- Comedy: `GET /public/get-comedy-tickets`

---

#### **Ticket Detail Page (`/ticket/:ticketId`)**

**Backend Logic:**
```
GET /ticket/get-event-info/{ticketId}
  ↓
TicketRepository.findById(ticketId)
  ↓
Returns Optional<Ticket>
```

**Frontend:**
```javascript
useParams() → extract ticketId
  ↓
ticketAPI.getTicketInfo(ticketId)
  ↓
Display ticket information
  ↓
Show "Buy This Ticket" button
```

---

#### **Payment Page (`/payment/:ticketId`)**

**Backend Logic (Complex Flow):**
```
POST /payments/{ticketId}
  Request body: { from, to, cardNo, cvv, timestamp }
  ↓
PaymentService.savePaymentInfo():
  1. Validate: cardNo.length == 12 && cvv.length == 3
  2. UserService.findByUsername(from) → get seller
  3. TicketRepository.findById(ticketId) → get ticket
  4. Validate: seller owns ticket && ticket.status == true
  5. PaymentRepository.save(payment)
  6. UserRepository.save(buyer with payment added)
  ↓
TicketService.transferTicket(from, to, ticketId):
  1. Get seller and buyer from UserRepository
  2. seller.myTickets.remove(ticket)
  3. buyer.purchasedTickets.add(ticket)
  4. ticket.status = false (mark as sold)
  5. Save all changes to MongoDB
  6. Send email confirmation
  ↓
Response: HTTP 202 ACCEPTED
```

**Frontend:**
```javascript
Form fields:
  - Seller Username (payment.from)
  - Card Number (payment.cardNo) - 12 digits
  - CVV (payment.cvv) - 3 digits

Validation:
  - cardNo.length === 12
  - cvv.length === 3
  - from username not empty

handleSubmit():
  paymentAPI.processPayment(ticketId, {
    from: sellerUsername,
    cardNo,
    cvv
  })
  ↓
  Success → redirect to /purchased-tickets
  Error → show error message
```

---

#### **My Tickets Page (`/my-tickets`)**

**Backend Logic:**
```
GET /ticket/get-my-tickets
  ↓
UserService.findByUsername(currentUser)
  ↓
UserRepository.findByUsername() → populates myTickets DBRef
  ↓
Returns List<Ticket> (user's created tickets)
```

**Frontend:**
```javascript
useEffect(() => fetchMyTickets(), [])

fetchMyTickets():
  ticketAPI.getMyTickets()
  ↓
Display list of tickets in grid
  ↓
Each card has "Delete" button

handleDeleteTicket():
  DELETE /ticket/id/{ticketId}
  ↓
Backend:
    - Verify user owns ticket
    - Delete GridFS file
    - Remove from myTickets list
    - Delete from database
  ↓
Remove from frontend list
```

---

#### **Purchased Tickets Page (`/purchased-tickets`)**

**Backend Logic:**
```
GET /ticket/get-purchased-tickets
  ↓
UserRepository.findByUsername() → populates purchasedTickets DBRef
  ↓
Returns List<Ticket> (user's bought tickets)
```

**Frontend:**
```javascript
Each purchased ticket has "Download" button

handleDownloadTicket(ticketId):
  GET /ticket/view-ticket/{ticketId}
  ↓
Backend:
    - Verify user has access (owns or purchased)
    - Get ticket.fileId (GridFS reference)
    - Retrieve file from GridFS
    - Return as blob with headers
  ↓
Create blob download link in browser
  ↓
Trigger file download
```

---

#### **Profile Page (`/profile`)**

**Backend Logic:**

Update Profile:
```
PUT /user
  Request: { username, password }
  ↓
UserService.findByUsername() → get current user
  ↓
Update fields
  ↓
BCryptPasswordEncoder.encode() → hash password
  ↓
UserRepository.save() → update in MongoDB
  ↓
Response: HTTP 204 NO_CONTENT
```

Delete Account:
```
DELETE /user
  ↓
UserService.deleteUser()
  ↓
UserRepository.delete() → remove from MongoDB
  ↓
Response: HTTP 204 NO_CONTENT
```

**Frontend:**
```javascript
Display user info:
  - Username
  - Avatar initial
  - Account status

Edit section:
  - Change username
  - Change password
  - Update → PUT /user
  
Delete account:
  - Confirmation dialog
  - DELETE /user
  - Clear auth & redirect to login
```

---

### Step 9: Main App Router

**Routing Structure:**

```javascript
Public Routes:
  /login → <Login />
  /signup → <Signup />

Protected Routes:
  /home → <ProtectedRoute><Home /></ProtectedRoute>
  /ticket/:ticketId → <ProtectedRoute><TicketDetail /></ProtectedRoute>
  /payment/:ticketId → <ProtectedRoute><Payment /></ProtectedRoute>
  /my-tickets → <ProtectedRoute><MyTickets /></ProtectedRoute>
  /purchased-tickets → <ProtectedRoute><PurchasedTickets /></ProtectedRoute>
  /profile → <ProtectedRoute><Profile /></ProtectedRoute>

Default:
  / → redirect to /home
  * → redirect to /home
```

---

## 🔄 Complete User Journey

### 1. New User
```
Landing on frontend
  ↓
Redirected to /login
  ↓
Click "Sign up" → /signup
  ↓
Enter: username, email, password
  ↓
POST /public/signup → backend creates user (BCrypt password)
  ↓
Auto-login: POST /public/login → get JWT
  ↓
Store JWT in localStorage
  ↓
Redirect to /home
```

### 2. Browse & Purchase
```
Home page:
  GET /public/getAllTickets() → display all tickets

Filter by type:
  GET /public/get-concert-tickets() → filtered list

Click ticket:
  GET /ticket/get-event-info/{ticketId} → details

Buy ticket:
  Redirect to payment page
  ↓
Enter: seller username, card #, CVV
  ↓
POST /payments/{ticketId}
    ├→ Validate payment
    ├→ Save payment record
    ├→ Transfer ticket (from seller to buyer)
    ├→ Update statuses
    └→ Send email confirmation
  ↓
Redirect to /purchased-tickets
```

### 3. View Purchased Tickets
```
/purchased-tickets:
  GET /ticket/get-purchased-tickets() → show my tickets

Download document:
  GET /ticket/view-ticket/{ticketId}
    ├→ Verify access
    ├→ Retrieve from GridFS
    └→ Return as blob
  ↓
Browser downloads file
```

---

## 🏗️ Component Dependencies

```
App (Routes & Auth)
├── AuthProvider (Auth state)
├── Navbar (Navigation)
├── ProtectedRoute (Guard)
│   ├── Home
│   │   └── TicketCard (multiple)
│   ├── TicketDetail
│   │   └── TicketCard
│   ├── Payment
│   ├── MyTickets
│   │   └── TicketCard (multiple)
│   ├── PurchasedTickets
│   │   └── TicketCard (multiple)
│   └── Profile
│
Auth Components
├── Login (public route)
└── Signup (public route)
```

---

## 📊 Data Flow Diagram

```
┌─────────────────────────────────────────────────────────┐
│          React Components                               │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐   │
│  │ Auth Pages   │  │ Home/Browse  │  │ Payment Page │   │
│  └──────────────┘  └──────────────┘  └──────────────┘   │
└────────┬──────────────────┬──────────────────┬───────────┘
         │                  │                  │
         └──────────────────┼──────────────────┘
                            │
                    ┌───────▼────────┐
                    │ API Service    │
                    │  (axios)       │
                    └───────┬────────┘
                            │
                  ┌─────────┴─────────┐
                  │                   │
        ┌─────────▼────────┐  ┌──────▼──────────┐
        │ JWT Token in     │  │ HTTP Requests  │
        │ localStorage     │  │ with headers   │
        └──────────────────┘  └────────┬────────┘
                                       │
                            ┌──────────▼──────────┐
                            │ Spring Boot Backend │
                            │ (Controllers)       │
                            └──────────┬──────────┘
                                       │
                            ┌──────────▼──────────┐
                            │ Services & Logic   │
                            └──────────┬──────────┘
                                       │
                            ┌──────────▼──────────┐
                            │ MongoDB & GridFS   │
                            └────────────────────┘
```

---

## ✅ Build Checklist

- [x] API Service with interceptors
- [x] Auth Context & hooks
- [x] Global styles & utilities
- [x] Login component
- [x] Signup component
- [x] Navbar with auth state
- [x] ProtectedRoute guard
- [x] TicketCard component
- [x] Home page with filtering
- [x] Ticket detail page
- [x] Payment page
- [x] My tickets page
- [x] Purchased tickets page
- [x] Profile management
- [x] App routing
- [x] Environment configuration

---

## 🚀 Ready to Run!

The frontend is now complete and ready to connect to the backend.

**Next Steps:**
1. Ensure backend is running (`./mvnw spring-boot:run`)
2. Navigate to `zicket-frontend`
3. Run `npm install` (if not done)
4. Run `npm start`
5. Open http://localhost:3000

---

**Architecture: React 18 + React Router 6 + Axios + Context API**

