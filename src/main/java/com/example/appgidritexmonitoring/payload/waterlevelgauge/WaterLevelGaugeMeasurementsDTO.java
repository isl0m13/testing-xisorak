package com.example.appgidritexmonitoring.payload.waterlevelgauge;

import com.example.appgidritexmonitoring.payload.projection.HydrologicalStationMeasurementProj;
import lombok.*;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class WaterLevelGaugeMeasurementsDTO {
   List<WaterLevelGaugeMeasurementDTO> upper;
   HydrologicalStationMeasurementProj lower;

   public static WaterLevelGaugeMeasurementsDTO make(List<WaterLevelGaugeMeasurementDTO> upper,
                                                     HydrologicalStationMeasurementProj lower) {

       return WaterLevelGaugeMeasurementsDTO.builder()
               .lower(lower)
               .upper(upper)
               .build();
   }


}
