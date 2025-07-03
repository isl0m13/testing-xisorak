package com.example.appgidritexmonitoring.controller;

import com.example.appgidritexmonitoring.payload.ApiResult;
import com.example.appgidritexmonitoring.util.RestConstants;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@RequestMapping(HydrologicalStationController.HYDROLOGICAL_STATION_CONTROLLER_BASE_URL)
public interface HydrologicalStationController {
    String HYDROLOGICAL_STATION_CONTROLLER_BASE_URL = RestConstants.BASE_PATH + "/hydrological-stations";
    String LATEST_MEASUREMENTS_OF_RESERVOIR = "/reservoirs/{reservoirId}/measurements/latest";
    String LATEST_HYDROLOGICAL_AND_WATER_FLOW_MEASUREMENTS_OF_RESERVOIR = "/water-flow-meter/reservoirs/{reservoirId}/measurements/latest";
    String MEASUREMENTS_OF_RESERVOIR_BY_DATES = "/reservoirs/{reservoirId}/measurements";
    String DOWNLOAD_EXCEL_FILE_OF_MEASUREMENTS_OF_RESERVOIR = "/download/excel-file/reservoirs/{reservoirId}/measurements";

    @GetMapping(LATEST_MEASUREMENTS_OF_RESERVOIR)
    ApiResult<?> getLatestMeasurementsOfReservoir(@PathVariable UUID reservoirId);

    @GetMapping(LATEST_HYDROLOGICAL_AND_WATER_FLOW_MEASUREMENTS_OF_RESERVOIR)
    ApiResult<?> getLatestHydrologicalAndWaterFlowMeasurementsOfReservoir(@PathVariable UUID reservoirId);

    @GetMapping(MEASUREMENTS_OF_RESERVOIR_BY_DATES)
    ApiResult<?> getMeasurementsOfReservoir(@PathVariable UUID reservoirId,
                                            @RequestParam("startDate") String startDate,
                                            @RequestParam("endDate") String endDate);

    @GetMapping(DOWNLOAD_EXCEL_FILE_OF_MEASUREMENTS_OF_RESERVOIR)
    ResponseEntity<Resource> downloadExcelFileOfMeasurementsOfReservoir(@PathVariable UUID reservoirId,
                                                                        @RequestParam("startDate") String startDate,
                                                                        @RequestParam("endDate") String endDate);


}
