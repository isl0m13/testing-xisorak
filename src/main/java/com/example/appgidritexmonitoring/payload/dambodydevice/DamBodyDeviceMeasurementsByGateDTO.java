package com.example.appgidritexmonitoring.payload.dambodydevice;

import com.example.appgidritexmonitoring.payload.GateDTO;
import lombok.*;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DamBodyDeviceMeasurementsByGateDTO {
    private GateDTO gate;
    private List<DamBodyDeviceMeasurementDTO> indications;
}
