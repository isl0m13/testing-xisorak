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
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"tiltmeter_id", "date"})},
        indexes = @Index(name = "i_tiltmeter_measurement_id_date", columnList = "tiltmeter_id, date"))
public class TiltmeterMeasurement extends AbsUUIDEntity {

    @ManyToOne(optional = false)
    private Tiltmeter tiltmeter;

    @ManyToOne(optional = false)
    private ReadFile readFile;

    private LocalDateTime date;

    private Double sinAValue;

    private Double sinBValue;

    private Double tempInDegrees;

    private Double computedTiltValue;



}
