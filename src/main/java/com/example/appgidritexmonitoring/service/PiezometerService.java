package com.example.appgidritexmonitoring.service;

import com.example.appgidritexmonitoring.payload.ApiResult;
import com.example.appgidritexmonitoring.payload.piezometer.PiezometerMark;
import com.example.appgidritexmonitoring.payload.piezometer.PiezometerMeasurementDTO;
import com.example.appgidritexmonitoring.payload.piezometer.PiezometersDataByDateDTO;
import com.example.appgidritexmonitoring.payload.piezometer.PiezometersLatestDataOfGateDTO;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface PiezometerService {

    ApiResult<List<PiezometersLatestDataOfGateDTO>> getLatestMeasurementsOfReservoirByGate(UUID reservoirId);

    ApiResult<List<PiezometerMeasurementDTO>> getLatestMeasurementsOfGate(UUID gateId);

    ApiResult<List<PiezometersDataByDateDTO>> getMeasurementsOfGate(UUID gateId, String startDate, String endDate);

    ResponseEntity<Resource> downloadExcelOfMeasurementsOfGate(UUID gateId, String startDate, String endDate);

    ApiResult<?> getCoordinatesOfPiezometers(UUID reservoirId);

    ApiResult<List<PiezometerMark>> getLocationPressuresOfGate(UUID gateId);

    ApiResult<?> updateLocationPressure(Double locationPressure, UUID piezometerId);

    ApiResult<List<PiezometerMeasurementDTO>> getMeasurementsOfPiezometer(UUID piezometerId, String startDate, String endDate);

    ApiResult<List<PiezometersLatestDataOfGateDTO>> getLatestMeasurementsByLocationOfPressurePiezometers(UUID reservoirId, Integer location);

    ApiResult<List<PiezometerMeasurementDTO>> getLatestMeasurementsByLocationOfNoPressurePiezometers(UUID reservoirId, Integer location);
}
