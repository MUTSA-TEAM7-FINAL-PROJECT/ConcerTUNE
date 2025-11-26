package com.team7.ConcerTUNE.repository;

import com.team7.ConcerTUNE.entity.TossPaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TossPaymentTransactionRepository extends JpaRepository<TossPaymentTransaction, Long> {
    Optional<TossPaymentTransaction> findByOrderId(String orderId);

}
