package com.example.appgidritexmonitoring.payload.piezometer;

import com.example.appgidritexmonitoring.payload.Coordinates;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PiezometerMeasurementDTO extends PiezometerDataDTO {

    private LocalDateTime date;

    private Coordinates coordinates;



}
