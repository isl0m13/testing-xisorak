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
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"piezometer_id", "date"})},
        indexes = @Index(name = "i_piezometer_measurement_id_date", columnList = "piezometer_id, date"))
public class PiezometerMeasurement extends AbsUUIDEntity {

    @ManyToOne(optional = false)
    private Piezometer piezometer;

    @ManyToOne(optional = false)
    private ReadFile readFile;

    private LocalDateTime date;

    private Double readPressure;

    private Double virtualPressure;

    private Double computedPressure;

    private Double tempInDegrees;

    private Double tempInOhms;

}


//{Piezometer, readPressure, (tempInDegress, tempinOhms))



