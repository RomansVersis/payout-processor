package com.example.payoutprocessor.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WakandaFileParserTest {

    private WakandaFileParser parser;

    @BeforeEach
    void setUp() {
        parser = new WakandaFileParser();
    }

    @Test
    void parse_validRecords() throws Exception {
        var tempFile = new File("src/test/resources/WK_payouts_valid_records.csv");

        var payouts = parser.parse(tempFile);

        assertEquals(2, payouts.size());
        assertEquals("156-5562415", payouts.get(0).getCompanyIdentityNumber());
        assertEquals(LocalDate.of(2023, 11, 17), payouts.get(0).getPaymentDate());
        assertEquals(7000.10, payouts.get(0).getPaymentAmount(), 0.001);
    }

    @Test
    void parse_invalidRecords() throws Exception {
        var tempFile = new File("src/test/resources/WK_payouts_invalid_records.csv");

        var payouts = parser.parse(tempFile);

        // Only one valid record should be parsed
        assertEquals(1, payouts.size());
        assertEquals("123-456", payouts.get(0).getCompanyIdentityNumber());
    }

    @Test
    void parse_emptyRecords() throws Exception {
        // Empty file
        var tempFile = new File("src/test/resources/WK_payouts_empty_records.csv");

        var payouts = parser.parse(tempFile);

        assertTrue(payouts.isEmpty());
    }

    @Test
    void parse_noFileException() {
        var nonExistentFile = new File("non_existing.csv");

        assertThrows(Exception.class, () -> parser.parse(nonExistentFile));
    }
}
