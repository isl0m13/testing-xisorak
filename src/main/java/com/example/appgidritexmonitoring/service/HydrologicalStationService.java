package com.example.appgidritexmonitoring.service;

import com.example.appgidritexmonitoring.payload.ApiResult;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface HydrologicalStationService {
    ApiResult<?> getLatestMeasurementsOfReservoir(UUID reservoirId);

    ApiResult<?> getMeasurementsOfReservoir(UUID reservoirId, String startDate, String endDate);

    ResponseEntity<Resource> downloadExcelFileOfMeasurementsOfReservoir(UUID reservoirId, String startDate, String endDate);

    ApiResult<?> getLatestHydrologicalAndWaterFlowMeasurementsOfReservoir(UUID reservoirId);
}
