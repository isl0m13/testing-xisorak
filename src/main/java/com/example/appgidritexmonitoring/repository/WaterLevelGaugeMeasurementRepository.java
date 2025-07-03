package com.example.appgidritexmonitoring.repository;

import com.example.appgidritexmonitoring.entity.WaterLevelGaugeMeasurement;
import com.example.appgidritexmonitoring.payload.projection.WaterLevelGaugeMeasurementDiffProjection;
import com.example.appgidritexmonitoring.payload.projection.WaterLevelGaugeMeasurementProj;
import com.example.appgidritexmonitoring.payload.projection.WaterLevelGaugeVolumeProj;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WaterLevelGaugeMeasurementRepository extends JpaRepository<WaterLevelGaugeMeasurement, UUID> {

    /*@Query(nativeQuery = true,
            value = """
                    WITH temp AS (
                        SELECT
                            wlgm.date AS datetime,
                            wlgm.date - INTERVAL '1 day' AS datetime_before_1_day,
                            wlgm.date - INTERVAL '1 day 1 hour' AS datetime_before_1_day_1_hour,
                            wlg.id AS water_level_gauge_id,
                            wlg.ordinal AS ordinal,
                            wlg.location_pressure AS location_pressure
                        FROM water_level_gauge_measurement wlgm
                                 JOIN water_level_gauge wlg ON wlgm.water_level_gauge_id = wlg.id
                        WHERE wlg.reservoir_id = :reservoirId
                          AND wlgm.date BETWEEN :startDate AND :endDate
                        ORDER BY wlgm.date DESC
                    )
                    SELECT
                        CAST(t.water_level_gauge_id AS VARCHAR) AS water_level_gauge_id,
                        t.ordinal as ordinal,
                        MAX(t.location_pressure) as location_pressure,
                        t.datetime AS date,
                        MAX(CASE WHEN wlgm.date = t.datetime THEN wlgm.computed_pressure END) AS computed_pressure,
                        MAX(CASE WHEN wlgm.date = t.datetime THEN wlgm.virtual_pressure END) AS virtual_pressure,
                        MAX(CASE WHEN wlgm.date = t.datetime THEN wlgm.volume_value END) AS volume_value,
                        COALESCE(
                                MAX(CASE WHEN wlgm.date = t.datetime THEN wlgm.computed_pressure END), 0
                        ) -
                        COALESCE(
                                MAX(CASE WHEN wlgm.date = t.datetime_before_1_day THEN wlgm.computed_pressure END),
                                MAX(CASE WHEN wlgm.date = t.datetime_before_1_day_1_hour THEN wlgm.computed_pressure END)
                        ) AS pressure_diff
                    FROM water_level_gauge_measurement wlgm
                             JOIN temp t ON t.water_level_gauge_id = wlgm.water_level_gauge_id
                    GROUP BY t.water_level_gauge_id, t.ordinal, t.datetime
                    ORDER BY t.datetime DESC, ordinal ;
                    """)
    List<WaterLevelGaugeMeasurementProj> getMeasurementsByDatesOfReservoir(UUID reservoirId,
                                                                           LocalDateTime startDate,
                                                                           LocalDateTime endDate);*/

    @Query(
            nativeQuery = true,
            value = """
                    SELECT wlgm.*
                    FROM water_level_gauge_measurement wlgm
                    JOIN water_level_gauge wlg ON wlgm.water_level_gauge_id = wlg.id
                    WHERE  reservoir_id = :reservoirId
                      AND date BETWEEN :startDate AND :endDate
                    ORDER BY date DESC, wlg.ordinal;
                    """
    )
    List<WaterLevelGaugeMeasurement> getAllMeasurementsByDates(UUID reservoirId,
                                                               LocalDateTime startDate,
                                                               LocalDateTime endDate);


    @Query(nativeQuery = true,
            value = """
                       with temp AS (SELECT water_level_gauge_id, max(date) latest_date
                                     FROM water_level_gauge_measurement wlgm
                                              JOIN water_level_gauge wlg ON wlgm.water_level_gauge_id = wlg.id
                                     WHERE wlg.reservoir_id = :reservoirId
                                     GROUP BY water_level_gauge_id, wlg.ordinal
                                     ORDER BY wlg.ordinal)
                       SELECT wlgm.*
                       FROM water_level_gauge_measurement wlgm
                                JOIN temp ON wlgm.water_level_gauge_id = temp.water_level_gauge_id
                           AND wlgm.date = temp.latest_date;
                    """)
    List<WaterLevelGaugeMeasurement> getLatestMeasurementsOfReservoir(UUID reservoirId);


    @Query(nativeQuery = true,
            value = """
                    SELECT wlgm.*
                    FROM water_level_gauge_measurement wlgm
                             JOIN water_level_gauge wlg ON wlgm.water_level_gauge_id = wlg.id
                    WHERE wlg.ordinal = 3 AND reservoir_id = :reservoirId
                         AND date BETWEEN :startDate AND :endDate
                    ORDER BY date DESC ;
                    """)
    List<WaterLevelGaugeMeasurement> getAllByReservoir(UUID reservoirId,
                                                       LocalDateTime startDate,
                                                       LocalDateTime endDate);

    @Query(nativeQuery = true,
    value = """
            SELECT wlgm.*
            FROM water_level_gauge_measurement wlgm
                     JOIN water_level_gauge wlg ON wlgm.water_level_gauge_id = wlg.id
            WHERE wlg.ordinal = 3 AND reservoir_id = :reservoirId
              AND date BETWEEN :startDate AND :endDate
              AND EXTRACT(HOUR FROM date) = 5 AND EXTRACT(MINUTE FROM date) = 0
            ORDER BY date DESC ;
            """)
    List<WaterLevelGaugeMeasurement> getMeasurementsOfLastWlgByReservoirId(UUID reservoirId,
                                                                           LocalDateTime startDate,
                                                                           LocalDateTime endDate);

    boolean existsByWaterLevelGaugeIdAndDate(UUID waterLevelGaugeId, LocalDateTime date);


    @Query(nativeQuery = true,
            value = """
                    WITH temp AS (SELECT date                           AS datetime,
                                         date - INTERVAL '1 day'        AS datetime_before_1_day,
                                         date - INTERVAL '1 day 1 hour' AS datetime_before_1_day_1_hour,
                                         wlg.id                         AS water_level_gauge_id,
                                         wlg.ordinal                    AS ordinal,
                                         wlg.location_pressure          AS location_pressure
                                  FROM water_level_gauge_measurement wlgm
                                           JOIN water_level_gauge wlg ON wlgm.water_level_gauge_id = wlg.id
                                  WHERE date_part('hour', date) = 6 AND date_part('minute', date) = 0 AND
                                      reservoir_id = :reservoirId AND
                                      date BETWEEN :startDate AND :endDate
                                  ORDER BY date DESC)
                    SELECT
                        CAST(t.water_level_gauge_id AS VARCHAR) AS water_level_gauge_id,
                        t.ordinal AS ordinal,
                        MAX(t.location_pressure) AS location_pressure,
                        t.datetime AS date,
                        MAX(CASE WHEN wlgm.date = t.datetime THEN wlgm.virtual_pressure END) AS virtual_pressure,
                        COALESCE(
                                MAX(CASE WHEN date = t.datetime THEN computed_pressure END), 0) -
                        COALESCE(
                                MAX(CASE WHEN date = t.datetime_before_1_day THEN computed_pressure END),
                                MAX(CASE WHEN date = t.datetime_before_1_day_1_hour THEN computed_pressure END)) AS pressure_diff
                    FROM water_level_gauge_measurement wlgm
                             JOIN temp t ON t.water_level_gauge_id = wlgm.water_level_gauge_id
                    GROUP BY t.water_level_gauge_id, t.ordinal, t.datetime
                    ORDER BY date DESC, t.ordinal ;  
                    """)
    List<WaterLevelGaugeMeasurementDiffProjection> getDifferenceMeasurementsByDate(UUID reservoirId,
                                                                                   LocalDateTime startDate,
                                                                                   LocalDateTime endDate);

    @Query(
            nativeQuery = true,
            value = """
                    WITH temp AS (SELECT MAX(date)                           AS datetime,
                                         MAX(date - INTERVAL '1 day')        AS datetime_before_1_day,
                                         MAX(date - INTERVAL '1 day 1 hour') AS datetime_before_1_day_1_hour,
                                         wlgm.water_level_gauge_id           AS water_level_gauge_id
                                  FROM water_level_gauge_measurement wlgm
                                           JOIN water_level_gauge wlg ON wlgm.water_level_gauge_id = wlg.id
                                  WHERE reservoir_id = :reservoirId
                                  GROUP BY wlgm.water_level_gauge_id)
                    
                    SELECT CAST(t.water_level_gauge_id AS VARCHAR) AS water_level_gauge_id,
                           t.datetime AS date,
                           COALESCE(MAX(CASE WHEN wlgm.date = t.datetime THEN wlgm.computed_pressure END), 0) -
                           COALESCE(MAX(CASE WHEN wlgm.date = t.datetime_before_1_day THEN wlgm.computed_pressure END),
                                    MAX(CASE WHEN wlgm.date = t.datetime_before_1_day_1_hour THEN wlgm.computed_pressure END)) AS pressure_diff
                    FROM water_level_gauge_measurement wlgm
                             JOIN temp t ON t.water_level_gauge_id = wlgm.water_level_gauge_id
                    GROUP BY t.water_level_gauge_id, t.datetime;
                    """
    )
    List<WaterLevelGaugeMeasurementDiffProjection> getLatestDiffMeasurementOfReservoir(UUID reservoirId);

    List<WaterLevelGaugeMeasurement> findAllByWaterLevelGaugeId(UUID waterLevelGaugeId);

    @Query(nativeQuery = true,
            value = """
                    WITH measurement_diff AS (
                        SELECT wl1.water_level_gauge_id,
                               wl1.date AS start_time,
                               wl2.date AS end_time,
                               (wl2.volume_value - wl1.volume_value) / 21600 AS volume_per_6_hours
                        FROM water_level_gauge_measurement wl1
                                 JOIN water_level_gauge_measurement wl2
                                      ON wl1.water_level_gauge_id = wl2.water_level_gauge_id
                                          AND wl2.date = wl1.date + INTERVAL '6 hours'
                                 JOIN water_level_gauge wlg ON wl1.water_level_gauge_id = wlg.id
                        WHERE wlg.reservoir_id = :reservoirId
                          AND EXTRACT(HOUR FROM wl1.date) IN (6, 12, 18, 0)
                          AND EXTRACT(MINUTE FROM wl1.date) = 0
                    ),
                         latest_measurements AS (
                             SELECT *,
                                    ROW_NUMBER() OVER (PARTITION BY water_level_gauge_id ORDER BY end_time DESC) AS rn
                             FROM measurement_diff
                         )
                    SELECT CAST(water_level_gauge_id AS VARCHAR) AS water_level_gauge_id,
                           end_time as date,
                           volume_per_6_hours AS volume_in_second
                    FROM latest_measurements lm
                    WHERE rn = 1 ;
                    """)
    List<WaterLevelGaugeVolumeProj> getLatestVolumesByWaterLevelGauges(UUID reservoirId);


}
