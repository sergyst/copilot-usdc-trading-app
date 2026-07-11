package com.usdctrading.repository;

import com.usdctrading.entity.Order;
import com.usdctrading.entity.User;
import com.usdctrading.entity.Wallet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findByUser(User user, Pageable pageable);
    Page<Order> findByWallet(Wallet wallet, Pageable pageable);
    List<Order> findByUserOrderByCreatedAtDesc(User user);
    Page<Order> findByUserAndOrderTypeOrderByCreatedAtDesc(User user, Order.OrderType orderType, Pageable pageable);
}
