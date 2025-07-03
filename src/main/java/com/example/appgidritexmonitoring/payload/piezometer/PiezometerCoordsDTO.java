package com.example.appgidritexmonitoring.payload.piezometer;

import com.example.appgidritexmonitoring.payload.Coordinates;
import lombok.*;

import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PiezometerCoordsDTO {

    private UUID id;

    private Integer ordinal;

    private String name;

    private Coordinates coordinates;

}
