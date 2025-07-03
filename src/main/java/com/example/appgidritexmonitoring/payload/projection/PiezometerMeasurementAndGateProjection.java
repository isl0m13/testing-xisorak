package com.example.appgidritexmonitoring.payload.projection;

import java.time.LocalDateTime;
import java.util.UUID;

public interface PiezometerMeasurementAndGateProjection {

    UUID getPiezometerId();

    Integer getPiezometerOrdinal();

    double getHydroPressure();

    double getTemperature();

    LocalDateTime getDate();

    UUID getGateId();

    Integer getGateOrdinal();

    String getGateName();
}
