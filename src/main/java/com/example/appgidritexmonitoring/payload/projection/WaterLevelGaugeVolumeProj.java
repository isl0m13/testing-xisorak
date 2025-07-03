package com.example.appgidritexmonitoring.payload.projection;

import java.time.LocalDateTime;

public interface WaterLevelGaugeVolumeProj {

    String getWater_level_gauge_id();

    LocalDateTime getDate();

    Double getVolume_in_second();


}
