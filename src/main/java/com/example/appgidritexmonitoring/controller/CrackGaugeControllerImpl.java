package com.example.appgidritexmonitoring.controller;

import com.example.appgidritexmonitoring.payload.ApiResult;
import com.example.appgidritexmonitoring.payload.crackgauge.CrackGaugeLatestDataOfGateDTO;
import com.example.appgidritexmonitoring.payload.crackgauge.CrackGaugeMeasurementDTO;
import com.example.appgidritexmonitoring.service.CrackGaugeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CrackGaugeControllerImpl implements CrackGaugeController{

    private final CrackGaugeService crackGaugeService;

    @Override
    public ApiResult<List<CrackGaugeLatestDataOfGateDTO>> getLatestMeasurementsBySectionsOfLocation(Integer location) {
        return crackGaugeService.getLatestMeasurementsBySectionsOfLocation(location);
    }

    @Override
    public ApiResult<List<CrackGaugeMeasurementDTO>> getMeasurementsOfCrackGaugeByDates(String crackGaugeId,
                                                                                        String startDate,
                                                                                        String endDate) {
        return crackGaugeService.getMeasurementsOfCrackGaugeByDates(crackGaugeId, startDate, endDate);
    }

}
