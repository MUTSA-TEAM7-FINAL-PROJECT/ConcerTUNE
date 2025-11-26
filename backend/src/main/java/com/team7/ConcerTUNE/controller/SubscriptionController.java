package com.team7.ConcerTUNE.temp.controller;

import com.team7.ConcerTUNE.config.TossPaymentsConfig;
import com.team7.ConcerTUNE.entity.Artist;
import com.team7.ConcerTUNE.entity.User;
import com.team7.ConcerTUNE.service.AuthService;
import com.team7.ConcerTUNE.temp.dto.ArtistDto;
import com.team7.ConcerTUNE.temp.dto.SubscriptionCreateRequest;
import com.team7.ConcerTUNE.temp.dto.SubscriptionDetailDto;
import com.team7.ConcerTUNE.temp.dto.SubscriptionInitRequest;
import com.team7.ConcerTUNE.temp.entity.DonationSubscription;
import com.team7.ConcerTUNE.temp.entity.TossPaymentTransaction;
import com.team7.ConcerTUNE.temp.repository.TossPaymentTransactionRepository;
import com.team7.ConcerTUNE.temp.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/subscription")
@RequiredArgsConstructor
@Slf4j
public class SubscriptionController {

    private final TossPaymentsConfig config;
    private final SubscriptionService subscriptionService;
    private final AuthService authService;

    @PostMapping("/init")
    public Map<String, String> initSubscription(@RequestBody SubscriptionInitRequest req,
                                                Authentication authentication) {
        User user = authService.getUserFromAuth(authentication);

        String customerKey = subscriptionService.registerCustomer(user.getId(), req.getArtistId());

        return Map.of(
                "customerKey", customerKey
        );
    }

    @GetMapping("/toss/success")
    public DonationSubscription tossSuccess(
            @RequestParam String authKey,
            @RequestParam String orderId,
            @RequestParam int amount,
            Authentication authentication) {

        User user = authService.getUserFromAuth(authentication);


        SubscriptionCreateRequest request = SubscriptionCreateRequest.builder()
                .authKey(authKey)
                .orderId(orderId)
                .amount(amount)
                .build();

        return subscriptionService.createSubscription(user.getId(), request);
    }

    // 💡 3. 토스 실패 리디렉션 처리 엔드포인트 추가 (선택적)
    @GetMapping("/toss/fail")
    public String tossFail(
            @RequestParam String code,
            @RequestParam String message,
            @RequestParam String orderId) {

        log.error("[TOSS FAIL] Code: {}, Message: {}, Order ID: {}", code, message, orderId);

        return "결제 실패: " + message; // 프론트엔드에서 이 메시지를 사용자에게 보여줍니다.
    }

    @GetMapping("/status/{artistId}")
    public ResponseEntity<Map<String, Boolean>> getSubscriptionStatus(
            @PathVariable Long artistId,
            Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.ok(Map.of("subscribed", false));
        }

        User user = authService.getUserFromAuth(authentication);

        boolean isSubscribed = subscriptionService.isSubscribed(user.getId(), artistId);
        return ResponseEntity.ok(Map.of("subscribed", isSubscribed));
    }

    @DeleteMapping("/cancel/{artistId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> cancelSubscription(
            Authentication authentication,
            @PathVariable Long artistId
    ) {
        User user = authService.getUserFromAuth(authentication);
        subscriptionService.cancelSubscription(user.getId(), artistId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/detail/{artistId}")
    @PreAuthorize("isAuthenticated()")
    public SubscriptionDetailDto getSubscriptionDetail(
            Authentication authentication,
            @PathVariable Long artistId
    ) {
        User user = authService.getUserFromAuth(authentication);
        return subscriptionService.getSubscriptionDetail(user.getId(), artistId);
    }


}