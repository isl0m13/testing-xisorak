package com.example.appgidritexmonitoring.entity;

import com.example.appgidritexmonitoring.entity.enums.DeviceDatasourceEnum;
import com.example.appgidritexmonitoring.entity.enums.PlumbTypeEnum;
import com.example.appgidritexmonitoring.entity.template.AbsUUIDEntity;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Plumb extends AbsUUIDEntity {

    @Column(nullable = false)
    private String name;

    @ManyToOne(optional = false)
    private Gate gate;

    @OneToOne
    private Sensor sensor;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PlumbTypeEnum type;

    @Column(nullable = false, unique = true)
    private String xDatasource;

    @Column(nullable = false, unique = true)
    private String yDatasource;

    private String tempDatasource;

    @Column(nullable = false)
    private Double locationPressure;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DeviceDatasourceEnum deviceDatasource;



}
