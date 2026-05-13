package com.noticore.noticore_api.repository;

import com.noticore.noticore_api.entity.NotificationAttempts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface NotificationAttemptsRepository extends JpaRepository<NotificationAttempts, UUID> {
}
