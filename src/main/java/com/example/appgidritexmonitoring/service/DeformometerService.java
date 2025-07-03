package com.example.appgidritexmonitoring.service;

import com.example.appgidritexmonitoring.payload.ApiResult;
import com.example.appgidritexmonitoring.payload.deformometer.DeformometerMeasurementDTO;

import java.util.List;
import java.util.UUID;

public interface DeformometerService {

    ApiResult<List<DeformometerMeasurementDTO>> getLatestMeasurementsOfReservoir(UUID reservoirId);

    ApiResult<List<?>> getMeasurementsOfReservoir(UUID reservoirId, String startDate, String endDate);

    ApiResult<List<DeformometerMeasurementDTO>> getMeasurementsOfDeformometer(UUID deformometerId, String startDate, String endDate);

}
