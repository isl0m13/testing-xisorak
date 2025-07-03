package com.example.appgidritexmonitoring.controller;

import com.example.appgidritexmonitoring.payload.ApiResult;
import com.example.appgidritexmonitoring.payload.spillway.*;
import com.example.appgidritexmonitoring.util.RestConstants;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@RequestMapping(SpillwayController.SPILLWAY_CONTROLLER_BASE_PATH)
public interface SpillwayController {
    String SPILLWAY_CONTROLLER_BASE_PATH = RestConstants.BASE_PATH + "/spillways";
    String LATEST_MEASUREMENT_OF_RESERVOIR = "/reservoirs/{reservoirId}/measurements/latest";
    //String LATEST_MEASUREMENTS_BY_SECTION_OF_RESERVOIR = "/reservoir/{reservoirId}/by-section/measurements/latest";
    String MEASUREMENTS_OF_RESERVOIR = "/reservoirs/{reservoirId}/measurements";
    String MEASUREMENTS_BY_ID = "/{spillwayId}/measurements";
    String DOWNLOAD_EXCEL_FILE_OF_MEASUREMENTS_OF_RESERVOIR = "/download/excel-file/reservoirs/{reservoirId}/measurements";
    String LATEST_MEASUREMENTS_BY_SECRET_KEY = "/reservoir/measurements/latest";

    @GetMapping(LATEST_MEASUREMENT_OF_RESERVOIR)
    ApiResult<List<SpillwayLatestDataDTO>> getLatestMeasurementOfReservoir(@PathVariable UUID reservoirId);

    /*@GetMapping(LATEST_MEASUREMENTS_BY_SECTION_OF_RESERVOIR)
    ApiResult<List<SpillwayLatestDataOfGateDTO>> getLatestMeasurementsBySectionsOfLocation(@PathVariable UUID reservoirId);*/

    @GetMapping(MEASUREMENTS_BY_ID)
    ApiResult<List<SpillwayMeasurementDTO>> getMeasurementsById(@PathVariable UUID spillwayId,
                                                                @RequestParam(name = "startDate") String startDate,
                                                                @RequestParam(name = "endDate") String endDate);

    @GetMapping(MEASUREMENTS_OF_RESERVOIR)
    ApiResult<List<SpillwaysDataByDate>> getMeasurementsOfReservoirByDate(@PathVariable UUID reservoirId,
                                                                          @RequestParam(name = "startDate") String startDate,
                                                                          @RequestParam(name = "endDate") String endDate);

    @GetMapping(DOWNLOAD_EXCEL_FILE_OF_MEASUREMENTS_OF_RESERVOIR)
    ResponseEntity<Resource> downloadExcelFileOfMeasurementsOfReservoir(@PathVariable UUID reservoirId,
                                                                        @RequestParam(name = "startDate") String startDate,
                                                                        @RequestParam(name = "endDate") String endDate);

    @GetMapping(LATEST_MEASUREMENTS_BY_SECRET_KEY)
    ApiResult<SpillwayMeasurementTotalDTO> getLatestMeasurementsBySecretKey(@RequestParam("secret_key") String secretKey);


}
