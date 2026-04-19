package com.noticore.noticore_api.entity;

import com.noticore.noticore_api.enums.DomainStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tenant_domains")
public class TenantDomains {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    @NotNull
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", referencedColumnName = "id")
    private Tenants tenants;

    @Column(name = "domain_name")
    @NotNull
    private String domainName;

    @Column(name = "dkim_token")
    @NotNull
    private String dkimToken;

    @Column(name = "status")
    @NotNull
    @Enumerated(EnumType.STRING)
    private DomainStatus status;

    @Column(name = "creation_date")
    @CreationTimestamp
    private LocalDateTime creationDate;

    @Column(name = "modified_date")
    @UpdateTimestamp
    private LocalDateTime modifiedDate;

}
