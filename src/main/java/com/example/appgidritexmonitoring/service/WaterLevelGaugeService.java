package com.example.appgidritexmonitoring.service;

import com.example.appgidritexmonitoring.payload.ApiResult;
import com.example.appgidritexmonitoring.payload.waterlevelgauge.*;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

public interface WaterLevelGaugeService {
    ApiResult<List<WaterLevelGaugeLatestDataDTO>> getLatestMeasurementOfReservoir(UUID reservoirId);

    ApiResult<List<WaterLevelGaugesDataByDateDTO>> getMeasurementsByDatesOfReservoir(UUID reservoirId, String startDate, String endDate);

    ResponseEntity<Resource> downloadExcelOfMeasurementsOfReservoir(UUID reservoirId, String startDate, String endDate);

    ApiResult<?> getDifferenceMeasurementsBetweenDays(UUID reservoirId, String startDate, String endDate);

    ApiResult<?> updateLocationPressure(Double locationPressure, UUID waterLevelGaugeId);

    ApiResult<List<WaterLevelGaugeDTO>> getLocationPressuresOfReservoir(UUID reservoirId);

    ApiResult<WaterLevelGaugeMeasurementsDTO> getLatestMeasurementsBySecretKey(String secretKey);

    ApiResult<List<WaterLevelGaugeMeasurementDTO>> getMeasurementsOfLastWlgByReservoir(UUID reservoirId, String startDate, String endDate);
}
