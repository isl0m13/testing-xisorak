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
public class CrackGauge extends AbsUUIDEntity {

    @Column(nullable = false)
    private String name;

    @ManyToOne(optional = false)
    private Gate gate;

    @OneToOne(optional = false)
    private Sensor sensor;

    private Integer ordinal;

    @Column(nullable = false, unique = true)
    private String mDatasource;

    private String tempDatasource;

    @Column(nullable = false)
    private Double locationPressure;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DeviceDatasourceEnum deviceDatasource;

    @Column(nullable = false)
    private String location;



}
