package com.example.appgidritexmonitoring.payload.waterlevelgauge;

import lombok.*;

import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class WaterLevelGaugeDTO {

    private UUID id;

    private Integer ordinal;

    private Double locationPressure;
}
