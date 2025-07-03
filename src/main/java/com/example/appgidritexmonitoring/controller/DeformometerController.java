package com.example.appgidritexmonitoring.controller;

import com.example.appgidritexmonitoring.payload.ApiResult;
import com.example.appgidritexmonitoring.payload.deformometer.DeformometerMeasurementDTO;
import com.example.appgidritexmonitoring.util.RestConstants;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@RequestMapping(DeformometerController.DEFORMOMETER_CONTROLLER_BASE_PATH)
public interface DeformometerController {
    String DEFORMOMETER_CONTROLLER_BASE_PATH = RestConstants.BASE_PATH + "/deformometer";
    String GET_LATEST_MEASUREMENTS = "/reservoirs/{reservoirId}/measurements/latest";
    String MEASUREMENTS_OF_RESERVOIR = "/reservoirs/{reservoirId}/measurements";
    String MEASUREMENTS_OF_DEFORMOMETER = "/{deformometerId}/measurements";

    @GetMapping(GET_LATEST_MEASUREMENTS)
    ApiResult<List<DeformometerMeasurementDTO>> getLatestMeasurementsOfReservoir(
            @PathVariable("reservoirId") UUID reservoirId
    );

    @GetMapping(MEASUREMENTS_OF_RESERVOIR)
    ApiResult<List<?>> getMeasurementsOfReservoir(
            @PathVariable("reservoirId") UUID reservoirId,
            @RequestParam(name = "startDate") String startDate,
            @RequestParam(name = "endDate") String endDate
    );

    @GetMapping(MEASUREMENTS_OF_DEFORMOMETER)
    ApiResult<List<DeformometerMeasurementDTO>> getMeasurementsOfDeformometer(
            @PathVariable("deformometerId") UUID deformometerId,
            @RequestParam(name = "startDate") String startDate,
            @RequestParam(name = "endDate") String endDate
    );






}
