package com.example.appgidritexmonitoring.controller;

import com.example.appgidritexmonitoring.entity.DamBodyDevice;
import com.example.appgidritexmonitoring.payload.ApiResult;
import com.example.appgidritexmonitoring.payload.dambodydevice.DamBodyDeviceMeasurementDTO;
import com.example.appgidritexmonitoring.payload.dambodydevice.DamBodyDeviceMeasurementsByGateDTO;
import com.example.appgidritexmonitoring.util.RestConstants;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@RequestMapping(DamBodyDeviceController.DAM_BODY_DEVICE_CONTROLLER_BASE_PATH)
public interface DamBodyDeviceController {
    String DAM_BODY_DEVICE_CONTROLLER_BASE_PATH = RestConstants.BASE_PATH + "/dam-body-device";
    String LATEST_MEASUREMENTS_OF_RESERVOIR = "/reservoir/{reservoirId}/all-types/measurements/latest";
    String LATEST_MEASUREMENTS_OF_GATE = "/gate/{gateId}/all-types/measurements/latest";
    String MEASUREMENTS_BY_DEVICE_OF_GATE = "/gate/{gateId}/{deviceType}/measurements";
    String MEASUREMENTS_BY_DEVICE_OF_RESERVOIR = "/reservoir/{reservoirId}/{deviceType}/measurements";

    @GetMapping(LATEST_MEASUREMENTS_OF_RESERVOIR)
    ApiResult<List<DamBodyDeviceMeasurementsByGateDTO>> getLatestMeasurementsByGateOfReservoir(@PathVariable UUID reservoirId);

    @GetMapping(LATEST_MEASUREMENTS_OF_GATE)
    ApiResult<List<DamBodyDeviceMeasurementDTO>> getLatestMeasurementsOfGate(@PathVariable UUID gateId);

    @GetMapping(MEASUREMENTS_BY_DEVICE_OF_GATE)
    ApiResult<List<DamBodyDeviceMeasurementDTO>> getMeasurementsOfDeviceType(@PathVariable UUID gateId,
                                                                             @PathVariable String deviceType,
                                                                             @RequestParam("startDate") String startDate,
                                                                             @RequestParam("endDate") String endDate);




}
