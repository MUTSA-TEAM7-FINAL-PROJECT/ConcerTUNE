package com.team7.ConcerTUNE.service;

import com.team7.ConcerTUNE.dto.ArtistDto;
import com.team7.ConcerTUNE.dto.SubscriptionCreateRequest;
import com.team7.ConcerTUNE.dto.SubscriptionDetailDto;
import com.team7.ConcerTUNE.dto.TossPaymentResponse;
import com.team7.ConcerTUNE.entity.*;
import com.team7.ConcerTUNE.repository.ArtistRepository;
import com.team7.ConcerTUNE.repository.DonationSubscriptionRepository;
import com.team7.ConcerTUNE.repository.PaymentHistoryRepository;
import com.team7.ConcerTUNE.repository.TossPaymentTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionService {

    private final TossPaymentsApiClient tossClient;
    private final DonationSubscriptionRepository subscriptionRepository;
    private final PaymentHistoryRepository paymentHistoryRepository;
    private final TossPaymentTransactionRepository transactionRepository;
    private final ArtistRepository artistRepository;

    @Transactional
    public String registerCustomer(Long userId, Long artistId) {
        String customerKey = "CUST-" + userId + "-" + System.currentTimeMillis();

        DonationSubscription sub = DonationSubscription.builder()
                .userId(userId)
                .artistId(artistId)
                .tossCustomerKey(customerKey)
                .status(SubscriptionStatus.PENDING) // PENDING 상태
                .amount(null) // 초기에는 결제 전이므로 null
                .subscribedAt(LocalDateTime.now())
                .nextPaymentDate(null) // 초기에는 결제 전이므로 null
                .build();

        subscriptionRepository.save(sub);

        return customerKey;
    }


    // 2. 빌링키 발급 및 구독 활성화
    @Transactional
    public DonationSubscription createSubscription(Long userId, SubscriptionCreateRequest req) {
        log.info("[SUBSCRIPTION] 빌링키 발급 시작 authKey={}, orderId={}", req.getAuthKey(), req.getOrderId());


        DonationSubscription sub = subscriptionRepository.findByTossCustomerKey(req.getOrderId())
                .orElseThrow(() -> new RuntimeException("PENDING 상태의 구독 레코드를 찾을 수 없습니다."));

        String customerKey = sub.getTossCustomerKey();

        String billingKey = tossClient.issueBillingKey(
                req.getAuthKey(),
                customerKey
        );

        // 3. 구독 엔티티 활성화 및 빌링키 저장
        sub.activate(
            req.getAmount(),
            billingKey,
            LocalDateTime.now().plusMonths(1).truncatedTo(ChronoUnit.MINUTES)
        );

        log.info("[SUBSCRIPTION] 빌링 키 발급 완료 → 구독 활성화 완료");

        executeBilling(sub);

        log.info("첫 결제 완료");

        return sub;
    }

    // 3️⃣ 정기 결제
    @Transactional
    public void executeBilling(DonationSubscription sub) {
        String orderId = "SUBSCRIPTION-" + sub.getId() + "-" + System.currentTimeMillis();

        try {
            TossPaymentResponse resp = tossClient.executeBilling(
                    sub.getBillingKey(),
                    sub.getTossCustomerKey(),
                    sub.getAmount(),
                    orderId
            );

            PaymentHistory history = PaymentHistory.builder()
                    .subscription(sub)
                    .amount(sub.getAmount())
                    .tossPaymentKey(resp.getPaymentKey())
                    .orderId(resp.getOrderId())
                    .status(PaymentStatus.DONE)
                    .paymentDate(LocalDateTime.now())
                    .build();
            paymentHistoryRepository.save(history);

            sub.updateNextPaymentDate(LocalDateTime.now().plusMonths(1));

            TossPaymentTransaction transaction = TossPaymentTransaction.fromDto(resp);
            TossPaymentTransaction savedTransaction = transactionRepository.save(transaction);
            log.info("결제 완료 : {}", savedTransaction);

        } catch (Exception e) {
            PaymentHistory failHistory = PaymentHistory.builder()
                    .subscription(sub)
                    .amount(sub.getAmount())
                    .status(PaymentStatus.CANCELED)
                    .failReason(e.getMessage())
                    .paymentDate(LocalDateTime.now())
                    .build();
            paymentHistoryRepository.save(failHistory);

            log.error("[Billing Fail] subscriptionId={} msg={}", sub.getId(), e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public boolean isSubscribed(Long userId, Long artistId) {
        return subscriptionRepository.findByUserIdAndArtistIdAndStatus(
                userId,
                artistId,
                SubscriptionStatus.ACTIVE
        ).isPresent();
    }

    public List<ArtistDto> getSubscribedArtists(Long userId) {
        List<DonationSubscription> subscriptions = subscriptionRepository.findByUserIdAndStatus(userId, SubscriptionStatus.ACTIVE);

        return subscriptions.stream()
                .map(sub -> {
                    Artist artist = artistRepository.findById(sub.getArtistId())
                            .orElseThrow(() -> new RuntimeException("아티스트가 없습니다: " + sub.getArtistId()));
                    ArtistDto dto = new ArtistDto();
                    dto.setId(artist.getArtistId());
                    dto.setName(artist.getArtistName());
                    dto.setProfileImageUrl(artist.getArtistImageUrl());
                    return dto;
                })
                .collect(Collectors.toList());    }

    @Transactional
    public void cancelSubscription(Long userId, Long artistId) {
        DonationSubscription subscription = subscriptionRepository
                .findByUserIdAndArtistId(userId, artistId)
                .orElseThrow(() -> new RuntimeException("구독 정보를 찾을 수 없습니다."));

        if (subscription.getStatus() == SubscriptionStatus.INACTIVE) {
            return;
        }

        subscription.setStatus(SubscriptionStatus.INACTIVE);
        subscription.setNextPaymentDate(null);
    }


    public SubscriptionDetailDto getSubscriptionDetail(Long userId, Long artistId) {
        DonationSubscription subscription = subscriptionRepository
                .findByUserIdAndArtistIdAndStatus(userId, artistId, SubscriptionStatus.ACTIVE)
                .orElseThrow(() -> new NoSuchElementException("구독 정보를 찾을 수 없습니다."));

        Artist artist = artistRepository.findById(artistId)
                .orElseThrow(() -> new NoSuchElementException("아티스트를 찾을 수 없습니다."));

        List<PaymentHistory> histories = paymentHistoryRepository
                .findBySubscriptionIdOrderByPaymentDateDesc(subscription.getId());

        List<SubscriptionDetailDto.PaymentDetail> paymentHistoryDtos = histories.stream().map(h -> {
            TossPaymentTransaction tx = transactionRepository.findByOrderId(h.getOrderId()).orElse(null);

            SubscriptionDetailDto.PaymentDetail.TossPaymentTransactionDetail tossDetail = null;
            if (tx != null) {
                tossDetail = SubscriptionDetailDto.PaymentDetail.TossPaymentTransactionDetail.builder()
                        .paymentKey(tx.getPaymentKey())
                        .status(tx.getStatus())
                        .type(tx.getType())
                        .method(tx.getMethod())
                        .orderName(tx.getOrderName())
                        .totalAmount(tx.getTotalAmount())
                        .requestedAt(tx.getRequestedAt())
                        .approvedAt(tx.getApprovedAt())
                        .card(tx.getCard() != null ? SubscriptionDetailDto.PaymentDetail.TossPaymentTransactionDetail.CardInfo.builder()
                                .number(tx.getCard().getNumber())
                                .issuerCode(tx.getCard().getIssuerCode())
                                .acquirerCode(tx.getCard().getAcquirerCode())
                                .installmentPlanMonths(tx.getCard().getInstallmentPlanMonths())
                                .approveNo(tx.getCard().getApproveNo())
                                .cardType(tx.getCard().getCardType())
                                .build() : null)
                        .receipt(tx.getReceipt() != null ? SubscriptionDetailDto.PaymentDetail.TossPaymentTransactionDetail.ReceiptInfo.builder()
                                .url(tx.getReceipt().getUrl())
                                .build() : null)
                        .build();
            }

            return SubscriptionDetailDto.PaymentDetail.builder()
                    .orderId(h.getOrderId())
                    .amount(h.getAmount())
                    .status(h.getStatus())
                    .paymentDate(h.getPaymentDate())
                    .tossTransaction(tossDetail)
                    .build();
        }).toList();

        return SubscriptionDetailDto.builder()
                .artistId(artist.getArtistId())
                .artistName(artist.getArtistName())
                .artistImageUrl(artist.getArtistImageUrl())
                .subscribedAt(subscription.getSubscribedAt())
                .amount(subscription.getAmount())
                .nextPaymentDate(subscription.getNextPaymentDate())
                .status(subscription.getStatus())
                .paymentHistory(paymentHistoryDtos)
                .build();
    }
}