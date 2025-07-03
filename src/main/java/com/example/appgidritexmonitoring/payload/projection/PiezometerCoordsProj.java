package com.example.appgidritexmonitoring.payload.projection;

import java.time.LocalDate;
import java.util.UUID;

public interface PiezometerCoordsProj {
    UUID getId();
    Integer getOrdinal();

    String getName();

    String getLat();
    String getLon();

}
