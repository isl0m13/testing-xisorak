package com.example.appgidritexmonitoring.controller;

import com.example.appgidritexmonitoring.payload.ApiResult;
import com.example.appgidritexmonitoring.payload.tiltmeter.TiltmeterLatestDataOfGateDTO;
import com.example.appgidritexmonitoring.payload.tiltmeter.TiltmeterMeasurementDTO;
import com.example.appgidritexmonitoring.service.TiltmeterService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class TiltmeterControllerImpl implements TiltmeterController {

    private final TiltmeterService tiltmeterService;

    @Override
    public ApiResult<List<TiltmeterLatestDataOfGateDTO>> getLatestMeasurementsOfReservoir(UUID reservoirId) {
        return tiltmeterService.getLatestMeasurementsOfReservoir(reservoirId);
    }

    @Override
    public ApiResult<List<TiltmeterMeasurementDTO>> getMeasurementsOfTiltmeter(UUID tiltmeterId,
                                                                               String startDate,
                                                                               String endDate) {
        return tiltmeterService.getMeasurementsOfTiltmeter(tiltmeterId, startDate, endDate);
    }
}
