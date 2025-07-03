package com.example.appgidritexmonitoring.controller;

import com.example.appgidritexmonitoring.payload.ApiResult;
import com.example.appgidritexmonitoring.payload.WaterFlowMeterMeasurementDTO;
import com.example.appgidritexmonitoring.service.WaterFlowMeterService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class WaterFlowMeterControllerImpl implements WaterFlowMeterController{

    private final WaterFlowMeterService waterFlowMeterService;
    @Override
    public ApiResult<WaterFlowMeterMeasurementDTO> getLatestMeasurementsOfReservoir(UUID reservoirId) {
        return waterFlowMeterService.getLatestMeasurementsOfReservoir(reservoirId);
    }

    @Override
    public ApiResult<List<WaterFlowMeterMeasurementDTO>> getMeasurementsOfReservoir(UUID reservoirId, String startDate, String endDate) {
        return waterFlowMeterService.getMeasurementsOfReservoir(reservoirId, startDate, endDate);
    }

    @Override
    public ResponseEntity<Resource> downloadExcelOfMeasurementsOfReservoir(UUID reservoirId, String startDate, String endDate) {
        return waterFlowMeterService.downloadExcelOfMeasurementsOfReservoir(reservoirId, startDate, endDate);
    }
}
