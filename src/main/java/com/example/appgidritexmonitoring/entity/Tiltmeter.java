package com.example.appgidritexmonitoring.entity;

import com.example.appgidritexmonitoring.entity.enums.DeviceDatasourceEnum;
import com.example.appgidritexmonitoring.entity.enums.PiezometerTypeEnum;
import com.example.appgidritexmonitoring.entity.template.AbsUUIDEntity;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Tiltmeter extends AbsUUIDEntity {

    @Column(nullable = false)
    private String name;

    @ManyToOne(optional = false)
    private Gate gate;

    @Column(nullable = false)
    private Integer ordinal;

    @OneToOne(optional = false)
    private Sensor sensor;

    @Column(nullable = false, unique = true)
    private String sinADatasource;

    @Column(nullable = false, unique = true)
    private String sinBDatasource;

    @Column(nullable = false, unique = true)
    private String tempDatasource;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeviceDatasourceEnum deviceDatasource;

    private boolean active;

}
