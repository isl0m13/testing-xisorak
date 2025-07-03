package com.example.appgidritexmonitoring.entity;


import com.example.appgidritexmonitoring.entity.enums.DeviceDatasourceEnum;
import com.example.appgidritexmonitoring.entity.template.AbsUUIDEntity;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(indexes = @Index(name = "i_read_file_name_datasource", columnList = "name, datasource"))
public class ReadFile extends AbsUUIDEntity {

    @Column(nullable = false)
    private String name;

    private LocalDateTime date;

    @Column(nullable = false)
    private String path;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeviceDatasourceEnum datasource;

    @ManyToOne(optional = false)
    private Reservoir reservoir;


}


