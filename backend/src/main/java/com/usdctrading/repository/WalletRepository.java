package com.usdctrading.repository;

import com.usdctrading.entity.Wallet;
import com.usdctrading.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {
    List<Wallet> findByUser(User user);
    Optional<Wallet> findByWalletAddress(String walletAddress);
    Optional<Wallet> findByUserAndIsDefault(User user, Boolean isDefault);
}
