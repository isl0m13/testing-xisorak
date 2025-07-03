package com.example.appgidritexmonitoring.payload;

import com.example.appgidritexmonitoring.entity.enums.DeviceDatasourceEnum;
import com.example.appgidritexmonitoring.entity.enums.DeviceTypeEnum;
import lombok.*;

import java.io.File;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class DeviceReadRoute {
    private DeviceTypeEnum deviceTypeEnum;
    private DeviceDatasourceEnum deviceDatasourceEnum;
    private Set<File> files;
}

