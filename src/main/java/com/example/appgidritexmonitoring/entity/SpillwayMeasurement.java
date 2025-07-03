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
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"spillway_id", "date"})},
        indexes = @Index(name = "i_spillway_measurement_id_date", columnList = "spillway_id, date"))
public class SpillwayMeasurement extends AbsUUIDEntity {
    @ManyToOne(optional = false)
    private Spillway spillway;

    @ManyToOne(optional = false)
    private ReadFile readFile;

    @Column(nullable = false)
    private LocalDateTime date;

    @Column(nullable = false)
    private Double readPressure;

    @Column(nullable = false)
    private Double computedWaterFlow;

    private Double turbidityValue;

    private Double tempInDegrees;

    private Double tempInOhms;

}
