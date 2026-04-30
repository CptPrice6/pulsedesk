package com.pulsedesk.service;

import com.pulsedesk.dto.TicketAnalysis;
import com.pulsedesk.model.Comment;
import com.pulsedesk.model.Ticket;
import com.pulsedesk.repository.CommentRepository;
import com.pulsedesk.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final TicketRepository ticketRepository;
    private final HuggingFaceService aiService;

    // saves the comment, runs AI analysis, creates ticket if needed
    public Comment addComment(String text, String channel) {

        Comment comment = new Comment();
        comment.setText(text);
        comment.setChannel(channel != null ? channel : "web");

        // we save first to get the ID, for linking ticket to comment
        comment = commentRepository.save(comment);

        TicketAnalysis aiResult = aiService.analyze(text);

        comment.setRequiresManualReview(aiResult.isManualReview());

        // update comment to mark it was converted to a ticket
        comment.setConvertedToTicket(aiResult.isNeedsTicket());

        if (aiResult.isNeedsTicket()) {
            Ticket ticket = new Ticket();
            ticket.setCommentId(comment.getId());
            ticket.setTitle(aiResult.getTitle());
            ticket.setCategory(aiResult.getCategory());
            ticket.setPriority(aiResult.getPriority());
            ticket.setSummary(aiResult.getSummary());
            ticketRepository.save(ticket);
        }

        return commentRepository.save(comment);
    }

    public List<Comment> getAllComments() {
        return commentRepository.findAll();
    }

    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    public Optional<Ticket> getTicketById(Long id) {
        return ticketRepository.findById(id);
    }
}