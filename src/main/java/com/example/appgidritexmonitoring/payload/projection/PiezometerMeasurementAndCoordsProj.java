package com.example.appgidritexmonitoring.payload.projection;

import com.example.appgidritexmonitoring.entity.Piezometer;

import java.time.LocalDateTime;
import java.util.UUID;

public interface PiezometerMeasurementAndCoordsProj {

    String getPiezometer_id();

    Integer getOrdinal();

    String getName();

    LocalDateTime getDate();

    Double getComputed_pressure();

    Double getVirtual_pressure();

    Double getTemp_in_degrees();

    String getLat();
    String getLon();


}
