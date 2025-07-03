package com.example.appgidritexmonitoring.repository;

import com.example.appgidritexmonitoring.entity.DeformometerMeasurement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface DeformometerMeasurementRepository extends JpaRepository<DeformometerMeasurement, UUID> {

    boolean existsByDeformometerIdAndDate(UUID deformometerId, LocalDateTime date);

    @Query(
            nativeQuery = true,
            value = """
                    with temp as (SELECT MAX(date) AS date, deformometer_id
                                  FROM deformometer_measurement
                                  GROUP BY deformometer_id)
                    SELECT dm.*
                    FROM deformometer_measurement dm
                    JOIN deformometer d ON d.id = dm.deformometer_id
                    WHERE d.reservoir_id = :reservoir_id
                    AND dm.date = (SELECT t.date FROM temp t WHERE t.deformometer_id = dm.deformometer_id)
                    ORDER BY d.ordinal ;
                    """
    )
    List<DeformometerMeasurement> getLatestMeasurementsOfReservoir(UUID reservoir_id);


    @Query(
            nativeQuery = true,
            value = """
                    SELECT dm.*
                    FROM deformometer_measurement dm
                    JOIN deformometer d ON dm.deformometer_id = d.id
                    WHERE d.reservoir_id = :reservoirId AND date BETWEEN :startDate AND :endDate
                    ORDER BY date DESC, d.ordinal ;
                    """
    )
    List<DeformometerMeasurement> getMeasurementsOfReservoirByDates(UUID reservoirId,
                                                                    LocalDateTime startDate,
                                                                    LocalDateTime endDate);

    @Query(
            nativeQuery = true,
            value = """
                    SELECT dm.*
                    FROM deformometer_measurement dm
                    WHERE deformometer_id = :deformometerId
                    AND date BETWEEN :startDate AND :endDate
                    ORDER BY date DESC ;
                    """
    )
    List<DeformometerMeasurement> getMeasurementsOfDeformometerByDates(UUID deformometerId,
                                                                       LocalDateTime startDate,
                                                                       LocalDateTime endDate);

}
