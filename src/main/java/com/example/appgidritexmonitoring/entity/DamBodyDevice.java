package com.example.appgidritexmonitoring.entity;


import com.example.appgidritexmonitoring.entity.enums.DamBodyDeviceTypeEnum;
import com.example.appgidritexmonitoring.entity.enums.DeviceDatasourceEnum;
import com.example.appgidritexmonitoring.entity.template.AbsUUIDEntity;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
/*@Table(uniqueConstraints =
        {@UniqueConstraint(columnNames = {"device_datasource", "property_datasource"})})*/
public class DamBodyDevice extends AbsUUIDEntity {

    @ManyToOne(optional = false)
    private Gate gate;

    @OneToOne(optional = false)
    private Sensor sensor;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DamBodyDeviceTypeEnum type;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DeviceDatasourceEnum deviceDatasource;

    @Column(nullable = false)
    private String name;

    private Double locationPressure;

    @Column(nullable = false)
    private String propertyDatasource;

    private boolean active = true;


}
