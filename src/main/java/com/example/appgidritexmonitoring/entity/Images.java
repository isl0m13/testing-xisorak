package com.example.appgidritexmonitoring.entity;

import com.example.appgidritexmonitoring.entity.template.AbsUUIDEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Images extends AbsUUIDEntity {

    private String smallPhotoName;

    private Long smallPhotoSize;

    private String mediumPhotoName;

    private Long mediumPhotoSize;

    @OneToOne(optional = false)
    private Attachment attachment;

    public Images(Attachment attachment) {
        this.attachment = attachment;
    }
}
