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
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"deformometer_id", "date"})},
        indexes = @Index(name = "i_deformometer_measurement_id_date", columnList = "deformometer_id, date"))
public class DeformometerMeasurement extends AbsUUIDEntity {

    @ManyToOne(optional = false)
    private Deformometer deformometer;

    @ManyToOne(optional = false)
    private ReadFile readFile;

    private LocalDateTime date;

    private Double readValue;

    private Double computedValue;

    private Double tempInDegrees;

    private Double tempInOhms;

}
