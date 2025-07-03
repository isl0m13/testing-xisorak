package com.example.appgidritexmonitoring.repository;

import com.example.appgidritexmonitoring.entity.CrackGauge;
import com.example.appgidritexmonitoring.entity.Gate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GateRepository extends JpaRepository<Gate, UUID> {
    Optional<Gate> findByOrdinalAndReservoirId(Integer ordinal, UUID reservoirId);
    Optional<Gate> findByNameAndReservoirId(String name, UUID reservoirId);

    List<Gate> findAllByReservoirId(UUID reservoirId);

    @Query(nativeQuery = true,
    value = """
            SELECT DISTINCT g.*
            FROM gate g
            JOIN piezometer p ON g.id = p.gate_id
            WHERE g.reservoir_id = :reservoirId
            ORDER BY g.ordinal ;
            """)
    List<Gate> findAllByReservoirIdWithPiezometers(UUID reservoirId);

    List<Gate> findAllByReservoirIdOrderByOrdinal(UUID reservoirId);

    @Query(nativeQuery = true,
            value = """
                    SELECT DISTINCT g.*
                    FROM piezometer p
                    JOIN gate g ON p.gate_id = g.id
                    WHERE g.reservoir_id = :reservoirId AND
                        p.location = :locationName
                    ORDER BY g.ordinal ;
                    """
    )
    List<Gate> findAllByReservoirIdAndLocationNameForPiezometers(UUID reservoirId, String locationName);


    @Query(
            nativeQuery = true,
            value = """
                    SELECT DISTINCT g.*
                    FROM plumb p
                    JOIN gate g ON p.gate_id = g.id
                    ORDER BY ordinal DESC ;                
                    """
    )
    List<Gate> getAllOfPlumsOrderByOrdinalDesc();

    @Query(
            nativeQuery = true,
            value = """
                    SELECT DISTINCT g.*
                    FROM crack_gauge cg
                    JOIN gate g ON cg.gate_id = g.id
                    WHERE cg.location = :locationName
                    ORDER BY ordinal;                
                    """
    )
    List<Gate> getAllOfCrackGaugeByLocation(String locationName);

    @Query(nativeQuery = true,
            value = """
                    SELECT DISTINCT g.*
                    FROM tiltmeter t
                    JOIN gate g ON t.gate_id = g.id
                    WHERE g.reservoir_id = :reservoirId
                    ORDER BY g.ordinal
                    """
    )
    List<Gate> getAllGatesOfTiltmetersByReservoir(UUID reservoirId);




}
