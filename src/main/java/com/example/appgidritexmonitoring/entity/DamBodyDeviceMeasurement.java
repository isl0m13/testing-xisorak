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
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"dam_body_device_id", "date"})},
        indexes = @Index(name = "i_dambodydevice_measurement_id_date", columnList = "dam_body_device_id, date"))
public class DamBodyDeviceMeasurement extends AbsUUIDEntity {

    @ManyToOne(optional = false)
    private DamBodyDevice damBodyDevice;

    @ManyToOne(optional = false)
    private ReadFile readFile;

    private LocalDateTime date;

    private Double readValue;

    private Double computedValue;

}
