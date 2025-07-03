package com.example.appgidritexmonitoring.payload.piezometer;

import com.example.appgidritexmonitoring.payload.GateDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;
import java.util.Set;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PiezometersLatestDataOfGateDTO {


    private GateDTO gate;
    private List<PiezometerMeasurementDTO> piezometerIndications;


}
