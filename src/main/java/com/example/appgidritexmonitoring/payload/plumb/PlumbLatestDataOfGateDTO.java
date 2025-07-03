package com.example.appgidritexmonitoring.payload.plumb;

import com.example.appgidritexmonitoring.entity.Gate;
import com.example.appgidritexmonitoring.payload.GateDTO;
import lombok.*;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PlumbLatestDataOfGateDTO {

    private GateDTO gate;

    private List<PlumbMeasurementDTO> indications;

}
