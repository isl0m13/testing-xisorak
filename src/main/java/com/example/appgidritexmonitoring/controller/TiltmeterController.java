package com.example.appgidritexmonitoring.controller;

import com.example.appgidritexmonitoring.payload.ApiResult;
import com.example.appgidritexmonitoring.payload.tiltmeter.TiltmeterLatestDataOfGateDTO;
import com.example.appgidritexmonitoring.payload.tiltmeter.TiltmeterMeasurementDTO;
import com.example.appgidritexmonitoring.util.RestConstants;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@RequestMapping(TiltmeterController.TILTMETER_CONTROLLER_BASE_PATH)
public interface TiltmeterController {
    String TILTMETER_CONTROLLER_BASE_PATH = RestConstants.BASE_PATH + "/tiltmeters";
    String LATEST_MEASUREMENTS_OF_RESERVOIR = "/reservoir/{reservoirId}/measurements/latest";
    String MEASUREMENTS_OF_TILTMETER = "/{tiltmeterId}/measurements";

    @GetMapping(LATEST_MEASUREMENTS_OF_RESERVOIR)
    ApiResult<List<TiltmeterLatestDataOfGateDTO>> getLatestMeasurementsOfReservoir(@PathVariable("reservoirId") UUID reservoirId);

    @GetMapping(MEASUREMENTS_OF_TILTMETER)
    ApiResult<List<TiltmeterMeasurementDTO>> getMeasurementsOfTiltmeter(@PathVariable("tiltmeterId") UUID tiltmeterId,
                                                                        @RequestParam(name = "startDate") String startDate,
                                                                        @RequestParam(name = "endDate") String endDate);




}
