package com.example.payoutprocessor.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.payoutprocessor.client.PayoutApiClient;
import com.example.payoutprocessor.domain.Payout;
import java.io.File;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;

class WakandaPayoutProcessorTest {

    private WakandaFileParser parser;
    private PayoutApiClient apiClient;
    private WakandaPayoutProcessor processor;
    @TempDir
    private File tempDir;

    @BeforeEach
    void setUp() {
        parser = mock(WakandaFileParser.class);
        apiClient = mock(PayoutApiClient.class);

        processor = new WakandaPayoutProcessor(parser, apiClient, tempDir.getAbsolutePath());
    }

    @Test
    void processFiles_allValid_fileDeleted() throws Exception {
        var file = new File(tempDir, "WK_payouts_20250103_000000.csv");
        Files.writeString(file.toPath(), "dummy");

        var payout1 = new Payout("111-222", LocalDate.of(2025, 2, 1), 1000.0);
        var payout2 = new Payout("333-444", LocalDate.of(2025, 2, 2), 2000.0);
        when(parser.parse(file)).thenReturn(List.of(payout1, payout2));

        processor.processFiles();

        var captor = ArgumentCaptor.forClass(Payout.class);
        verify(apiClient, times(2)).sendPayout(captor.capture());

        var sentPayouts = captor.getAllValues();
        assertEquals(2, sentPayouts.size());
        assertEquals("111-222", sentPayouts.get(0).getCompanyIdentityNumber());
        assertEquals(2000.0, sentPayouts.get(1).getPaymentAmount());
        assertFalse(file.exists());
    }

    @Test
    void processFiles_noFilesFound() throws Exception {
        processor.processFiles();

        verify(parser, never()).parse(any(File.class));
        verify(apiClient, never()).sendPayout(any());
    }

    @Test
    void processFiles_failsOnParser_fileMovedToFailed() throws Exception {
        var file1 = new File(tempDir, "WK_payouts_20250101_000000.csv");
        Files.writeString(file1.toPath(), "dummy");

        when(parser.parse(file1)).thenThrow(new RuntimeException("Parsing error"));

        processor.processFiles();

        verify(parser).parse(file1);
        verify(apiClient, never()).sendPayout(any());

        var failedDir = new File(tempDir, "failed");
        var movedFile = new File(failedDir, file1.getName());
        assertTrue(movedFile.exists());
    }

    @Test
    void processFiles_partialFailure_fileMovedToFailed() throws Exception {
        var file = new File(tempDir, "WK_payouts_20250105_000000.csv");
        Files.writeString(file.toPath(), "dummy");

        var payout1 = new Payout("AAA-111", LocalDate.of(2025, 3, 1), 500.0);
        var payout2 = new Payout("BBB-222", LocalDate.of(2025, 3, 2), 600.0);
        when(parser.parse(file)).thenReturn(List.of(payout1, payout2));

        doThrow(new RuntimeException("API error")).when(apiClient).sendPayout(payout2);

        processor.processFiles();

        verify(apiClient, times(2)).sendPayout(any());

        var failedDir = new File(tempDir, "failed");
        var movedFile = new File(failedDir, file.getName());
        assertTrue(movedFile.exists());
        assertFalse(file.exists());
    }
}
