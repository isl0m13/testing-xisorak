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
public class HydrologicalStation  extends AbsUUIDEntity {

    @ManyToOne(optional = false)
    private Reservoir reservoir;

    @OneToOne(optional = false)
    private Sensor sensor;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private Integer ordinal;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DeviceDatasourceEnum deviceDatasource;

    @Column(nullable = false, unique = true)
    private String pressureDatasource;

    @Column(nullable = false, unique = true)
    private String tempDatasource;

    @Column(nullable = false)
    private Double localPressure;

    private String lat;

    private String lon;

    private boolean active;

}
