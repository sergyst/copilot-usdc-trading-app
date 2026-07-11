package com.usdctrading.repository;

import com.usdctrading.entity.Transaction;
import com.usdctrading.entity.User;
import com.usdctrading.entity.Wallet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Page<Transaction> findByUser(User user, Pageable pageable);
    Page<Transaction> findByWallet(Wallet wallet, Pageable pageable);
    List<Transaction> findByTransactionHash(String transactionHash);
    Page<Transaction> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);
}
