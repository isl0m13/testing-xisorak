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
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"water_level_gauge_id", "date"})},
        indexes = @Index(name = "i_waterlevelgauge_measurement_id_date", columnList = "water_level_gauge_id, date"))
public class WaterLevelGaugeMeasurement extends AbsUUIDEntity {

    @ManyToOne(optional = false)
    private WaterLevelGauge waterLevelGauge;

    @ManyToOne(optional = false)
    private ReadFile readFile;

    private LocalDateTime date;

    private Double readPressure;

    private Double computedPressure;

    private Double volumeValue;

    private Double virtualPressure;

    private Double tempInDegrees;

    private Double tempInOhms;


}


//{Piezometer, readPressure, (tempInDegress, tempinOhms))



