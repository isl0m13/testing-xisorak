package com.example.appgidritexmonitoring.payload.piezometer;

import lombok.*;

import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PiezometerMark {

    private UUID piezometerId;

    private String name;

    private Integer ordinal;

    private Double locationPressure;


}
