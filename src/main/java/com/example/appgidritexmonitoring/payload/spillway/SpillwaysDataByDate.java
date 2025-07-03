package com.example.appgidritexmonitoring.payload.spillway;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SpillwaysDataByDate {
    private LocalDateTime date;
    private List<SpillwayWaterFlowDTO> indications;

}
