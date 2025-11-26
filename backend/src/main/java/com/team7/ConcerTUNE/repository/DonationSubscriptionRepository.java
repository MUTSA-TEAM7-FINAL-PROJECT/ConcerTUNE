package com.team7.ConcerTUNE.repository;

import com.team7.ConcerTUNE.entity.DonationSubscription;
import com.team7.ConcerTUNE.entity.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface DonationSubscriptionRepository extends JpaRepository<DonationSubscription, Long> {

    List<DonationSubscription> findByStatusAndNextPaymentDateLessThanEqual(
            SubscriptionStatus status,
            LocalDateTime time
    );

    Optional<DonationSubscription> findByUserIdAndArtistIdAndStatus(
            Long userId,
            Long artistId,
            SubscriptionStatus status
    );

    List<DonationSubscription> findByUserIdAndStatus(Long userId, SubscriptionStatus status);

    Optional<DonationSubscription> findByTossCustomerKey(String customerKey);

    Optional<DonationSubscription> findByUserIdAndArtistId(Long userId, Long artistId);


}
