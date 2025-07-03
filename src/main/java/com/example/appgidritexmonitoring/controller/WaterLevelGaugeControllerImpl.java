package com.example.appgidritexmonitoring.controller;

import com.example.appgidritexmonitoring.payload.ApiResult;
import com.example.appgidritexmonitoring.payload.waterlevelgauge.*;
import com.example.appgidritexmonitoring.service.WaterLevelGaugeService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class WaterLevelGaugeControllerImpl implements WaterLevelGaugeController {
    private final WaterLevelGaugeService waterLevelGaugeService;

    @Override
    public ApiResult<?> updateLocationPressure(Double locationPressure, UUID waterLevelGaugeId) {
        return waterLevelGaugeService.updateLocationPressure(locationPressure, waterLevelGaugeId);
    }

    @Override
    public ApiResult<List<WaterLevelGaugeLatestDataDTO>> getLatestMeasurementOfReservoir(UUID reservoirId) {
        return waterLevelGaugeService.getLatestMeasurementOfReservoir(reservoirId);
    }


    @Override
    public ApiResult<List<WaterLevelGaugesDataByDateDTO>> getMeasurementsByDatesOfReservoir(UUID reservoirId, String startDate, String endDate) {
        return waterLevelGaugeService.getMeasurementsByDatesOfReservoir(reservoirId, startDate, endDate);
    }

    @Override
    public ResponseEntity<Resource> downloadExcelOfMeasurementsOfReservoir(UUID reservoirId, String startDate, String endDate) {
        return waterLevelGaugeService.downloadExcelOfMeasurementsOfReservoir(reservoirId, startDate, endDate);
    }

    @Override
    public ApiResult<?> getDifferenceMeasurementsBetweenDays(UUID reservoirId, String startDate, String endDate) {
        return waterLevelGaugeService.getDifferenceMeasurementsBetweenDays(reservoirId, startDate, endDate);
    }

    @Override
    public ApiResult<List<WaterLevelGaugeDTO>> getLocationPressuresOfReservoir(UUID reservoirId) {
        return waterLevelGaugeService.getLocationPressuresOfReservoir(reservoirId);
    }

    @Override
    public ApiResult<WaterLevelGaugeMeasurementsDTO> getLatestMeasurementsBySecretKey(String secretKey) {
        return waterLevelGaugeService.getLatestMeasurementsBySecretKey(secretKey);
    }

    @Override
    public ApiResult<List<WaterLevelGaugeMeasurementDTO>> getMeasurementsOfLastWlgByReservoir(UUID reservoirId, String startDate, String endDate) {
        return waterLevelGaugeService.getMeasurementsOfLastWlgByReservoir(reservoirId, startDate, endDate);
    }


}
