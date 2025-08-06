package com.example.payoutprocessor.controller;

import com.example.payoutprocessor.service.WakandaPayoutProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api")
public class TriggerController {

    private final WakandaPayoutProcessor processor;

    public TriggerController(WakandaPayoutProcessor processor) {
        this.processor = processor;
    }

    @PostMapping("/trigger")
    public ResponseEntity<String> trigger() {
        try {
            processor.processFiles();
            log.info("Manual payout processing triggered successfully");
            return ResponseEntity.ok("Processing triggered manually");
        } catch (Exception e) {
            log.error("Error during manual processing trigger", e);
            return ResponseEntity.internalServerError()
                    .body("Failed to trigger processing: " + e.getMessage());
        }
    }
}
