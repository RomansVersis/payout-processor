package com.example.payoutprocessor.controller;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.payoutprocessor.service.WakandaPayoutProcessor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(TriggerController.class)
class TriggerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WakandaPayoutProcessor processor;

    @Test
    void trigger_valid() throws Exception {
        doNothing().when(processor).processFiles();

        mockMvc.perform(post("/api/trigger"))
                .andExpect(status().isOk())
                .andExpect(content().string("Processing triggered manually"));

        verify(processor).processFiles();
    }

    @Test
    void trigger_internalServerError() throws Exception {
        doThrow(new RuntimeException("Simulated failure")).when(processor).processFiles();

        mockMvc.perform(post("/api/trigger"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Failed to trigger processing: Simulated failure"));

        verify(processor).processFiles();
    }
}
