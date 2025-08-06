package com.example.payoutprocessor.domain;

import java.time.LocalDate;
import lombok.Getter;

@Getter
public class Payout {

    private final String companyIdentityNumber;
    private final LocalDate paymentDate;
    private final double paymentAmount;

    public Payout(String companyIdentityNumber, LocalDate paymentDate, double paymentAmount) {
        this.companyIdentityNumber = companyIdentityNumber;
        this.paymentDate = paymentDate;
        this.paymentAmount = paymentAmount;
    }
}
