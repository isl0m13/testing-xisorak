package com.example.appgidritexmonitoring.entity;

import com.example.appgidritexmonitoring.entity.enums.PermissionEnum;
import com.example.appgidritexmonitoring.entity.template.AbsIntegerEntity;
import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Role extends AbsIntegerEntity {

    @Column(nullable = false, unique = true)
    private String name;

    @Column(length = 500)
    private String description;

    @CollectionTable(name = "role_permission",
            joinColumns =
                    @JoinColumn(name = "role_id", referencedColumnName = "id")
    )
    @Enumerated(EnumType.STRING)
    @ElementCollection
    @Column(name = "permission", nullable = false)
    private Set<PermissionEnum> permission;

}
