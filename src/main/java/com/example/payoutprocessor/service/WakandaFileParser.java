package com.example.payoutprocessor.service;

import com.example.payoutprocessor.domain.Payout;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class WakandaFileParser implements PayoutFileParser {

    private static final Logger errorLogger = LoggerFactory.getLogger("InvalidPayoutLogger");

    private static final String TAX_NUMBER_HEADER = "Company tax number";
    private static final String PAYMENT_DATE_HEADER = "Payment Date";
    private static final String AMOUNT_HEADER = "Amount";

    @Override
    public List<Payout> parse(File file) throws Exception {
        List<Payout> payouts = new ArrayList<>();

        var format = CSVFormat.DEFAULT.builder()
                .setDelimiter(';')
                .setQuote('"')
                .setTrim(true)
                .setIgnoreEmptyLines(true)
                .setAllowMissingColumnNames(true)
                .setHeader()
                .setSkipHeaderRecord(true)
                .build();

        try (var reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.ISO_8859_1);
                var parser = new CSVParser(reader, format)) {

            for (var record : parser) {
                try {
                    var taxNumber = record.get(TAX_NUMBER_HEADER);
                    var date = LocalDate.parse(record.get(PAYMENT_DATE_HEADER));
                    var amount = Double.parseDouble(record.get(AMOUNT_HEADER).replace(",", "."));

                    payouts.add(new Payout(taxNumber, date, amount));
                } catch (Exception e) {
                    log.info("Failed to send payout record {} due to {}", record, e.getMessage());
                    errorLogger.error("Invalid record [{}] - Reason: {}", record, e.getMessage());
                }
            }
        }

        return payouts;
    }
}
