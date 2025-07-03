package com.example.appgidritexmonitoring.payload.tiltmeter;

import com.example.appgidritexmonitoring.payload.GateDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TiltmeterLatestDataOfGateDTO {

    private GateDTO gate;

    private List<TiltmeterMeasurementDTO> indications;

    public static TiltmeterLatestDataOfGateDTO of(GateDTO gate, List<TiltmeterMeasurementDTO> indications) {
        return new TiltmeterLatestDataOfGateDTO(gate, indications);
    }

}
