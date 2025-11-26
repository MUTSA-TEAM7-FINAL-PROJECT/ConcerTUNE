package com.team7.ConcerTUNE.temp.repository;

import com.team7.ConcerTUNE.entity.Artist;
import com.team7.ConcerTUNE.temp.entity.DonationSubscription;
import com.team7.ConcerTUNE.temp.entity.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
