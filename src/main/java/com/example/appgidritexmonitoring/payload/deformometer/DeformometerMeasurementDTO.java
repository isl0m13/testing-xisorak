package com.example.appgidritexmonitoring.payload.deformometer;

import com.example.appgidritexmonitoring.entity.Deformometer;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeformometerMeasurementDTO {

    private DeformometerDTO deformometer;

    private LocalDateTime date;

    private Double displacementValue;

    private Double temperature;

}
