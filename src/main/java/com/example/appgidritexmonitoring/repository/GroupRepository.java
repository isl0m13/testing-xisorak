package com.example.appgidritexmonitoring.repository;

import com.example.appgidritexmonitoring.entity.Group;
import com.example.appgidritexmonitoring.payload.GroupDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface GroupRepository extends JpaRepository<Group, UUID> {

    @Query(nativeQuery = true,
            value = """
                    SELECT COUNT(id)
                    FROM groups
                    WHERE reservoir_id = :reservoirId ;
                    """)
    Integer getCountByReservoirId(UUID reservoirId);

    List<Group> getAllByReservoirIdOrderByCreatedAtDesc(UUID reservoirId);

}
