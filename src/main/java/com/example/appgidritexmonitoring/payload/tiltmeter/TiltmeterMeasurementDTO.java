package com.example.appgidritexmonitoring.payload.tiltmeter;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TiltmeterMeasurementDTO extends TiltmeterDTO {

    private LocalDateTime date;

    private Double tiltValue;

    private Double sinA;

    private Double sinB;

    private Double temp;

}
