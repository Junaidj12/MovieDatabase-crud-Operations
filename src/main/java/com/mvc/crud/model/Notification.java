package com.mvc.crud.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String message;

    private LocalDateTime timestamp;

    private boolean seen = false;  // ✅ new field

    @ManyToOne
    @JoinColumn(name = "recipient_id")
    private Friend recipient;

    public Notification() {
        this.timestamp = LocalDateTime.now();
        this.seen = false;
    }

    public Notification(String message, Friend receiver) {
        this.message = message;
        this.recipient = receiver;
        this.timestamp = LocalDateTime.now();
        this.seen = false;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public String getMessage() { return message; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public Friend getReceiver() { return recipient; }
    public boolean isSeen() { return seen; }

    public void setId(Long id) { this.id = id; }
    public void setMessage(String message) { this.message = message; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public void setReceiver(Friend receiver) { this.recipient = receiver; }
    public void setSeen(boolean seen) { this.seen = seen; }
}
