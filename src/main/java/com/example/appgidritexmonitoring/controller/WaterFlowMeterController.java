package com.example.appgidritexmonitoring.controller;

import com.example.appgidritexmonitoring.payload.ApiResult;
import com.example.appgidritexmonitoring.payload.WaterFlowMeterMeasurementDTO;
import com.example.appgidritexmonitoring.util.RestConstants;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@RequestMapping(WaterFlowMeterController.WATER_FLOW_CONTROLLER_BASE_URL)
public interface WaterFlowMeterController {
    String WATER_FLOW_CONTROLLER_BASE_URL = RestConstants.BASE_PATH + "/water-flow-meters";
    String LATEST_MEASUREMENTS_OF_RESERVOIR = "/reservoirs/{reservoirId}/measurements/latest";
    String MEASUREMENTS_OF_RESERVOIR_BY_DATES = "/reservoirs/{reservoirId}/measurements";
    String DOWNLOAD_EXCEL_FILE_MEASUREMENTS_OF_RESERVOIR = "/download/excel-file/reservoirs/{reservoirId}/measurements";


    @GetMapping(LATEST_MEASUREMENTS_OF_RESERVOIR)
    ApiResult<WaterFlowMeterMeasurementDTO> getLatestMeasurementsOfReservoir(@PathVariable UUID reservoirId);


    @GetMapping(MEASUREMENTS_OF_RESERVOIR_BY_DATES)
    ApiResult<List<WaterFlowMeterMeasurementDTO>> getMeasurementsOfReservoir(@PathVariable UUID reservoirId,
                                                                             @RequestParam("startDate") String startDate,
                                                                             @RequestParam("endDate") String endDate);

    @GetMapping(DOWNLOAD_EXCEL_FILE_MEASUREMENTS_OF_RESERVOIR)
    ResponseEntity<Resource> downloadExcelOfMeasurementsOfReservoir(@PathVariable UUID reservoirId,
                                                                    @RequestParam("startDate") String startDate,
                                                                    @RequestParam("endDate") String endDate);


}
