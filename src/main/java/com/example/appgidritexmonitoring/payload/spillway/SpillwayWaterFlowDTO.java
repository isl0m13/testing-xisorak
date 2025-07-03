package com.example.appgidritexmonitoring.payload.spillway;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SpillwayWaterFlowDTO {
    private SpillwayDTO spillway;
    private Double waterFlow;
    private Double turbidityIndication;
}
