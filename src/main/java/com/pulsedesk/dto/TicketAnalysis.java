package com.pulsedesk.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TicketAnalysis {
    private boolean needsTicket;
    private String title;
    private String category;
    private String priority;
    private String summary;
    private boolean manualReview;
}