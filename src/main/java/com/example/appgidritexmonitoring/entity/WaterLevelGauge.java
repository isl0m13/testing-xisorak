package com.example.appgidritexmonitoring.entity;


import com.example.appgidritexmonitoring.entity.enums.DeviceDatasourceEnum;
import com.example.appgidritexmonitoring.entity.enums.WaterLevelGaugeEnum;
import com.example.appgidritexmonitoring.entity.template.AbsUUIDEntity;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WaterLevelGauge extends AbsUUIDEntity {

    @ManyToOne(optional = false)
    private Reservoir reservoir;

    @OneToOne(optional = false)
    private Sensor sensor;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private WaterLevelGaugeEnum type;

    private Integer ordinal;

    @Column(nullable = false, unique = true)
    private String pressureDatasource;

    private String tempDatasource;

    @Column(nullable = false)
    private Double locationPressure;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DeviceDatasourceEnum deviceDatasource;



}
