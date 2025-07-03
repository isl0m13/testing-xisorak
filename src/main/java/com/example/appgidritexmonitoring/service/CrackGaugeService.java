package com.example.appgidritexmonitoring.service;

import com.example.appgidritexmonitoring.payload.ApiResult;
import com.example.appgidritexmonitoring.payload.crackgauge.CrackGaugeLatestDataOfGateDTO;
import com.example.appgidritexmonitoring.payload.crackgauge.CrackGaugeMeasurementDTO;

import java.util.List;
import java.util.UUID;

public interface CrackGaugeService {

    ApiResult<List<CrackGaugeLatestDataOfGateDTO>> getLatestMeasurementsBySectionsOfLocation(Integer location);

    ApiResult<List<CrackGaugeMeasurementDTO>> getMeasurementsOfCrackGaugeByDates(String crackGaugeId,
                                                                                 String startDate,
                                                                                 String endDate);
}
