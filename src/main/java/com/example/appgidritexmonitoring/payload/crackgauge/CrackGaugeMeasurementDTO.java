package com.example.appgidritexmonitoring.payload.crackgauge;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CrackGaugeMeasurementDTO extends CrackGaugeDTO {

    private LocalDateTime date;

    private Double xValue;

    private Double xTemp;

    private Double yValue;

    private Double yTemp;

    private Double zValue;

    private Double zTemp;


}
