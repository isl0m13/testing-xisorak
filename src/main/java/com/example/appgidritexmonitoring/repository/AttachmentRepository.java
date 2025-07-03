package com.example.appgidritexmonitoring.repository;

import com.example.appgidritexmonitoring.entity.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AttachmentRepository extends JpaRepository<Attachment, UUID> {
    Optional<Attachment> findByName(String name);


}
