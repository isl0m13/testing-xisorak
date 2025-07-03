package com.example.appgidritexmonitoring.entity;

import com.example.appgidritexmonitoring.entity.template.AbsUUIDEntity;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@Table(name = "groups")
@SQLDelete(sql = "UPDATE groups SET deleted = true WHERE id = ?")
@Where(clause = "deleted = false")
public class Group extends AbsUUIDEntity {

    @Column(nullable = false)
    private String name;

    @ManyToOne(optional = false)
    private Reservoir reservoir;

    @CreatedDate
    private LocalDateTime createdAt;

    @CreatedBy
    private UUID createdBy;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @LastModifiedBy
    private UUID modifiedBy;

    private boolean deleted = false;


}
