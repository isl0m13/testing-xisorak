package com.example.appgidritexmonitoring.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HydrologicalStationDTO {
    private UUID id;
    private String name;
    private Integer ordinal;


}
