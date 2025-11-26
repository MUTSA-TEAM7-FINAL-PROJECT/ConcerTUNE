package com.team7.ConcerTUNE.repository;

import com.team7.ConcerTUNE.entity.PaymentHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, Long> {
    List<PaymentHistory> findBySubscriptionIdOrderByPaymentDateDesc(Long subscriptionId);

}