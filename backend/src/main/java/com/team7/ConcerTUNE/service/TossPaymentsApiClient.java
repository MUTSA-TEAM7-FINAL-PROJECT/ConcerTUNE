package com.team7.ConcerTUNE.temp.service;

import com.team7.ConcerTUNE.config.TossPaymentsConfig;
import com.team7.ConcerTUNE.dto.TossBillingKeyResponse;
import com.team7.ConcerTUNE.dto.TossPaymentResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Base64;
import java.util.Map;

@Service
@Slf4j
public class TossPaymentsApiClient {

    private final TossPaymentsConfig config;
    private final WebClient webClient;

    public TossPaymentsApiClient(TossPaymentsConfig config) {
        this.config = config;
        this.webClient = WebClient.builder()
                .baseUrl(config.getApi().getBaseUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }



    public String issueBillingKey(String authKey, String customerKey) {
        String auth = createAuthHeader();

        // 💡 토스 API 문서에 명시된 대로 authKey를 사용합니다.
        Map<String, Object> requestBody = Map.of(
                "authKey", authKey,
                "customerKey", customerKey
        );

        log.info("토스페이먼츠 빌링키 발급 요청 시작 - Auth Key: {}, Customer Key: {}", authKey, customerKey);

        try {
            TossBillingKeyResponse res = webClient.post()
                    .uri("/billing/authorizations/issue")
                    .header(HttpHeaders.AUTHORIZATION, auth)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(TossBillingKeyResponse.class)
                    .block();

            log.info("토스페이먼츠 빌링키 발급 성공: BillingKey={}", res.getBillingKey());
            return res.getBillingKey();

        } catch (WebClientResponseException e) {
            log.error("Toss 빌링키 발급 실패: 응답 코드={}, 본문={}", e.getRawStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("빌링키 발급 중 토스 시스템 오류: " + e.getResponseBodyAsString(), e);
        }
    }

    public TossPaymentResponse executeBilling(String billingKey, String customerKey, Integer amount, String orderId) {
        String auth = createAuthHeader();

        log.info("토스페이먼츠 정기결제 요청 - customerKey: {}, amount: {}", customerKey, amount);

        try {
            return webClient.post()
                    .uri("/billing/" + billingKey)
                    .header(HttpHeaders.AUTHORIZATION, auth)
                    .bodyValue(Map.of(
                            "amount", amount,
                            "customerKey", customerKey,
                            "orderId", orderId,
                            "orderName", "정기구독 자동결제"
                    ))
                    .retrieve()
                    .bodyToMono(TossPaymentResponse.class)
                    .block();

        } catch (Exception e) {
            log.error("정기결제 실패: {}", e.getMessage());
            throw new RuntimeException("정기결제 API 호출 실패: " + e.getMessage());
        }
    }

    private String createAuthHeader() {
        String credentials = config.getSecretKey() + ":";
        String encoded = Base64.getEncoder().encodeToString(credentials.getBytes());
        return "Basic " + encoded;
    }
}