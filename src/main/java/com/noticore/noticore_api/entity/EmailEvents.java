package com.noticore.noticore_api.entity;

import com.noticore.noticore_api.converter.MetadataConverter;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "email_events")
@Data
public class EmailEvents {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notification_id", referencedColumnName = "id")
    private EmailNotifications emailNotifications;

    @Column(name = "event_type")
    private String eventType;

    @Column(name = "occurred_at")
    @CreationTimestamp
    private LocalDateTime occurredAt;

    @Column(name = "payload", columnDefinition = "TEXT")
    private String payload;

    @Column(name = "metadata", columnDefinition = "TEXT")
    @Convert(converter = MetadataConverter.class)
    private Map<String, String> metadata;
}
