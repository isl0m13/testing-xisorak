package com.example.appgidritexmonitoring.payload.plumb;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlumbMeasurementDTO extends PlumbDTO {

    private LocalDateTime date;

    private Double xValue;

    private Double yValue;

    private Double temp;


}

/*
[{
    gate: {
        id: 9556e56e-2abc-486d-9a3c-4e131ef5b9d8,
        ordinal: 4
        name: 4 section
    }
    indications: [

    ]

  }
]
 */