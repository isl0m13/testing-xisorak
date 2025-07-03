package com.example.appgidritexmonitoring.controller;

import com.example.appgidritexmonitoring.payload.ApiResult;
import com.example.appgidritexmonitoring.payload.piezometer.PiezometerMark;
import com.example.appgidritexmonitoring.payload.piezometer.PiezometerMeasurementDTO;
import com.example.appgidritexmonitoring.payload.piezometer.PiezometersDataByDateDTO;
import com.example.appgidritexmonitoring.payload.piezometer.PiezometersLatestDataOfGateDTO;
import com.example.appgidritexmonitoring.service.PiezometerService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class PiezometerControllerImpl implements PiezometerController{

    private final PiezometerService piezometerService;

    @Override
    public ApiResult<?> updateLocationPressure(Double locationPressure, UUID piezometerId) {
        return piezometerService.updateLocationPressure(locationPressure, piezometerId);
    }

    @Override
    public ApiResult<List<PiezometerMark>> getLocationPressuresOfGate(UUID gateId) {
        return piezometerService.getLocationPressuresOfGate(gateId);
    }

    @Override
    public ApiResult<List<PiezometersLatestDataOfGateDTO>> getLatestMeasurementsOfReservoirByGate(UUID reservoirId) {
        return piezometerService.getLatestMeasurementsOfReservoirByGate(reservoirId);
    }

    @Override
    public ApiResult<List<PiezometerMeasurementDTO>> getLatestMeasurementsOfGate(UUID gateId) {
        return piezometerService.getLatestMeasurementsOfGate(gateId);
    }

    @Override
    public ApiResult<List<PiezometersDataByDateDTO>> getMeasurementsOfGate(UUID gateId, String startDate, String endDate) {
        return piezometerService.getMeasurementsOfGate(gateId, startDate, endDate);
    }

    @Override
    public ResponseEntity<Resource> downloadExcelOfMeasurementsOfGate(UUID gateId, String startDate, String endDate) {
        return piezometerService.downloadExcelOfMeasurementsOfGate(gateId, startDate, endDate);
    }

    @Override
    public ApiResult<?> getCoordinatesOfPiezometers(UUID reservoirId) {
        return piezometerService.getCoordinatesOfPiezometers(reservoirId);
    }

    @Override
    public ApiResult<List<PiezometerMeasurementDTO>> getMeasurementsOfPiezometer(UUID piezometerId, String startDate, String endDate) {
        return piezometerService.getMeasurementsOfPiezometer(piezometerId, startDate, endDate);
    }

    @Override
    public ApiResult<List<PiezometersLatestDataOfGateDTO>> getLatestMeasurementsByLocationOfPressurePiezometers(UUID reservoirId, Integer location) {
        return piezometerService.getLatestMeasurementsByLocationOfPressurePiezometers(reservoirId, location);
    }

    @Override
    public ApiResult<List<PiezometerMeasurementDTO>> getLatestMeasurementsByLocationOfNoPressurePiezometers(UUID reservoirId, Integer location) {
        return piezometerService.getLatestMeasurementsByLocationOfNoPressurePiezometers(reservoirId, location);
    }
}
