package com.example.payoutprocessor.client;

import com.example.payoutprocessor.domain.Payout;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
public class PayoutApiClient {

    private final RestTemplate restTemplate;
    private final String API_URL;

    public PayoutApiClient(RestTemplate restTemplate,
            @Value("${api.payout.url}") String apiUrl) {
        this.restTemplate = restTemplate;
        this.API_URL = apiUrl;
    }

    @Retryable(
            retryFor = RestClientException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000, multiplier = 2)
    )
    public void sendPayout(Payout payout) {

        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        var requestBody = new PayoutRequestDto(
                payout.getCompanyIdentityNumber(),
                payout.getPaymentDate(),
                payout.getPaymentAmount()
        );

        restTemplate.postForEntity(API_URL, new HttpEntity<>(requestBody, headers), String.class);
        log.info("Successfully sent payout for {}.", payout.getCompanyIdentityNumber());
    }

    @Recover
    public void recover(RestClientException e, Payout payout) {
        log.error("Failed to send payout {} after retries. Error: {}", payout.getCompanyIdentityNumber(), e.getMessage());
        throw e;
    }
}
