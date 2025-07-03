package com.example.appgidritexmonitoring.payload.spillway;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SpillwayMeasurementTotalDTO {
    private Double totalWaterFlow;
    private LocalDateTime date;

}


