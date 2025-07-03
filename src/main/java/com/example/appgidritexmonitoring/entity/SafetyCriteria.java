package com.example.appgidritexmonitoring.entity;

import com.example.appgidritexmonitoring.entity.enums.SafetyCriteriaStatusEnum;
import com.example.appgidritexmonitoring.entity.template.AbsUUIDEntity;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SafetyCriteria extends AbsUUIDEntity {

    @Column(nullable = false)
    private String name;

    private String methodName;

    @ManyToOne(optional = false)
    private Reservoir reservoir;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SafetyCriteriaStatusEnum status;

    @Column(unique = true, nullable = false)
    private String indicationSourceDB;

    @Column(nullable = false)
    private Integer ordinal;



}
