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
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"water_flow_meter_id", "date"})},
        indexes = @Index(name = "i_waterflowmeter_measurement_id_date", columnList = "water_flow_meter_id, date"))
public class WaterFlowMeterMeasurement extends AbsUUIDEntity {

    @ManyToOne(optional = false)
    private WaterFlowMeter waterFlowMeter;

    @ManyToOne(optional = false)
    private ReadFile readFile;

    private LocalDateTime date;

    private Double readPressure;

    private Double computedWaterFlow;

    private Double tempInDegrees;


}
