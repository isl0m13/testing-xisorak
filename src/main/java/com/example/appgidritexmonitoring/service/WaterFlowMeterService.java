package com.example.appgidritexmonitoring.service;

import com.example.appgidritexmonitoring.payload.ApiResult;
import com.example.appgidritexmonitoring.payload.WaterFlowMeterMeasurementDTO;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

public interface WaterFlowMeterService {
    ApiResult<WaterFlowMeterMeasurementDTO> getLatestMeasurementsOfReservoir(UUID reservoirId);

    ApiResult<List<WaterFlowMeterMeasurementDTO>> getMeasurementsOfReservoir(UUID reservoirId, String startDate, String endDate);

    ResponseEntity<Resource> downloadExcelOfMeasurementsOfReservoir(UUID reservoirId, String startDate, String endDate);
}


