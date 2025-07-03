package com.example.appgidritexmonitoring.payload.spillway;

import com.example.appgidritexmonitoring.payload.GateDTO;
import lombok.*;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SpillwayLatestDataOfGateDTO {
    private GateDTO gate;
    private List<SpillwayLatestDataDTO> indications;

}
