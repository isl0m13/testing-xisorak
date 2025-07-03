package com.example.appgidritexmonitoring.payload.spillway;

import lombok.*;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SpillwayDataDTO {
    private SpillwayDTO spillway;
    private List<SpillwayMeasurementDTO> indications;

}
