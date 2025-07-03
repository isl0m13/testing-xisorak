package com.example.appgidritexmonitoring.controller;


import com.example.appgidritexmonitoring.entity.WaterLevelGaugeMeasurement;
import com.example.appgidritexmonitoring.payload.ApiResult;
import com.example.appgidritexmonitoring.payload.waterlevelgauge.*;
import com.example.appgidritexmonitoring.util.RestConstants;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequestMapping(WaterLevelGaugeController.WATER_LEVEL_GAUGE_CONTROLLER_BASE_PATH)
public interface WaterLevelGaugeController {
    String WATER_LEVEL_GAUGE_CONTROLLER_BASE_PATH = RestConstants.BASE_PATH + "/water-level-gauges";
    String LOCATION_PRESSURES_OF_RESERVOIR = "/reservoir/{reservoirId}/marks";
    String UPDATE_LOCATION_PRESSURE_OF_WLG = "/{waterLevelGaugeId}/update/mark";
    String GET_MEASUREMENTS_OF_LAST_WLG = "/last/reservoir/{reservoirId}/measurements";
    String LATEST_MEASUREMENT_OF_UPPER = "/reservoirs/{reservoirId}/measurements/latest";
    String MEASUREMENTS_OF_UPPER = "/reservoirs/{reservoirId}/measurements";
    String DOWNLOAD_EXCEL_FILE_OF_RESERVOIR_UPPER = "/download/excel-file/reservoirs/{reservoirId}/measurements";
    String DIFFERENCE_MEASUREMENTS_BETWEEN_DAYS_OF_UPPER = "/reservoirs/{reservoirId}/difference-measurements";
    String LATEST_MEASUREMENTS_BY_SECRET_KEY = "/reservoir/measurements/latest";

    @PutMapping(UPDATE_LOCATION_PRESSURE_OF_WLG)
    ApiResult<?> updateLocationPressure(@RequestParam("mark") Double locationPressure, @PathVariable UUID waterLevelGaugeId);

    @GetMapping(LATEST_MEASUREMENT_OF_UPPER)
    ApiResult<List<WaterLevelGaugeLatestDataDTO>> getLatestMeasurementOfReservoir(@PathVariable UUID reservoirId);

    @GetMapping(MEASUREMENTS_OF_UPPER)
    ApiResult<List<WaterLevelGaugesDataByDateDTO>> getMeasurementsByDatesOfReservoir(@PathVariable UUID reservoirId,
                                                                                     @RequestParam("startDate") String startDate,
                                                                                     @RequestParam("endDate") String endDate);

    @GetMapping(DOWNLOAD_EXCEL_FILE_OF_RESERVOIR_UPPER)
    ResponseEntity<Resource> downloadExcelOfMeasurementsOfReservoir(@PathVariable UUID reservoirId,
                                                                    @RequestParam("startDate") String startDate,
                                                                    @RequestParam("endDate") String endDate);

    @GetMapping(DIFFERENCE_MEASUREMENTS_BETWEEN_DAYS_OF_UPPER)
    ApiResult<?> getDifferenceMeasurementsBetweenDays(@PathVariable UUID reservoirId,
                                                      @RequestParam("startDate") String startDate,
                                                      @RequestParam("endDate") String endDate);

    @GetMapping(LOCATION_PRESSURES_OF_RESERVOIR)
    ApiResult<List<WaterLevelGaugeDTO>> getLocationPressuresOfReservoir(@PathVariable UUID reservoirId);

    @GetMapping(LATEST_MEASUREMENTS_BY_SECRET_KEY)
    ApiResult<WaterLevelGaugeMeasurementsDTO> getLatestMeasurementsBySecretKey(@RequestParam("secret_key") String secretKey);

    @GetMapping(GET_MEASUREMENTS_OF_LAST_WLG)
    ApiResult<List<WaterLevelGaugeMeasurementDTO>> getMeasurementsOfLastWlgByReservoir(@PathVariable UUID reservoirId,
                                                                            @RequestParam("startDate") String startDate,
                                                                            @RequestParam("endDate") String endDate);

}
