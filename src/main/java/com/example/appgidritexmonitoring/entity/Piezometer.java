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
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"device_datasource", "pressure_datasource"}),
        @UniqueConstraint(columnNames = {"device_datasource", "temp_datasource"})
})
public class Piezometer extends AbsUUIDEntity {
    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PiezometerTypeEnum type;

    @ManyToOne(optional = false)
    private Gate gate;

    @Column(nullable = false)
    private Integer ordinal;

    @OneToOne(optional = false)
    private Sensor sensor;

    @Column(nullable = false)
    private Double locationPressure;

    private String location;

    private String lat;

    private String lon;

    @Column(nullable = false, name = "pressure_datasource")
    private String pressureDatasource;

    @Column(nullable = false, name = "temp_datasource")
    private String tempDatasource;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "device_datasource")
    private DeviceDatasourceEnum deviceDatasource;

    private boolean active;




}
