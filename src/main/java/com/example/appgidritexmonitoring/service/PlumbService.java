package com.example.appgidritexmonitoring.service;

import com.example.appgidritexmonitoring.payload.ApiResult;
import com.example.appgidritexmonitoring.payload.plumb.PlumbLatestDataOfGateDTO;
import com.example.appgidritexmonitoring.payload.plumb.PlumbMeasurementDTO;

import java.util.List;
import java.util.UUID;

public interface PlumbService {

    ApiResult<List<PlumbLatestDataOfGateDTO>> getLatestMeasurementsByGateOfReservoir(UUID reservoirId);

    ApiResult<List<PlumbMeasurementDTO>> getMeasurementsOfPlumb(UUID plumbId, String startDate, String endDate);

}
