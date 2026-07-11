package com.usdctrading.service;

import com.usdctrading.dto.BuyUsdcRequest;
import com.usdctrading.dto.SellUsdcRequest;
import com.usdctrading.dto.TransactionResponse;
import com.usdctrading.dto.OrderResponse;
import com.usdctrading.entity.*;
import com.usdctrading.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Slf4j
@Service
public class TradeService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CircleService circleService;

    @Autowired
    private EthereumService ethereumService;

    private static final BigDecimal FEE_PERCENTAGE = new BigDecimal("0.01"); // 1% fee
    private static final String USDC_CONTRACT = "0xA0b86991c6218b36c1d19D4a2e9Eb0cE3606eB48";

    /**
     * Buy USDC tokens
     * Creates a buy order and initiates transaction with Circle API
     */
    @Transactional
    public OrderResponse buyUsdc(Long userId, BuyUsdcRequest request) {
        log.info("Processing buy USDC request for user: {}", userId);

        // Validate user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Validate wallet
        Wallet wallet = walletRepository.findById(request.getWalletId())
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        if (!wallet.getUser().getId().equals(userId)) {
            throw new RuntimeException("Wallet does not belong to user");
        }

        // Calculate fees
        BigDecimal feeAmount = request.getAmount().multiply(FEE_PERCENTAGE);
        BigDecimal totalAmount = request.getAmount().add(feeAmount);

        // Create order
        Order order = Order.builder()
                .user(user)
                .wallet(wallet)
                .orderType(Order.OrderType.BUY)
                .quantity(request.getAmount())
                .pricePerUnit(request.getPricePerUnit())
                .totalAmount(totalAmount)
                .status(Order.OrderStatus.OPEN)
                .paymentMethod(request.getPaymentMethod())
                .build();

        Order savedOrder = orderRepository.save(order);
        log.info("Order created with ID: {}", savedOrder.getId());

        // Create transaction record
        Transaction transaction = Transaction.builder()
                .user(user)
                .wallet(wallet)
                .type(Transaction.TransactionType.BUY)
                .amount(request.getAmount())
                .pricePerUnit(request.getPricePerUnit())
                .totalValue(request.getAmount().multiply(request.getPricePerUnit()))
                .feeAmount(feeAmount)
                .status(Transaction.TransactionStatus.PENDING)
                .description(request.getDescription())
                .build();

        Transaction savedTransaction = transactionRepository.save(transaction);
        log.info("Transaction created with ID: {}", savedTransaction.getId());

        // Call Circle API to process payment
        try {
            String circleTransferId = processCircleTransfer(
                    wallet.getWalletAddress(),
                    request.getAmount(),
                    request.getPaymentMethod()
            );

            // Update transaction with Circle transfer ID
            savedTransaction.setTransactionHash(circleTransferId);
            savedTransaction.setStatus(Transaction.TransactionStatus.PROCESSING);
            transactionRepository.save(savedTransaction);

            // Update order status
            savedOrder.setStatus(Order.OrderStatus.PARTIALLY_FILLED);
            savedOrder.setFilledQuantity(request.getAmount());
            orderRepository.save(savedOrder);

            // Update wallet balance
            wallet.setUsdcBalance(wallet.getUsdcBalance().add(request.getAmount()));
            walletRepository.save(wallet);

            log.info("Buy USDC transaction completed successfully");
        } catch (Exception e) {
            log.error("Error processing Circle transfer", e);
            savedTransaction.setStatus(Transaction.TransactionStatus.FAILED);
            transactionRepository.save(savedTransaction);

            savedOrder.setStatus(Order.OrderStatus.FAILED);
            orderRepository.save(savedOrder);

            throw new RuntimeException("Failed to process buy order: " + e.getMessage());
        }

        return OrderResponse.fromEntity(savedOrder);
    }

    /**
     * Sell USDC tokens
     * Creates a sell order and initiates transaction with Circle API
     */
    @Transactional
    public OrderResponse sellUsdc(Long userId, SellUsdcRequest request) {
        log.info("Processing sell USDC request for user: {}", userId);

        // Validate user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Validate wallet
        Wallet wallet = walletRepository.findById(request.getWalletId())
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        if (!wallet.getUser().getId().equals(userId)) {
            throw new RuntimeException("Wallet does not belong to user");
        }

        // Check if wallet has sufficient balance
        if (wallet.getUsdcBalance().compareTo(request.getAmount()) < 0) {
            throw new RuntimeException("Insufficient USDC balance");
        }

        // Calculate fees
        BigDecimal feeAmount = request.getAmount().multiply(FEE_PERCENTAGE);
        BigDecimal netAmount = request.getAmount().subtract(feeAmount);

        // Create order
        Order order = Order.builder()
                .user(user)
                .wallet(wallet)
                .orderType(Order.OrderType.SELL)
                .quantity(request.getAmount())
                .pricePerUnit(request.getPricePerUnit())
                .totalAmount(netAmount)
                .status(Order.OrderStatus.OPEN)
                .build();

        Order savedOrder = orderRepository.save(order);
        log.info("Sell order created with ID: {}", savedOrder.getId());

        // Create transaction record
        Transaction transaction = Transaction.builder()
                .user(user)
                .wallet(wallet)
                .type(Transaction.TransactionType.SELL)
                .amount(request.getAmount())
                .pricePerUnit(request.getPricePerUnit())
                .totalValue(request.getAmount().multiply(request.getPricePerUnit()))
                .feeAmount(feeAmount)
                .status(Transaction.TransactionStatus.PENDING)
                .fromAddress(wallet.getWalletAddress())
                .toAddress(request.getRecipientAddress())
                .description(request.getDescription())
                .build();

        Transaction savedTransaction = transactionRepository.save(transaction);
        log.info("Sell transaction created with ID: {}", savedTransaction.getId());

        // Call Circle API to process transfer
        try {
            String circleTransferId = processCircleTransfer(
                    request.getRecipientAddress(),
                    request.getAmount(),
                    "BLOCKCHAIN"
            );

            // Update transaction
            savedTransaction.setTransactionHash(circleTransferId);
            savedTransaction.setStatus(Transaction.TransactionStatus.PROCESSING);
            transactionRepository.save(savedTransaction);

            // Update order status
            savedOrder.setStatus(Order.OrderStatus.PARTIALLY_FILLED);
            savedOrder.setFilledQuantity(request.getAmount());
            orderRepository.save(savedOrder);

            // Update wallet balance (deduct USDC)
            wallet.setUsdcBalance(wallet.getUsdcBalance().subtract(request.getAmount()));
            walletRepository.save(wallet);

            log.info("Sell USDC transaction completed successfully");
        } catch (Exception e) {
            log.error("Error processing Circle transfer for sell", e);
            savedTransaction.setStatus(Transaction.TransactionStatus.FAILED);
            transactionRepository.save(savedTransaction);

            savedOrder.setStatus(Order.OrderStatus.FAILED);
            orderRepository.save(savedOrder);

            throw new RuntimeException("Failed to process sell order: " + e.getMessage());
        }

        return OrderResponse.fromEntity(savedOrder);
    }

    /**
     * Get all transactions for a user
     */
    public Page<TransactionResponse> getUserTransactions(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return transactionRepository.findByUserOrderByCreatedAtDesc(user, pageable)
                .map(TransactionResponse::fromEntity);
    }

    /**
     * Get transaction details
     */
    public TransactionResponse getTransaction(Long transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        return TransactionResponse.fromEntity(transaction);
    }

    /**
     * Get all orders for a user
     */
    public Page<OrderResponse> getUserOrders(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return orderRepository.findByUser(user, pageable)
                .map(OrderResponse::fromEntity);
    }

    /**
     * Get order details
     */
    public OrderResponse getOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        return OrderResponse.fromEntity(order);
    }

    /**
     * Cancel an order
     */
    @Transactional
    public OrderResponse cancelOrder(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getUser().getId().equals(userId)) {
            throw new RuntimeException("Order does not belong to user");
        }

        if (!order.getStatus().equals(Order.OrderStatus.OPEN) &&
                !order.getStatus().equals(Order.OrderStatus.PARTIALLY_FILLED)) {
            throw new RuntimeException("Cannot cancel completed or failed order");
        }

        order.setStatus(Order.OrderStatus.CANCELLED);
        Order cancelledOrder = orderRepository.save(order);

        log.info("Order {} cancelled successfully", orderId);
        return OrderResponse.fromEntity(cancelledOrder);
    }

    /**
     * Process Circle API transfer
     * This is a placeholder - implement according to Circle API documentation
     */
    private String processCircleTransfer(String address, BigDecimal amount, String paymentMethod) {
        log.info("Processing Circle transfer to {} for amount {}", address, amount);
        // TODO: Implement actual Circle API call
        // For now, return a mock transfer ID
        return "circle_" + System.currentTimeMillis();
    }
}
