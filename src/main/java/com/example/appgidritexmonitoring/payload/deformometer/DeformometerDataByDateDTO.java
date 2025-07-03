package com.example.appgidritexmonitoring.payload.deformometer;

import com.example.appgidritexmonitoring.payload.piezometer.PiezometerDataDTO;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DeformometerDataByDateDTO {

    private LocalDateTime date;

    private List<DeformometerMeasurementDTO> indications;

}
