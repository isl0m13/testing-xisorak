package com.example.appgidritexmonitoring.payload.piezometer;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PiezometersDataByDateDTO {
    private LocalDateTime date;

    private List<PiezometerDataDTO> piezometerIndications;

}
