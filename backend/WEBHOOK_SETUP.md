# Circle Webhook Setup Guide

## Overview
Webhooks allow Circle to notify your application in real-time about payment and transfer status changes.

## Supported Webhook Events

### Payment Events
- `payments.created` - Payment was created
- `payments.pending` - Payment is pending
- `payments.confirmed` - Payment confirmed successfully
- `payments.failed` - Payment failed

### Transfer Events
- `transfers.created` - Transfer was created
- `transfers.pending` - Transfer is pending
- `transfers.complete` - Transfer completed successfully
- `transfers.failed` - Transfer failed

### Wallet Events
- `wallets.created` - Wallet was created

## Setup Instructions

### 1. Get Your Webhook Secret
1. Log in to Circle Dashboard (https://dashboard.circle.com)
2. Navigate to Webhooks settings
3. Copy your Webhook Secret (keep it safe!)

### 2. Register Webhook Endpoint
1. In Circle Dashboard, add your webhook URL:
   - Sandbox: `https://your-app-domain.com/api/webhooks/circle`
   - Production: `https://your-production-domain.com/api/webhooks/circle`

2. Select events to subscribe to (recommended all)

3. Copy the Webhook Secret to your `.env` file:
   ```
   CIRCLE_WEBHOOK_SECRET=your_webhook_secret_from_circle_dashboard
   ```

### 3. Signature Validation
Circle signs each webhook with HMAC-SHA256. Our implementation validates signatures automatically.

Header: `X-Circle-Signature`
Algorithm: HMAC-SHA256
Key: Your Webhook Secret

## Webhook Flow

```
Circle API
    ↓
Sends POST request with payload
    ↓
WebhookController receives request
    ↓
Validates signature
    ↓
WebhookService processes event
    ↓
Updates Transaction/Order status
    ↓
Returns 200 OK
```

## Transaction Status Updates

### Buy USDC
```
User initiates buy
    ↓
Circle payment created → PAYMENT_CREATED webhook
    ↓
Payment pending → PAYMENT_PENDING webhook
    ↓
Payment confirmed → PAYMENT_CONFIRMED webhook
    ↓
Transfer created → TRANSFER_CREATED webhook
    ↓
Transfer complete → TRANSFER_COMPLETE webhook
    ↓
Transaction marked COMPLETED
```

### Sell USDC
```
User initiates sell
    ↓
Transfer created → TRANSFER_CREATED webhook
    ↓
Transfer pending → TRANSFER_PENDING webhook
    ↓
Transfer complete → TRANSFER_COMPLETE webhook
    ↓
Transaction marked COMPLETED
```

## Error Handling

### Webhook Failures
- Failed webhooks are automatically retried every 5 minutes
- Maximum of 3 retry attempts
- Failed webhooks can be monitored via:
  - GET `/api/webhooks/failed`
  - Manual retry: POST `/api/webhooks/retry`

### Idempotency
- Each webhook has a unique `eventId`
- Duplicate webhooks are ignored (idempotent)
- Prevents double-processing of events

## Testing Webhooks

### Using Circle API Sandbox
1. Create a test payment/transfer in sandbox
2. Monitor your logs for webhook events
3. Check webhook status in database:
   ```sql
   SELECT * FROM webhooks ORDER BY received_at DESC;
   ```

### Manual Testing
```bash
# Test webhook receipt
curl -X POST http://localhost:8080/api/webhooks/circle \
  -H "Content-Type: application/json" \
  -H "X-Circle-Signature: your_signature" \
  -d '{
    "id": "test-event-1",
    "type": "payments.confirmed",
    "version": 1,
    "timestamp": "2024-01-01T00:00:00Z",
    "data": {
      "id": "test-payment-1",
      "status": "confirmed",
      "amount": {"amount": "100", "currency": "USD"},
      "source": {"type": "card", "id": "card-1"},
      "destination": {"type": "wallet", "id": "wallet-1"}
    }
  }'
```

## Monitoring

### Check Webhook Status
```bash
GET /api/webhooks/failed
```

### Retry Failed Webhooks
```bash
POST /api/webhooks/retry
```

### View Webhook Details
```bash
GET /api/webhooks/{id}
```

## Best Practices

1. **Always validate signatures** - Prevents processing unauthorized webhooks
2. **Process idempotently** - Use eventId to prevent duplicate processing
3. **Respond quickly** - Return 200 OK immediately, process asynchronously
4. **Log everything** - Maintain audit trail of all webhook events
5. **Monitor failures** - Set up alerts for failed webhook processing
6. **Test in sandbox first** - Verify webhook setup before production

## Troubleshooting

### Webhooks not arriving
1. Check Circle Dashboard webhook configuration
2. Verify endpoint URL is publicly accessible
3. Check firewall/security group rules
4. Review Circle Dashboard activity logs

### Signature validation failures
1. Ensure CIRCLE_WEBHOOK_SECRET is correct
2. Check that raw request body is used for signature
3. Verify HMAC-SHA256 algorithm is used

### Webhook processing errors
1. Check application logs
2. Review failed webhooks: GET `/api/webhooks/failed`
3. Manual retry: POST `/api/webhooks/retry`
4. Check database for transaction/order records
