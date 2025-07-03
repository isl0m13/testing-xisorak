package com.example.appgidritexmonitoring.payload.spillway;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Locale;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SpillwayMeasurementDTO {
    private LocalDateTime date;
    private Double waterFlow;
    private Double turbidityIndication;
    private Double temperature;
}

