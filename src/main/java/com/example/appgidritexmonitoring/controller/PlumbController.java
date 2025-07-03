package com.example.appgidritexmonitoring.controller;

import com.example.appgidritexmonitoring.payload.ApiResult;
import com.example.appgidritexmonitoring.payload.plumb.PlumbLatestDataOfGateDTO;
import com.example.appgidritexmonitoring.payload.plumb.PlumbMeasurementDTO;
import com.example.appgidritexmonitoring.util.RestConstants;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@RequestMapping(PlumbController.PLUMB_CONTROLLER_BASE_PATH)
public interface PlumbController {
    String PLUMB_CONTROLLER_BASE_PATH = RestConstants.BASE_PATH + "/plumb";
    String LATEST_MEASUREMENTS_BY_SECTIONS_OF_RESERVOIR = "/reservoir/{reservoirId}/by-section/measurements/latest";
    String MEASUREMENTS_OF_PLUMB = "/{plumbId}/measurements";

    @GetMapping(LATEST_MEASUREMENTS_BY_SECTIONS_OF_RESERVOIR)
    ApiResult<List<PlumbLatestDataOfGateDTO>> getLatestMeasurementsByGateOfReservoir(@PathVariable UUID reservoirId);

    @GetMapping(MEASUREMENTS_OF_PLUMB)
    ApiResult<List<PlumbMeasurementDTO>> getMeasurementsOfPlumb(@PathVariable UUID plumbId,
                                                                @RequestParam(name = "startDate") String startDate,
                                                                @RequestParam(name = "endDate") String endDate);




}
