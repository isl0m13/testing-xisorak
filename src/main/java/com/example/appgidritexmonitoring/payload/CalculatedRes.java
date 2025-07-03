package com.example.appgidritexmonitoring.payload;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CalculatedRes {

    private Double virtualRes;

    private Double finalRes;

    public static CalculatedRes make(double virtualRes, double finalRes){
        return new CalculatedRes(virtualRes, finalRes);
    }

}
