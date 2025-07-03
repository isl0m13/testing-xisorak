package com.example.appgidritexmonitoring.controller;

import com.example.appgidritexmonitoring.payload.ApiResult;
import com.example.appgidritexmonitoring.payload.deformometer.DeformometerMeasurementDTO;
import com.example.appgidritexmonitoring.service.DeformometerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class DeformometerControllerImpl implements DeformometerController{

    private final DeformometerService deformometerService;

    @Override
    public ApiResult<List<DeformometerMeasurementDTO>> getLatestMeasurementsOfReservoir(UUID reservoirId) {
        return deformometerService.getLatestMeasurementsOfReservoir(reservoirId);
    }

    @Override
    public ApiResult<List<?>> getMeasurementsOfReservoir(UUID reservoirId, String startDate, String endDate) {
        return deformometerService.getMeasurementsOfReservoir(reservoirId, startDate, endDate);
    }

    @Override
    public ApiResult<List<DeformometerMeasurementDTO>> getMeasurementsOfDeformometer(UUID deformometerId, String startDate, String endDate) {
        return deformometerService.getMeasurementsOfDeformometer(deformometerId, startDate, endDate);
    }


}
