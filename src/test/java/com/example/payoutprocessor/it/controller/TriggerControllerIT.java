package com.example.payoutprocessor.it.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import com.example.payoutprocessor.service.WakandaPayoutProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TriggerControllerIT {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @MockitoSpyBean
    private WakandaPayoutProcessor processor;

    private String URL;

    @BeforeEach
    void setUp() {
        URL = "http://localhost:" + port + "/api/trigger";
    }


    @Test
    void trigger_valid() {
        var response = restTemplate.postForEntity(URL, null, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("Processing triggered manually");
        verify(processor).processFiles();
    }

    @Test
    void trigger_internalServerError() {
        doThrow(new RuntimeException("Simulated failure")).when(processor).processFiles();

        var response = restTemplate.postForEntity(URL, null, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).contains("Failed to trigger processing: Simulated failure");
        verify(processor).processFiles();
    }
}
