package com.example.appgidritexmonitoring.controller;

import com.example.appgidritexmonitoring.payload.ApiResult;
import com.example.appgidritexmonitoring.payload.dambodydevice.DamBodyDeviceMeasurementDTO;
import com.example.appgidritexmonitoring.payload.dambodydevice.DamBodyDeviceMeasurementsByGateDTO;
import com.example.appgidritexmonitoring.service.DamBodyDeviceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class DamBodyDeviceControllerImpl implements DamBodyDeviceController{

    private final DamBodyDeviceService damBodyDeviceService;
    @Override
    public ApiResult<List<DamBodyDeviceMeasurementsByGateDTO>> getLatestMeasurementsByGateOfReservoir(UUID reservoirId) {
        return damBodyDeviceService.getLatestMeasurementsByGateOfReservoir(reservoirId);
    }

    @Override
    public ApiResult<List<DamBodyDeviceMeasurementDTO>> getLatestMeasurementsOfGate(UUID gateId) {
        return damBodyDeviceService.getLatestMeasurementsOfGate(gateId);
    }

    @Override
    public ApiResult<List<DamBodyDeviceMeasurementDTO>> getMeasurementsOfDeviceType(UUID gateId,
                                                                                    String deviceType,
                                                                                    String startDate,
                                                                                    String endDate) {
        return damBodyDeviceService.getMeasurementsOfDeviceType(gateId, deviceType, startDate, endDate);
    }
}
