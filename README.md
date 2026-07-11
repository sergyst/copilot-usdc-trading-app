# USDC Trading Application

A full-stack application for buying, selling, and managing USDC tokens on the Ethereum blockchain.

## Tech Stack

### Backend
- **Framework**: Spring Boot 3.x
- **Language**: Java 17+
- **Build**: Maven
- **APIs**: RESTful APIs for USDC operations
- **Integration**: Circle API for wallet and token management
- **Blockchain**: Web3j for Ethereum interactions

### Frontend
- **Framework**: React 18+
- **Language**: TypeScript
- **Build**: Vite
- **Styling**: Tailwind CSS
- **Web3**: ethers.js for blockchain interactions
- **State Management**: Redux Toolkit

## Project Structure

```
copilot-usdc-trading-app/
├── backend/              # Spring Boot application
│   ├── src/
│   ├── pom.xml
│   └── Dockerfile
├── frontend/             # React application
│   ├── src/
│   ├── package.json
│   └── Dockerfile
├── docker-compose.yml
└── README.md
```

## Features

- ✅ User authentication & authorization
- ✅ Wallet management (create, import, connect MetaMask)
- ✅ USDC balance tracking
- ✅ Buy USDC (via Circle API)
- ✅ Sell USDC (via Circle API)
- ✅ Transfer USDC between wallets
- ✅ Transaction history & analytics
- ✅ Real-time balance updates
- ✅ Gas fee estimation

## Getting Started

### Prerequisites
- Java 17 or higher
- Node.js 18 or higher
- Docker & Docker Compose (optional)
- Circle API Key
- Ethereum wallet (MetaMask)

### Environment Setup

1. Clone the repository
2. Set up backend environment variables (see `backend/.env.example`)
3. Set up frontend environment variables (see `frontend/.env.example`)

### Running Locally

#### Backend
```bash
cd backend
mvn clean install
mvn spring-boot:run
```

#### Frontend
```bash
cd frontend
npm install
npm run dev
```

### Running with Docker
```bash
docker-compose up
```

## API Documentation

### Authentication
- `POST /api/auth/register` - Register a new user
- `POST /api/auth/login` - Login user
- `POST /api/auth/logout` - Logout user

### Wallets
- `GET /api/wallets` - Get all user wallets
- `POST /api/wallets` - Create new wallet
- `GET /api/wallets/{id}` - Get wallet details
- `GET /api/wallets/{id}/balance` - Get USDC balance

### Transactions
- `POST /api/transactions/buy` - Buy USDC
- `POST /api/transactions/sell` - Sell USDC
- `POST /api/transactions/transfer` - Transfer USDC
- `GET /api/transactions` - Get transaction history
- `GET /api/transactions/{id}` - Get transaction details

### Circle Integration
- `POST /api/circle/wallets` - Create Circle wallet
- `GET /api/circle/wallets/{id}` - Get wallet details
- `POST /api/circle/transfers` - Transfer USDC via Circle

## Circle API Integration

The application uses Circle's API for:
- Wallet creation and management
- USDC transfers
- Balance inquiries
- Payment processing

Circle API Documentation: https://developers.circle.com/

## Configuration

### Backend Configuration
Update `application.yml` with:
```yaml
circle:
  api-key: ${CIRCLE_API_KEY}
  api-url: ${CIRCLE_API_URL}
  
ethernet:
  rpc-url: ${ETHEREUM_RPC_URL}
  chain-id: 1
  
spring:
  datasource:
    url: ${DB_URL}
    username: ${DB_USER}
    password: ${DB_PASSWORD}
```

### Frontend Configuration
Update `.env` with:
```
VITE_CIRCLE_API_KEY=your_circle_api_key
VITE_ETHEREUM_RPC_URL=your_ethereum_rpc_url
VITE_BACKEND_URL=http://localhost:8080
```

## Development Guidelines

- Follow Spring Boot best practices
- Use DTOs for API requests/responses
- Implement proper error handling
- Add comprehensive logging
- Write unit and integration tests
- Use Git workflow with feature branches

## Security Considerations

- Never expose private keys
- Use HTTPS in production
- Implement rate limiting
- Validate all user inputs
- Use secure session management
- Implement transaction signing
- Audit all blockchain transactions

## Testing

```bash
# Backend tests
cd backend
mvn test

# Frontend tests
cd frontend
npm test
```

## Deployment

See deployment guides:
- Backend: `backend/DEPLOYMENT.md`
- Frontend: `frontend/DEPLOYMENT.md`

## License

MIT

## Contributing

Contributions are welcome! Please follow the development guidelines.
