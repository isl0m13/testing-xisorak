package com.example.appgidritexmonitoring.payload.dambodydevice;

import com.example.appgidritexmonitoring.entity.enums.DamBodyDeviceTypeEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DamBodyDeviceMeasurementDTO {

    private Double indication;
    private Double readValue;
    private LocalDateTime date;
    private DamBodyDeviceTypeEnum deviceType;
    private String deviceName;

}
