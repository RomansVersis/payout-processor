package com.example.payoutprocessor.service;

import com.example.payoutprocessor.domain.Payout;
import java.io.File;
import java.util.List;

public interface PayoutFileParser {

    List<Payout> parse(File file) throws Exception;
}
