package com.noticore.noticore_api.repository;

import com.noticore.noticore_api.entity.EmailEvents;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EmailEventsRepository extends JpaRepository<EmailEvents, UUID> {
}
