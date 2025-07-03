package com.example.appgidritexmonitoring.entity;

import com.example.appgidritexmonitoring.entity.template.AbsUUIDEntity;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.math.BigDecimal;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Sensor extends AbsUUIDEntity {
    //to do unique constraint

    private String serialNumber;

    @Column(precision = 50, scale = 30)
    private Double ACoefficient;

    @Column(precision = 50, scale = 30)
    private Double BCoefficient;

    @Column(scale = 30)
    private Double CCoefficient;

    @Column(precision = 50, scale = 30)
    private Double DCoefficient;

    private String datalogger;

    private Integer multiplexer;

    private Double correction;

    private boolean active = true;




}
