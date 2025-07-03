package com.example.appgidritexmonitoring.controller;

import com.example.appgidritexmonitoring.payload.ApiResult;
import com.example.appgidritexmonitoring.service.HydrologicalStationService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class HydrologicalStationControllerImpl implements HydrologicalStationController{

    private final HydrologicalStationService hydrologicalStationService;

    @Override
    public ApiResult<?> getLatestMeasurementsOfReservoir(UUID reservoirId) {
        return hydrologicalStationService.getLatestMeasurementsOfReservoir(reservoirId);
    }

    @Override
    public ApiResult<?> getLatestHydrologicalAndWaterFlowMeasurementsOfReservoir(UUID reservoirId) {
        return hydrologicalStationService.getLatestHydrologicalAndWaterFlowMeasurementsOfReservoir(reservoirId);
    }

    @Override
    public ApiResult<?> getMeasurementsOfReservoir(UUID reservoirId, String startDate, String endDate) {
        return hydrologicalStationService.getMeasurementsOfReservoir(reservoirId, startDate, endDate);
    }

    @Override
    public ResponseEntity<Resource> downloadExcelFileOfMeasurementsOfReservoir(UUID reservoirId, String startDate, String endDate) {
        return hydrologicalStationService.downloadExcelFileOfMeasurementsOfReservoir(reservoirId, startDate, endDate);
    }

}
