package com.example.appgidritexmonitoring.repository;

import com.example.appgidritexmonitoring.entity.PiezometerMeasurement;
import com.example.appgidritexmonitoring.payload.projection.PiezometerMeasurementAndCoordsProj;
import com.example.appgidritexmonitoring.payload.projection.PiezometerMeasurementAndGateProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface PiezometerMeasurementRepository extends JpaRepository<PiezometerMeasurement, UUID> {

    @Query(nativeQuery = true,
            value = """
                    with temp as (SELECT max(date) as date,
                                          pm.piezometer_id
                                   FROM piezometer_measurement pm
                                   GROUP BY pm.piezometer_id)
                     SELECT pm.*, p.ordinal
                     FROM piezometer_measurement pm
                              JOIN piezometer p ON pm.piezometer_id = p.id
                     WHERE pm.date = (SELECT date FROM temp t WHERE t.piezometer_id = p.id)
                       and p.gate_id = :gateId
                     ORDER BY p.ordinal ;
                     """)
    List<PiezometerMeasurement> findAllLatestOfGate(UUID gateId);


    @Query(nativeQuery = true,
            value = """
                    with temp as (SELECT max(date) as date,
                                         pm.piezometer_id
                                  FROM piezometer_measurement pm
                                  GROUP BY pm.piezometer_id)
                                        
                    SELECT pm.piezometer_id as piezometer_id,
                           p.ordinal as piezometer_ordinal,
                           pm.computed_pressure as hydro_pressure,
                           pm.temp_in_degrees as temperature,
                           pm.date as date,
                           g.id as gate_id,
                           g.ordinal gate_ordinal,
                           g.name as gate_name
                    FROM piezometer_measurement pm
                             JOIN piezometer p ON pm.piezometer_id = p.id
                             JOIN gate g ON p.gate_id = g.id
                    WHERE pm.date = (SELECT date FROM temp t WHERE t.piezometer_id = p.id)
                      AND g.reservoir_id = :reservoirId ;
                    """)
    List<PiezometerMeasurementAndGateProjection> findAllLatestOfAllGates2(UUID reservoirId);

    @Query(nativeQuery = true,
            value = """
                        with temp as (SELECT max(date) as date,
                                             pm.piezometer_id
                                      FROM piezometer_measurement pm
                                      GROUP BY pm.piezometer_id)
                                            
                        SELECT pm.*
                        FROM piezometer_measurement pm
                                 JOIN piezometer p ON pm.piezometer_id = p.id
                                 JOIN gate g ON p.gate_id = g.id
                        WHERE pm.date = (SELECT date FROM temp t WHERE t.piezometer_id = p.id)
                          AND g.reservoir_id = :reservoirId
                        ORDER BY g.ordinal, p.ordinal ;
                    """)
    List<PiezometerMeasurement> findAllLatestOfAllGates(UUID reservoirId);

    @Query(nativeQuery = true,
            value = """
                        with temp as (SELECT max(date) as date,
                                                                            pm.piezometer_id
                                                                     FROM piezometer_measurement pm
                                                                     JOIN piezometer p ON pm.piezometer_id = p.id
                                                                     WHERE p.location = :locationName
                                                                     GROUP BY pm.piezometer_id)
                                                       
                                                       SELECT pm.*
                                                       FROM piezometer_measurement pm
                                                                JOIN piezometer p ON pm.piezometer_id = p.id
                                                                JOIN gate g ON p.gate_id = g.id
                                                       WHERE pm.date = (SELECT date FROM temp t WHERE t.piezometer_id = p.id)
                                                         AND g.reservoir_id = :reservoirId AND p.location = :locationName
                                                       ORDER BY g.name, p.ordinal ;
                    """)
    List<PiezometerMeasurement> findAllLatestOfLocation(UUID reservoirId, String locationName);

    @Query(nativeQuery = true,
            value = """
                        with temp as (SELECT max(date) as date,
                                                                            pm.piezometer_id
                                                                     FROM piezometer_measurement pm
                                                                     JOIN piezometer p ON pm.piezometer_id = p.id
                                                                     WHERE p.location = :locationName
                                                                     GROUP BY pm.piezometer_id)
                                                       
                                                       SELECT pm.*
                                                       FROM piezometer_measurement pm
                                                                JOIN piezometer p ON pm.piezometer_id = p.id
                                                                JOIN gate g ON p.gate_id = g.id
                                                       WHERE pm.date = (SELECT date FROM temp t WHERE t.piezometer_id = p.id)
                                                         AND g.reservoir_id = :reservoirId AND p.location = :locationName
                                                       ORDER BY p.ordinal ;
                    """)
    List<PiezometerMeasurement> findAllLatestOfLocationOrderByPiezometerOrdinal(UUID reservoirId, String locationName);

    @Query(nativeQuery = true,
            value = """
                    with temp as (SELECT max(date) as date,
                                         pm.piezometer_id
                                  FROM piezometer_measurement pm
                                  GROUP BY pm.piezometer_id)
                                
                    SELECT CAST(pm.piezometer_id AS VARCHAR) AS piezometer_id,
                           p.ordinal,
                           p.name,
                           pm.date,
                           pm.computed_pressure,
                           pm.virtual_pressure,
                           pm.temp_in_degrees,
                           p.lat,
                           p.lon
                    FROM piezometer_measurement pm
                             JOIN piezometer p ON pm.piezometer_id = p.id
                             JOIN gate g ON p.gate_id = g.id
                    WHERE pm.date = (SELECT date FROM temp t WHERE t.piezometer_id = p.id)
                      AND g.reservoir_id = :reservoirId
                    ORDER BY p.ordinal ;
                    """)
    List<PiezometerMeasurementAndCoordsProj> getLatestMeasurementWithCoordsOfReservoir(UUID reservoirId);

    @Query(nativeQuery = true,
            value = """
                    SELECT pm.*
                    FROM piezometer_measurement pm
                             JOIN piezometer p ON pm.piezometer_id = p.id
                    WHERE p.gate_id = :gateId
                    ORDER BY date DESC, p.ordinal ;
                    """
    )
    List<PiezometerMeasurement> findAllOfGate(UUID gateId);

    @Query(nativeQuery = true,
            value = """
                    SELECT pm.*
                    FROM piezometer_measurement pm
                             JOIN piezometer p ON pm.piezometer_id = p.id
                    WHERE p.gate_id = :gateId AND pm.date BETWEEN :startDate AND :endDate
                    ORDER BY date, p.ordinal ;
                    """
    )
    List<PiezometerMeasurement> findAllOfGateByDate(UUID gateId, LocalDateTime startDate, LocalDateTime endDate);

    boolean existsByPiezometerIdAndDate(UUID piezometerId, LocalDateTime date);

    List<PiezometerMeasurement> findAllByPiezometerId(UUID piezometerId);

    @Query(nativeQuery = true,
    value = """
            SELECT *
            FROM piezometer_measurement
            WHERE piezometer_id = :piezometerId AND date BETWEEN :startDate AND :endDate
            ORDER BY date DESC ;
            """)
    List<PiezometerMeasurement> getAllByPiezometerIdByDates(UUID piezometerId,
                                                            LocalDateTime startDate,
                                                            LocalDateTime endDate);



}
