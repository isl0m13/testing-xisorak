package com.example.appgidritexmonitoring.entity;

import com.example.appgidritexmonitoring.entity.template.AbsUUIDEntity;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Reservoir extends AbsUUIDEntity {
    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String location;




}
