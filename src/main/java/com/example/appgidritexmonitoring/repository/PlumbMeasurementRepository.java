package com.example.appgidritexmonitoring.repository;

import com.example.appgidritexmonitoring.entity.PlumbMeasurement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface PlumbMeasurementRepository extends JpaRepository<PlumbMeasurement, UUID> {

    boolean existsByPlumbIdAndDate(UUID plumb_id, LocalDateTime date);


    @Query(
            nativeQuery = true,
            value = """
                    with temp as (SELECT MAX(date) date, plumb_id
                                  FROM plumb_measurement
                                  GROUP BY plumb_id)
                    SELECT pm.*
                    FROM plumb_measurement pm
                    JOIN plumb p ON pm.plumb_id = p.id
                    JOIN gate g ON p.gate_id = g.id
                    WHERE pm.date = (SELECT t.date FROM temp t WHERE t.plumb_id = pm.plumb_id)
                    AND g.reservoir_id = :reservoirId ;
                    """
    )
    List<PlumbMeasurement> getLatestMeasurementsByReservoirId(UUID reservoirId);


    @Query(nativeQuery = true,
            value = """
                    SELECT *
                    FROM plumb_measurement pm
                    WHERE pm.plumb_id = :plumbId AND date BETWEEN :startDate AND :endDate
                    ORDER BY date DESC ;
                    """
    )
    List<PlumbMeasurement> getAllByPlumbIdByDates(UUID plumbId, LocalDateTime startDate, LocalDateTime endDate);


}
