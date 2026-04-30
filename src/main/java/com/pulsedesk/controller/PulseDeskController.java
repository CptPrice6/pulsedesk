package com.pulsedesk.controller;

import com.pulsedesk.dto.CommentRequest;
import com.pulsedesk.model.Comment;
import com.pulsedesk.model.Ticket;
import com.pulsedesk.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class PulseDeskController {

    private final CommentService commentService;

    // accepts comment, triggers AI analysis, returns saved comment
    @PostMapping("/comments")
    public ResponseEntity<Comment> addComment(@RequestBody CommentRequest payload) {
        if (payload.getText() == null || payload.getText().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(commentService.addComment(payload.getText(), payload.getChannel()));
    }

    @GetMapping("/comments")
    public List<Comment> getComments() {
        return commentService.getAllComments();
    }

    @GetMapping("/tickets")
    public List<Ticket> getTickets() {
        return commentService.getAllTickets();
    }

    // returns 404 if ticket doesn't exist
    @GetMapping("/tickets/{ticketId}")
    public ResponseEntity<Ticket> getTicket(@PathVariable Long ticketId) {
        return commentService.getTicketById(ticketId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}