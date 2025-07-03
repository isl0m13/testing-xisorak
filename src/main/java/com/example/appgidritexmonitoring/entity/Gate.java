package com.example.appgidritexmonitoring.entity;

import com.example.appgidritexmonitoring.entity.template.AbsUUIDEntity;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Gate extends AbsUUIDEntity {

    private Double ordinal;

    private String name;

    @ManyToOne(optional = false)
    private Reservoir reservoir;

    public static Gate make (Double ordinal, String name, Reservoir reservoir) {
        return Gate.builder()
                .ordinal(ordinal)
                .name(name)
                .reservoir(reservoir)
                .build();
    }

}
