package com.example.appgidritexmonitoring.payload.waterlevelgauge;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import javax.persistence.criteria.CriteriaBuilder;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WaterLevelGaugeMeasurementDTO {
    private WaterLevelGaugeDTO waterLevelGauge;

    private LocalDateTime date;

    private Double hydroPressure;
    private Double volumeValue;
    private Double virtualPressure;
    private Double temperature;
    private Double pressureDiff;

}
