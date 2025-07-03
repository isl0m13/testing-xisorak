package com.example.appgidritexmonitoring.entity;

import com.example.appgidritexmonitoring.entity.template.AbsUUIDEntity;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"hydrological_station_id", "date"})},
        indexes = @Index(name = "i_hydrological_station_measurement_id_date", columnList = "hydrological_station_id, date"))
public class HydrologicalStationMeasurement extends AbsUUIDEntity {

    @ManyToOne(optional = false)
    private HydrologicalStation hydrologicalStation;

    @ManyToOne(optional = false)
    private ReadFile readFile;

    private LocalDateTime date;

    private Double readPressure;

    private Double virtualPressure;

    private Double computedWaterFlow;

    private Double tempInDegrees;

}
