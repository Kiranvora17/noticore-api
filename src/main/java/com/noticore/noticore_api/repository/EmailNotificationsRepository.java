package com.noticore.noticore_api.repository;

import com.noticore.noticore_api.entity.EmailNotifications;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmailNotificationsRepository extends JpaRepository<EmailNotifications, UUID> {
    Optional<List<EmailNotifications>> findAllByTenants_Id(UUID tenantId);
    Optional<EmailNotifications> findBySesMessageId(String messageId);
}
