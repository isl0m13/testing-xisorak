package com.example.appgidritexmonitoring.repository;

import com.example.appgidritexmonitoring.entity.TiltmeterMeasurement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface TiltmeterMeasurementRepository extends JpaRepository<TiltmeterMeasurement, UUID> {

    boolean existsByTiltmeterIdAndDate(UUID tiltmeterId, LocalDateTime date);

    @Query(nativeQuery = true,
            value = """
                    with temp as (SELECT MAX(date) date, tiltmeter_id
                                  FROM tiltmeter_measurement
                                  GROUP BY tiltmeter_id)
                    SELECT tm.*
                    FROM tiltmeter_measurement tm
                             JOIN tiltmeter t ON tm.tiltmeter_id = t.id
                             JOIN gate g ON t.gate_id = g.id
                    WHERE tm.date = (SELECT t.date FROM temp t WHERE t.tiltmeter_id = tm.tiltmeter_id)
                      AND g.reservoir_id = :reservoirId
                    ORDER BY g.ordinal, t.ordinal ;
                    """
    )
    List<TiltmeterMeasurement> getLatestMeasurementsOfReservoir(UUID reservoirId);

    @Query(nativeQuery = true,
    value = """
            SELECT tm.*
            FROM tiltmeter_measurement tm
            WHERE tm.tiltmeter_id = :tiltmeterId
            AND date BETWEEN :startDate AND :endDate
            ORDER BY date DESC ;
            """)
    List<TiltmeterMeasurement> getMeasurementsOfTiltmeterByDates(UUID tiltmeterId,
                                                                 LocalDateTime startDate,
                                                                 LocalDateTime endDate);

}
