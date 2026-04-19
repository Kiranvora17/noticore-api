package com.noticore.noticore_api.entity;

import com.noticore.noticore_api.enums.NotificationAttemptStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "notification_attempts")
public class NotificationAttempts {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notification_id", referencedColumnName = "id")
    private EmailNotifications emailNotifications;

    @Column(name = "attempted_at")
    private LocalDateTime attemptedAt;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private NotificationAttemptStatus status;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    @Column(name = "modified_date")
    private LocalDateTime modifiedDate;

}
