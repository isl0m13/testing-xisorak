package com.example.appgidritexmonitoring.payload.crackgauge;


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
public class CrackGaugeLatestDataOfGateDTO {

    private GateDTO gate;

    private List<CrackGaugeMeasurementDTO> indications;

    public static CrackGaugeLatestDataOfGateDTO make(GateDTO gate, List<CrackGaugeMeasurementDTO> indications) {
        return new CrackGaugeLatestDataOfGateDTO(gate, indications);
    }


}
