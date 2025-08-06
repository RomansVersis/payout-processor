package com.example.payoutprocessor.service;

import com.example.payoutprocessor.domain.Payout;
import java.io.File;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class GondorFileParser implements PayoutFileParser {

    @Override
    public List<Payout> parse(File file) {
        // To be implemented when Gondor file format is supported
        return Collections.emptyList();
    }
}
