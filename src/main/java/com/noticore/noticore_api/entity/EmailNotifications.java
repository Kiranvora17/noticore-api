package com.noticore.noticore_api.entity;

import com.noticore.noticore_api.enums.EmailNotificationStatus;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "email_notifications")
@Data
public class EmailNotifications {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", referencedColumnName = "id")
    private Tenants tenants;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "domain", referencedColumnName = "id")
    private TenantDomains tenantDomains;

    @Column(name = "from_email")
    private String fromEmail;

    @Column(name = "to_email")
    private String toEmail;

    @Column(name = "subject")
    private String subject;

    @Column(name = "body")
    private String body;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private EmailNotificationStatus emailNotificationStatus;

    @Column(name = "retry_count")
    private int retryCount;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "creation_date")
    @CreationTimestamp
    private LocalDateTime creationDate;

    @Column(name = "modified_date")
    @UpdateTimestamp
    private LocalDateTime modifiedDate;

    @OneToMany(mappedBy = "emailNotifications",fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NotificationAttempts> notificationAttempts;
}
