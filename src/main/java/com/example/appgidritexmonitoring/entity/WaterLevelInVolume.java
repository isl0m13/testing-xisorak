package com.example.appgidritexmonitoring.entity;

import com.example.appgidritexmonitoring.entity.template.AbsUUIDEntity;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"waterLevel", "reservoir_id"})})
public class WaterLevelInVolume extends AbsUUIDEntity {

    private Double waterLevel;

    private Double volumeVal;
    @ManyToOne(fetch = FetchType.LAZY)
    private Reservoir reservoir;

}
