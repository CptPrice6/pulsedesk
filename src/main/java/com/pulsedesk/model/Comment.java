package com.pulsedesk.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String text;

    private String channel;

    private LocalDateTime createdAt = LocalDateTime.now();

    private boolean convertedToTicket = false;

    private boolean requiresManualReview = false;
}