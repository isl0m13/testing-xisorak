package com.example.appgidritexmonitoring.payload.piezometer;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.UUID;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PiezometerDataDTO {
    private UUID id;

    private Integer ordinal;

    private String name;

    private double hydroPressure;

    private double virtualPressure;

    private double temperature;
}
