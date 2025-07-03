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
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"plumb_id", "date"})},
        indexes = @Index(name = "i_plumb_measurement_id_date", columnList = "plumb_id, date"))
public class PlumbMeasurement extends AbsUUIDEntity {

    @ManyToOne(optional = false)
    private Plumb plumb;

    @ManyToOne(optional = false)
    private ReadFile readFile;

    private LocalDateTime date;

    private Double computedXValue;

    private Double computedYValue;

    private Double tempInDegrees;

}
