package com.example.appgidritexmonitoring.payload.projection;

import java.time.LocalDateTime;

public interface HydrologicalStationMeasurementProj {

    LocalDateTime getDate();

    Double getHydro_pressure();

    Double getVirtual_pressure();

}
