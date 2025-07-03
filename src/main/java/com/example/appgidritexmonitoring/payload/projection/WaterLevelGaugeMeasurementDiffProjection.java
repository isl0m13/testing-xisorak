package com.example.appgidritexmonitoring.payload.projection;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public interface WaterLevelGaugeMeasurementDiffProjection {
    String getWater_level_gauge_id();
    Integer getOrdinal();
    Double getLocation_pressure();
    LocalDateTime getDate();
    Double getVirtual_pressure();
    Double getPressure_diff();
}
