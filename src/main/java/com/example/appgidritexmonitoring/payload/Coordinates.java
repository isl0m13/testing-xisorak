package com.example.appgidritexmonitoring.payload;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Coordinates {

    private String lat;

    private String lon;

    public static Coordinates create(String lat, String lon){
        return new Coordinates(lat, lon);
    }

}
