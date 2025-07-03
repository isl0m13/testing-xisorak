package com.example.appgidritexmonitoring.repository;

import com.example.appgidritexmonitoring.entity.PiezometerMeasurement;
import com.example.appgidritexmonitoring.entity.SafetyCriteria;
import com.example.appgidritexmonitoring.entity.SpillwayMeasurement;
import com.example.appgidritexmonitoring.payload.projection.SafetyCriteriaValueDTO;
import com.example.appgidritexmonitoring.payload.projection.SafetyCriteriaValueDTO2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface SafetyCriteriaRepository extends JpaRepository<SafetyCriteria, UUID> {

    @Query(
            nativeQuery = true,
            value = """
                    WITH water_level_gauges_values AS (WITH temp AS (SELECT MAX(date)                    AS latest_date,
                                                                            MAX(date) - INTERVAL '1 day' AS datetime_before_1_day,
                                                                            wlg.id                       AS water_level_gauge_id
                                                                     FROM water_level_gauge_measurement wlgm
                                                                     JOIN water_level_gauge wlg ON wlgm.water_level_gauge_id = wlg.id
                                                                     WHERE wlg.ordinal = 3 AND reservoir_id = :reservoirId
                                                                     GROUP BY wlg.id)
                                                       SELECT MAX(CASE WHEN date = t.latest_date THEN computed_pressure END)           AS latest_date_value,
                                                              MAX(CASE WHEN date = t.latest_date THEN computed_pressure END) -
                                                              MAX(CASE WHEN date = t.datetime_before_1_day THEN computed_pressure END) AS pressure_difference
                                                       FROM water_level_gauge_measurement wlgm
                                                       JOIN temp t ON wlgm.water_level_gauge_id = t.water_level_gauge_id),
                        piezometers_gradients AS(
                            SELECT (MAX(CASE WHEN t.piezometer_ordinal = 3
                                AND t.gate_ordinal = 9 THEN computed_pressure END) -
                                    MAX(CASE WHEN t.piezometer_ordinal = 2
                                        AND t.gate_ordinal = 9 THEN computed_pressure END)) / 4 AS gradient_9_gate,
                                   (MAX(CASE WHEN t.piezometer_ordinal = 5
                                       AND t.gate_ordinal = 17 THEN computed_pressure END) -
                                    MAX(CASE WHEN t.piezometer_ordinal = 6
                                        AND t.gate_ordinal = 17 THEN computed_pressure END)) / 40 AS gradient_17_gate,
                                MAX(CASE WHEN t.piezometer_ordinal = 4
                                                  AND t.gate_ordinal = 17 THEN computed_pressure END ) AS s17_p4_value,
                                MAX(CASE WHEN t.piezometer_ordinal = 6
                                       AND t.gate_ordinal = 17 THEN computed_pressure END ) AS s17_p6_value,
                                MAX(CASE WHEN t.piezometer_ordinal = 3
                                       AND t.gate_ordinal = 21 THEN computed_pressure END ) AS s21_p3_value,
                                MAX(CASE WHEN t.piezometer_ordinal = 4
                                       AND t.gate_ordinal = 21 THEN computed_pressure END ) AS s21_p4_value,
                                MAX(CASE WHEN t.piezometer_ordinal = 1
                                       AND t.gate_ordinal = 23 THEN computed_pressure END ) AS s23_p1_value,
                                MAX(CASE WHEN t.piezometer_ordinal = 2
                                       AND t.gate_ordinal = 23 THEN computed_pressure END ) AS s23_p2_value,
                                MAX(CASE WHEN t.piezometer_ordinal = 4
                                       AND t.gate_ordinal = 25 THEN computed_pressure END ) AS s25_p4_value,
                                MAX(CASE WHEN t.piezometer_ordinal = 1
                                       AND t.gate_ordinal = 27 THEN computed_pressure END ) AS s27_p1_value,
                                   MAX(CASE WHEN t.piezometer_ordinal = 3
                                       AND t.gate_ordinal = 27 THEN computed_pressure END ) AS s27_p3_value,
                                   MAX(CASE WHEN t.piezometer_ordinal = 6
                                       AND t.gate_ordinal = 27 THEN computed_pressure END ) AS s27_p6_value,
                                   MAX(CASE WHEN t.piezometer_ordinal = 1
                                       AND t.gate_ordinal = 31 THEN computed_pressure END ) AS s31_p1_value,
                                   MAX(CASE WHEN t.piezometer_ordinal = 6
                                       AND t.gate_ordinal = 19 THEN computed_pressure END ) AS s19_p6_value
                            FROM (SELECT MAX(pm.date) AS date,
                                         pm.piezometer_id,
                                         p.ordinal    AS piezometer_ordinal,
                                         g.ordinal    AS gate_ordinal
                                  FROM piezometer_measurement pm
                                           JOIN piezometer p ON pm.piezometer_id = p.id
                                           JOIN gate g ON p.gate_id = g.id
                                    AND g.reservoir_id = :reservoirId
                                  GROUP BY pm.piezometer_id, p.ordinal, g.ordinal) t
                                  JOIN piezometer_measurement pm ON pm.piezometer_id = t.piezometer_id AND pm.date = t.date
                            LIMIT 1
                        ),
                        spillways_values AS (
                            with temp as (SELECT max(date) as date,
                                                 spillway_id,
                                                 s.ordinal
                                          FROM spillway_measurement sm
                                                   JOIN spillway s ON sm.spillway_id = s.id
                                          WHERE s.reservoir_id = :reservoirId
                                          GROUP BY sm.spillway_id, s.ordinal)
                            SELECT COALESCE(MAX(CASE WHEN t.ordinal = 4 THEN sm.computed_water_flow END), 0.0) s4_spillway_value,
                                   COALESCE(MAX(CASE WHEN t.ordinal = 1 THEN sm.computed_water_flow END), 0.0) s1_spillway_value,
                                   COALESCE(MAX(CASE WHEN t.ordinal = 5 THEN sm.computed_water_flow END), 0.0) s5_spillway_value,
                                   COALESCE(MAX(CASE WHEN t.ordinal = 2 THEN sm.computed_water_flow END), 0.0) s2_spillway_value,
                                   COALESCE(MAX(CASE WHEN t.ordinal = 3 THEN sm.computed_water_flow END), 0.0) s3_spillway_value,
                                   COALESCE(SUM(CASE WHEN t.ordinal IN (4, 1, 5, 2, 3) THEN sm.computed_water_flow END), 0.0) sum_of_spillway_values
                            FROM spillway_measurement sm
                                     JOIN temp t ON sm.spillway_id = t.spillway_id AND sm.date = t.date
                            LIMIT 1
                        ),
                        dam_body_devices_values AS (
                            with temp AS (SELECT MAX(date) AS date,
                                                 dbdm.dam_body_device_id,
                                                 dbd.name
                                          FROM dam_body_device_measurement dbdm
                                                   JOIN dam_body_device dbd ON dbdm.dam_body_device_id = dbd.id
                                          GROUP BY dbdm.dam_body_device_id, dbd.name)
                            SELECT (MAX(CASE WHEN dbdm.date = t.date AND t.name = 'П1-1658' THEN computed_value END) -
                                    MAX(CASE WHEN dbdm.date = t.date AND t.name = 'П2-1655' THEN computed_value END)) / 8 AS pds_difference,
                                   MAX(CASE WHEN t.name = 'П1-1752' THEN dbdm.computed_value END ) pds_p1_1752_value,
                                   MAX(CASE WHEN t.name = 'П1-936' THEN dbdm.computed_value END ) pds_p1_936_value,
                                   MAX(CASE WHEN t.name = 'П2-1751' THEN dbdm.computed_value END ) pds_p2_1751_value,
                                   MAX(CASE WHEN t.name = 'П3-850' THEN dbdm.computed_value END ) pds_p3_850_value,
                                   MAX(CASE WHEN t.name = 'П3-1606' THEN dbdm.computed_value END ) pds_p3_1606_value,
                                   MAX(CASE WHEN t.name = 'П2-1109' THEN dbdm.computed_value END ) pds_p2_1109_value,
                                   MAX(CASE WHEN t.name = 'П2-1159' THEN dbdm.computed_value END ) pds_p2_1159_value,
                                   MAX(CASE WHEN t.name = 'П2-1172' THEN dbdm.computed_value END ) pds_p2_1172_value,
                                   MAX(CASE WHEN t.name = 'П1-1171' THEN dbdm.computed_value END ) pds_p1_1171_value,
                                   MAX(CASE WHEN t.name = 'П2-1153' THEN dbdm.computed_value END ) pds_p2_1153_value,
                                   MAX(CASE WHEN t.name = 'П2-1655' THEN dbdm.computed_value END ) pds_p2_1655_value,
                                   MAX(CASE WHEN t.name = 'П1-1043' THEN dbdm.computed_value END ) pds_p1_1043_value,
                                   MAX(CASE WHEN t.name = 'П3-799' THEN dbdm.computed_value END )  pds_p3_799_value,
                                   MAX(CASE WHEN t.name = 'П2-1156' THEN dbdm.computed_value END ) pds_p2_1156_value,
                                   MAX(CASE WHEN t.name = 'П2-871' THEN dbdm.computed_value END )  pds_p2_871_value,
                                   MAX(CASE WHEN t.name = 'П1-1747' THEN dbdm.computed_value END ) pds_p1_1751_value,
                                   MAX(CASE WHEN t.name = 'Т2-2417' THEN dbdm.computed_value END ) pts_t2_2417_value,
                                   MAX(CASE WHEN t.name = 'Т1-2187' THEN dbdm.computed_value END ) pts_t1_2187_value,
                                   MAX(CASE WHEN t.name = 'Г1-1045' THEN dbdm.computed_value END ) pngs_g1_1045_value,
                                   MAX(CASE WHEN t.name = 'Г3-775' THEN dbdm.computed_value END )  pngs_g3_775_value,
                                   MAX(CASE WHEN t.name = 'Г1-777' THEN dbdm.computed_value END )  pngs_g1_777_value,
                                   MAX(CASE WHEN t.name = 'Г1-893' THEN dbdm.computed_value END )  pngs_g1_893_value
                            FROM dam_body_device_measurement dbdm
                                     JOIN temp t ON t.dam_body_device_id = dbdm.dam_body_device_id AND dbdm.date = t.date
                            LIMIT 1
                        )
                    SELECT wlgv.latest_date_value AS water_level_gauge_last_value,
                           wlgv.pressure_difference AS water_level_gauge_difference,
                           dbdv.pds_difference AS pds_difference_value,
                           pg.gradient_9_gate AS ninth_gate_piezometer_gr,
                           pg.gradient_17_gate AS seventeenth_gate_piezometer_gr,
                           sv.s4_spillway_value AS spillway_d1_value,
                           sv.s1_spillway_value AS spillway_d2_value,
                           sv.s5_spillway_value AS spillway_ls2_value,
                           0.0 AS k6_spillway_value,
                           sv.s2_spillway_value AS hd_spillway_value,
                           sv.s3_spillway_value AS trt_spillway_value,
                           sv.sum_of_spillway_values AS sum_of_spillways_values,
                           dbdv.pds_p1_1752_value AS pds_p1_m1752_value,
                           dbdv.pds_p1_936_value AS pds_p1_m936_value,
                           dbdv.pds_p2_1751_value AS pds_p2_m1751_value,
                           dbdv.pds_p3_850_value AS pds_p3_m850_value,
                           dbdv.pds_p3_1606_value AS pds_p3_m1606_value,
                           dbdv.pds_p2_1109_value AS pds_p2_m1109_value,
                           dbdv.pds_p2_1159_value AS pds_p2_m1159_value,
                           dbdv.pds_p2_1172_value AS pds_p2_m1172_value,
                           dbdv.pds_p1_1171_value AS pds_p1_m1171_value,
                           dbdv.pds_p2_1153_value AS pds_p2_m1153_value,
                           dbdv.pds_p2_1655_value AS pds_p2_m1655_value,
                           dbdv.pds_p1_1043_value AS pds_p1_m1043_value,
                           dbdv.pds_p3_799_value AS  pds_p3_m799_value,
                           dbdv.pds_p2_1156_value AS pds_p2_m1156_value,
                           dbdv.pds_p2_871_value AS  pds_p2_m871_value,
                           dbdv.pds_p1_1751_value AS pds_p1_m1751_value,
                           dbdv.pts_t2_2417_value AS pts_t2_m2417_value,
                           dbdv.pts_t1_2187_value AS pts_t1_m2187_value,
                           dbdv.pngs_g1_1045_value AS pngs_g1_m1045_value,
                           dbdv.pngs_g3_775_value AS pngs_g3_m775_value,
                           dbdv.pngs_g1_777_value AS pngs_g1_m777_value,
                           dbdv.pngs_g1_893_value AS pngs_g1_m893_value,
                           pg.s17_p4_value AS s17_p4_value,
                           pg.s17_p6_value AS s17_p6_value,
                           pg.s21_p3_value AS s21_p3_value,
                           pg.s21_p4_value AS s21_p4_value,
                           pg.s23_p1_value AS s23_p1_value,
                           pg.s23_p2_value AS s23_p2_value,
                           pg.s25_p4_value AS s25_p4_value,
                           pg.s27_p1_value AS s27_p1_value,
                           pg.s27_p3_value AS s27_p3_value,
                           pg.s27_p6_value AS s27_p6_value,
                           pg.s31_p1_value AS s31_p1_value,
                           pg.s19_p6_value AS s19_p6_value
                    FROM dam_body_devices_values dbdv
                    JOIN piezometers_gradients pg ON true
                    JOIN water_level_gauges_values wlgv ON true
                    JOIN spillways_values sv ON true ;
                    """
    )
    SafetyCriteriaValueDTO getValuesOfReservoir(UUID reservoirId);


    List<SafetyCriteria> findAllByReservoirIdOrderByOrdinal(UUID reservoirId);


}
