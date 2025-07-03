package com.example.appgidritexmonitoring.controller;

import com.example.appgidritexmonitoring.payload.ApiResult;
import com.example.appgidritexmonitoring.payload.plumb.PlumbLatestDataOfGateDTO;
import com.example.appgidritexmonitoring.payload.plumb.PlumbMeasurementDTO;
import com.example.appgidritexmonitoring.service.PlumbService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class PlumbControllerImpl implements PlumbController {

    private final PlumbService plumbService;

    @Override
    public ApiResult<List<PlumbLatestDataOfGateDTO>> getLatestMeasurementsByGateOfReservoir(UUID reservoirId) {
        return plumbService.getLatestMeasurementsByGateOfReservoir(reservoirId);
    }

    @Override
    public ApiResult<List<PlumbMeasurementDTO>> getMeasurementsOfPlumb(UUID plumbId, String startDate, String endDate) {
        return plumbService.getMeasurementsOfPlumb(plumbId, startDate, endDate);
    }
}
