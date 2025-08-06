package com.example.payoutprocessor.client;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;

public class PayoutRequestDto {

    private final String companyIdentityNumber;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private final LocalDate paymentDate;
    private final Double paymentAmount;

    public PayoutRequestDto(String companyIdentityNumber, LocalDate paymentDate, Double paymentAmount) {
        this.companyIdentityNumber = companyIdentityNumber;
        this.paymentDate = paymentDate;
        this.paymentAmount = paymentAmount;
    }

    @JsonProperty
    public String getCompanyIdentityNumber() {
        return companyIdentityNumber;
    }

    @JsonProperty
    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    @JsonProperty
    public double getPaymentAmount() {
        return paymentAmount;
    }
}
