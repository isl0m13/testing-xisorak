package com.example.appgidritexmonitoring.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HydrologicalStationMeasurementDTO {
    private HydrologicalStationDTO hydrologicalStation;
    private LocalDateTime date;
    private Double virtualPressure;
    private Double waterFlow;
    private Double temperature;
    private Coordinates coordinates;
    private List<HydrologicalStationMeasurementDTO> indications;
}
