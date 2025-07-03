package com.example.appgidritexmonitoring.payload.waterlevelgauge;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class WaterLevelGaugesDataByDateDTO {
    private LocalDateTime date;
    private List<WaterLevelGaugeMeasurementDTO> indications;
}
