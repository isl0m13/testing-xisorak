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
public class WaterFlowMeter extends AbsUUIDEntity {

    @ManyToOne(optional = false)
    private Reservoir reservoir;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DeviceDatasourceEnum deviceDatasource;

    @Column(nullable = false, unique = true)
    private String pressureDatasource;

    @Column(nullable = false, unique = true)
    private String tempDatasource;

    private String lat;

    private String lon;


}
