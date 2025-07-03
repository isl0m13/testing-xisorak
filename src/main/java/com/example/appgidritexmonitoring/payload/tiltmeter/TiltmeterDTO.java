package com.example.appgidritexmonitoring.payload.tiltmeter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TiltmeterDTO {
    private UUID id;
    private String name;
    private Integer ordinal;

}
