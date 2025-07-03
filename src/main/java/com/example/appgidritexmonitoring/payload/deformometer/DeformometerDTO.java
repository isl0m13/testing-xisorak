package com.example.appgidritexmonitoring.payload.deformometer;

import lombok.*;

import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DeformometerDTO {

    private UUID id;

    private Integer ordinal;

    private String name;

}
