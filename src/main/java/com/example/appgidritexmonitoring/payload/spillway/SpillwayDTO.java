package com.example.appgidritexmonitoring.payload.spillway;

import lombok.*;

import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SpillwayDTO {
    private UUID id;
    private Integer ordinal;
    private String name;
}
