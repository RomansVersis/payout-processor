package com.example.payoutprocessor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry
public class PayoutProcessorApplication {

    public static void main(String[] args) {
        SpringApplication.run(PayoutProcessorApplication.class, args);
    }

}
