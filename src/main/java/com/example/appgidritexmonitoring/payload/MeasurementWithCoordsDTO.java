package com.example.appgidritexmonitoring.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MeasurementWithCoordsDTO {
    private String name;
    private Double waterInd;
    private Double waterVirtualInd;
    private Double temperature;
    private LocalDateTime date;
    private Coordinates coords;


}
