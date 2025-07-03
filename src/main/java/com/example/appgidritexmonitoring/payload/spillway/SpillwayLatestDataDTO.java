package com.example.appgidritexmonitoring.payload.spillway;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonPropertyOrder({"spillway"})
public class SpillwayLatestDataDTO extends SpillwayMeasurementDTO {
    private SpillwayDTO spillway;

}

