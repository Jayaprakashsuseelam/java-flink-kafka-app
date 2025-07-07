# Order Processing System - Frontend

Angular frontend application for the Order Processing System with Kafka, Flink, and MongoDB integration.

## Features

- **Dashboard**: Overview of order statistics and recent orders
- **Enriched Orders**: Search and view orders processed through Flink stream
- **Order Events**: Send order events to Kafka for real-time processing
- **Responsive Design**: Bootstrap-based UI with modern styling

## Prerequisites

- **Node.js** (version 16 or higher)
- **npm** or **yarn**
- **Angular CLI** (version 15)

## Installation

1. **Install Angular CLI globally** (if not already installed):
   ```bash
   npm install -g @angular/cli@15
   ```

2. **Install dependencies**:
   ```bash
   npm install
   ```

## Development Server

Run the development server:

```bash
npm start
```

The application will be available at `http://localhost:4200`.

## Build for Production

Build the application for production:

```bash
npm run build
```

The build artifacts will be stored in the `dist/` directory.

## Project Structure

```
src/
├── app/
│   ├── components/
│   │   ├── dashboard/              # Dashboard overview
│   │   ├── enriched-order-list/    # MongoDB enriched orders
│   │   ├── order-event-form/       # Kafka order events
│   │   ├── order-list/             # Traditional orders (placeholder)
│   │   ├── order-detail/           # Order details (placeholder)
│   │   └── order-form/             # Order form (placeholder)
│   ├── models/
│   │   └── order.model.ts          # TypeScript interfaces
│   ├── services/
│   │   └── order.service.ts        # API service
│   ├── app.component.ts            # Main app component
│   ├── app.component.html          # Main app template
│   └── app.module.ts               # Main app module
├── assets/                         # Static assets
├── styles.css                      # Global styles
└── main.ts                         # Application entry point
```

## API Integration

The frontend communicates with the Spring Boot backend at `http://localhost:8085`:

### Endpoints Used:
- `GET /api/orders` - Get all orders
- `POST /api/orders/place-order` - Send order event to Kafka
- `GET /api/enriched-orders/{userId}` - Get enriched orders by user ID

## Key Components

### Dashboard Component
- Displays order statistics (total orders, revenue, status counts)
- Shows recent orders table
- Quick action buttons for navigation

### Enriched Order List Component
- Search enriched orders by user ID
- Displays orders processed through Flink stream
- Shows enriched data from MongoDB

### Order Event Form Component
- Form to send order events to Kafka
- Sample data generation
- Real-time processing pipeline integration

## Styling

- **Bootstrap 5**: Responsive grid system and components
- **Font Awesome**: Icons for better UX
- **Custom CSS**: Enhanced styling for cards, tables, and forms

## Browser Support

- Chrome (latest)
- Firefox (latest)
- Safari (latest)
- Edge (latest)

## Development Notes

- The application uses Angular 15 with TypeScript
- Reactive forms for data input
- HTTP interceptors for API communication
- Responsive design for mobile and desktop
- Error handling and loading states

## Troubleshooting

### Common Issues:

1. **CORS Errors**: Ensure the backend has CORS configured for `http://localhost:4200`
2. **API Connection**: Verify the backend is running on port 8085
3. **Dependencies**: Run `npm install` if you encounter module errors

### Port Configuration:
- Frontend: `http://localhost:4200`
- Backend: `http://localhost:8085`
- Kafka: `localhost:9092`
- MongoDB: `localhost:27017` 