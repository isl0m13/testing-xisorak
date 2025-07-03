package com.example.appgidritexmonitoring.entity;

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
public class Spillway extends AbsUUIDEntity {

    @ManyToOne(optional = false)
    private Reservoir reservoir;

    @OneToOne(optional = false)
    private Sensor sensor;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String pressureDatasource;

    private String tempDatasource;

    private String turbidityDatasource;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DeviceDatasourceEnum deviceDatasource;

    private Integer ordinal;

    private boolean active;


}
