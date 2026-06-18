package com.noticore.noticore_api.repository;

import com.noticore.noticore_api.entity.SuppressedEmails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SuppressedEmailsRespository extends JpaRepository<SuppressedEmails, UUID> {
    Optional<SuppressedEmails> findByTenants_IdAndEmail(UUID tenantId, String email);
    boolean existsByTenants_IdAndEmail(UUID tenantId, String email);
}
