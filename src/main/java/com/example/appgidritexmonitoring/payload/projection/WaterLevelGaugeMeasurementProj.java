package com.example.appgidritexmonitoring.payload.projection;

import java.time.LocalDateTime;

public interface WaterLevelGaugeMeasurementProj {

    String getWater_level_gauge_id();

    Integer getOrdinal();

    Double getLocation_pressure();

    LocalDateTime getDate();

    Double getComputed_pressure();

    Double getVirtual_pressure();

    Double getVolume_value();

    Double getPressure_diff();


}
