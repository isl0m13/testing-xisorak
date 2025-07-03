package com.example.appgidritexmonitoring.payload.waterlevelgauge;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class WaterLevelGaugeMeasurementDiffDTO {

    private LocalDate date;

    private double pressure;

}
