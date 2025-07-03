package com.example.appgidritexmonitoring.repository;

import com.example.appgidritexmonitoring.entity.CrackGaugeMeasurement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface CrackGaugeMeasurementRepository extends JpaRepository<CrackGaugeMeasurement, UUID> {

    boolean existsByCrackGaugeIdAndDate(UUID crackGaugeId, LocalDateTime date);

    @Query(
            nativeQuery = true,
            value = """
                    with temp AS (SELECT crack_gauge_id, MAX(date) date
                                  FROM crack_gauge_measurement
                                  GROUP BY crack_gauge_id)
                    SELECT cgm.*
                    FROM crack_gauge_measurement cgm
                    JOIN crack_gauge cg ON cgm.crack_gauge_id = cg.id
                    WHERE cgm.date = (SELECT t.date FROM temp t WHERE t.crack_gauge_id = cg.id)
                      AND cg.location = :location
                    ORDER BY cg.ordinal, cg.name ;
                    """
    )
    List<CrackGaugeMeasurement> getLatestMeasurementsOfLocation(String location);


    @Query(
            nativeQuery = true,
            value = """
                    SELECT cgm.*
                    FROM crack_gauge_measurement cgm
                    JOIN crack_gauge cg ON cgm.crack_gauge_id = cg.id
                    WHERE cg.ordinal = :ordinal AND cg.location = :location
                      AND cgm.date BETWEEN :startDate AND :endDate
                    ORDER BY date DESC, cg.name ;
                    """
    )
    List<CrackGaugeMeasurement> getOfOrdinalAndLocationByDates(Integer ordinal,
                                                               String location,
                                                               LocalDateTime startDate,
                                                               LocalDateTime endDate);


}
