package com.example.appgidritexmonitoring.service;

import com.example.appgidritexmonitoring.payload.ApiResult;
import com.example.appgidritexmonitoring.payload.tiltmeter.TiltmeterLatestDataOfGateDTO;
import com.example.appgidritexmonitoring.payload.tiltmeter.TiltmeterMeasurementDTO;

import java.util.List;
import java.util.UUID;

public interface TiltmeterService {

    ApiResult<List<TiltmeterLatestDataOfGateDTO>> getLatestMeasurementsOfReservoir(UUID reservoirId);

    ApiResult<List<TiltmeterMeasurementDTO>> getMeasurementsOfTiltmeter(UUID tiltmeterId, String startDate, String endDate);

}
