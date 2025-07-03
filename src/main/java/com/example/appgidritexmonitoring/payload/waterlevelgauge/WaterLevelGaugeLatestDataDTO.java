package com.example.appgidritexmonitoring.payload.waterlevelgauge;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WaterLevelGaugeLatestDataDTO {

    private WaterLevelGaugeDTO waterLevelGauge;

    private LocalDateTime pressureDate;
    private Double hydroPressure;
    private Double volumeValue;
    private Double virtualPressure;
    private Double temperature;
    private Double pressureDiff;

    private LocalDateTime volumeDate;
    private Double volumeInSecond;

}
