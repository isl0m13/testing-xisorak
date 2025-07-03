package com.example.appgidritexmonitoring.repository;

import com.example.appgidritexmonitoring.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface DocumentRepository extends JpaRepository<Document, UUID> {

    boolean existsByGroupId(UUID groupId);

    Integer countByGroupId(UUID groupId);

    List<Document> getAllByGroupIdOrderByCreatedAtDesc(UUID groupId);

    @Query(nativeQuery = true,
    value = """
            SELECT d.*
            FROM document d
            JOIN groups g ON d.group_id = g.id
            WHERE reservoir_id = :reservoirId
            ORDER BY g.id, d.created_at DESC ;
            """)
    List<Document> getAllByReservoirId(UUID reservoirId);


}
