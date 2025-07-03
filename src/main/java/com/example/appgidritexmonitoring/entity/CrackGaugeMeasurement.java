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
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"crack_gauge_id", "date"})},
        indexes = @Index(name = "i_crack_gauge_measurement_id_date", columnList = "crack_gauge_id, date"))
public class CrackGaugeMeasurement extends AbsUUIDEntity {

    @ManyToOne(optional = false)
    private CrackGauge crackGauge;

    @ManyToOne(optional = false)
    private ReadFile readFile;

    private LocalDateTime date;

    private Double readValue;

    private Double computedValue;

    private Double tempInDegrees;

}
