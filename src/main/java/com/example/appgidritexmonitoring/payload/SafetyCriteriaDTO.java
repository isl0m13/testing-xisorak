package com.example.appgidritexmonitoring.payload;

import com.example.appgidritexmonitoring.entity.enums.SafetyCriteriaStatusEnum;
import lombok.*;

import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SafetyCriteriaDTO {

    private UUID id;

    private String name;

    private Integer status;

}
