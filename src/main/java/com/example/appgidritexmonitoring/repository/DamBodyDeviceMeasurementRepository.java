package com.example.appgidritexmonitoring.repository;

import com.example.appgidritexmonitoring.entity.DamBodyDeviceMeasurement;
import com.example.appgidritexmonitoring.entity.enums.DamBodyDeviceTypeEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface DamBodyDeviceMeasurementRepository extends JpaRepository<DamBodyDeviceMeasurement, UUID> {

    boolean existsByDamBodyDeviceIdAndDate(UUID damBodyDeviceId, LocalDateTime date);

    @Query(
            nativeQuery = true,
            value = """
                    with temp AS (SELECT max(date) AS date, dbdm.dam_body_device_id
                                  FROM dam_body_device_measurement dbdm
                                  GROUP BY dbdm.dam_body_device_id)
                    SELECT dbdm.*
                    FROM dam_body_device_measurement dbdm
                    JOIN dam_body_device dbd ON dbdm.dam_body_device_id = dbd.id
                    JOIN gate g ON dbd.gate_id = g.id
                    JOIN temp t ON t.dam_body_device_id = dbdm.dam_body_device_id AND dbdm.date = t.date
                    WHERE reservoir_id = :reservoirId
                    ORDER BY g.ordinal, dbd.type, dbdm.date DESC ;
                    """
    )
    List<DamBodyDeviceMeasurement> getLatestMeasurementsOfReservoir(UUID reservoirId);


    @Query(
            nativeQuery = true,
            value = """
                    with temp AS (SELECT max(date) AS date, dbdm.dam_body_device_id
                                  FROM dam_body_device_measurement dbdm
                                           JOIN dam_body_device dbd ON dbdm.dam_body_device_id = dbd.id
                                  WHERE gate_id = :gateId
                                  GROUP BY dbdm.dam_body_device_id)
                    SELECT dbdm.*
                    FROM dam_body_device_measurement dbdm
                             JOIN dam_body_device dbd ON dbdm.dam_body_device_id = dbd.id
                             JOIN temp t ON t.dam_body_device_id = dbdm.dam_body_device_id AND dbdm.date = t.date
                    WHERE gate_id = :gateId
                    ORDER BY dbd.type, dbdm.date DESC ;
                    """
    )
    List<DamBodyDeviceMeasurement> getLatestMeasurementsOfGate(UUID gateId);

    @Query(
            nativeQuery = true,
            value = """
                    SELECT dbdm.*
                    FROM dam_body_device_measurement dbdm
                    JOIN dam_body_device dbd  ON dbdm.dam_body_device_id = dbd.id
                    WHERE gate_id = :gateId AND dbd.type = :deviceType
                      AND dbdm.date BETWEEN :startDate AND :endDate
                    ORDER BY date DESC ;
                    """
    )
    List<DamBodyDeviceMeasurement> getMeasurementsOfDeviceType(UUID gateId,
                                                               String deviceType,
                                                               LocalDateTime startDate,
                                                               LocalDateTime endDate);



}
