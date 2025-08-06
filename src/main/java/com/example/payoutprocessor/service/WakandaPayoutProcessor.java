package com.example.payoutprocessor.service;

import com.example.payoutprocessor.client.PayoutApiClient;
import com.example.payoutprocessor.domain.Payout;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class WakandaPayoutProcessor {

    private final WakandaFileParser parser;
    private final PayoutApiClient apiClient;
    private final String directoryPath;

    public WakandaPayoutProcessor(WakandaFileParser parser, PayoutApiClient apiClient,
            @Value("${wakanda.directory.path}") String directoryPath) {
        this.parser = parser;
        this.apiClient = apiClient;
        this.directoryPath = directoryPath;
    }

    public void processFiles() {
        var folder = new File(directoryPath);
        var files = folder.listFiles((dir, name) -> name.startsWith("WK_payouts_") && name.endsWith(".csv"));

        if (files == null || files.length == 0) {
            log.info("No payout files found to process in directory: {}", directoryPath);
            return;
        }

        var failedDir = new File(directoryPath, "failed");
        if (!failedDir.exists() && !failedDir.mkdir()) {
            log.warn("Could not create failed directory: {}", failedDir.getAbsolutePath());
        }

        for (var file : files) {
            log.info("Starting processing the file: {}", file.getName());
            var allPayoutsSent = true;

            try {
                var payouts = parser.parse(file);

                for (Payout p : payouts) {
                    try {
                        apiClient.sendPayout(p);
                    } catch (Exception e) {
                        allPayoutsSent = false;
                        log.error("Failed to send payout for company {}: {}", p.getCompanyIdentityNumber(), e.getMessage());
                    }
                }

                if (allPayoutsSent) {
                    if (file.delete()) {
                        log.info("File {} processed and deleted successfully.", file.getName());
                    } else {
                        log.warn("File {} processed but could not be deleted.", file.getName());
                    }
                } else {
                    moveToFailed(file.toPath(), failedDir.toPath());
                }

            } catch (Exception e) {
                log.error("Failed to parse file {}: {}", file.getName(), e.getMessage());
                moveToFailed(file.toPath(), failedDir.toPath());
            }
        }

        log.info("Completed processing {} payout files", files.length);
    }

    private void moveToFailed(Path filePath, Path failedDir) {
        try {
            Files.move(filePath, failedDir.resolve(filePath.getFileName()), StandardCopyOption.REPLACE_EXISTING);
            log.info("Moved file {} to failed directory for investigation.", filePath.getFileName());
        } catch (IOException ioe) {
            log.error("Could not move file {} to failed directory: {}", filePath.getFileName(), ioe.getMessage());
        }
    }
}
