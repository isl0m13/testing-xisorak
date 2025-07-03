package com.example.appgidritexmonitoring.controller;

import com.example.appgidritexmonitoring.payload.ApiResult;
import com.example.appgidritexmonitoring.payload.crackgauge.CrackGaugeLatestDataOfGateDTO;
import com.example.appgidritexmonitoring.payload.crackgauge.CrackGaugeMeasurementDTO;
import com.example.appgidritexmonitoring.util.RestConstants;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@RequestMapping(CrackGaugeController.CRACK_GAUGE_CONTROLLER_BASE_PATH)
public interface CrackGaugeController {
    String CRACK_GAUGE_CONTROLLER_BASE_PATH = RestConstants.BASE_PATH + "/crack-gauge";
    String LATEST_MEASUREMENTS_BY_SECTION_OF_LOCATION = "/by-section/location/{location}/measurements/latest";
    String MEASUREMENTS_OF_CRACK_GAUGE_BY_DATES = "/{crackGaugeId}/measurements";

    @GetMapping(LATEST_MEASUREMENTS_BY_SECTION_OF_LOCATION)
    ApiResult<List<CrackGaugeLatestDataOfGateDTO>> getLatestMeasurementsBySectionsOfLocation(@PathVariable Integer location);

    @GetMapping(MEASUREMENTS_OF_CRACK_GAUGE_BY_DATES)
    ApiResult<List<CrackGaugeMeasurementDTO>> getMeasurementsOfCrackGaugeByDates(@PathVariable String crackGaugeId,
                                                                                 @RequestParam(name = "startDate") String startDate,
                                                                                 @RequestParam(name = "endDate") String endDate);


}
