package com.example.appgidritexmonitoring.service;

import com.example.appgidritexmonitoring.payload.ApiResult;
import com.example.appgidritexmonitoring.payload.dambodydevice.DamBodyDeviceMeasurementDTO;
import com.example.appgidritexmonitoring.payload.dambodydevice.DamBodyDeviceMeasurementsByGateDTO;

import java.util.List;
import java.util.UUID;

public interface DamBodyDeviceService {
    ApiResult<List<DamBodyDeviceMeasurementsByGateDTO>> getLatestMeasurementsByGateOfReservoir(UUID reservoirId);

    ApiResult<List<DamBodyDeviceMeasurementDTO>> getLatestMeasurementsOfGate(UUID gateId);

    ApiResult<List<DamBodyDeviceMeasurementDTO>> getMeasurementsOfDeviceType(UUID gateId,
                                                                             String deviceType,
                                                                             String startDate,
                                                                             String endDate);
}
