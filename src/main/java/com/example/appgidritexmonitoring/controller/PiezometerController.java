package com.example.appgidritexmonitoring.controller;

import com.example.appgidritexmonitoring.payload.ApiResult;
import com.example.appgidritexmonitoring.payload.piezometer.PiezometerMark;
import com.example.appgidritexmonitoring.payload.piezometer.PiezometerMeasurementDTO;
import com.example.appgidritexmonitoring.payload.piezometer.PiezometersDataByDateDTO;
import com.example.appgidritexmonitoring.payload.piezometer.PiezometersLatestDataOfGateDTO;
import com.example.appgidritexmonitoring.util.RestConstants;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RequestMapping(PiezometerController.PIEZOMETER_CONTROLLER_BASE_PATH)
public interface PiezometerController {
    String PIEZOMETER_CONTROLLER_BASE_PATH = RestConstants.BASE_PATH + "/piezometers";
    String LOCATION_PRESSURES_OF_GATE = "/gate/{gateId}/marks";
    String UPDATE_LOCATION_PRESSURE_OF_PIEZOMETER = "/{piezometerId}/update/mark";
    String MEASUREMENTS_OF_PIEZOMETER = "/{piezometerId}/measurement";
    String LATEST_MEASUREMENTS_BY_GATES_OF_RESERVOIR = "/reservoirs/{reservoirId}/measurements/latest";
    String MEASUREMENTS_OF_GATE = "/gates/{gateId}/measurements";
    String LATEST_MEASUREMENTS_OF_GATE = "/gates/{gateId}/measurements/latest";
    String DOWNLOAD_EXCEL_FILE_OF_MEASUREMENTS_OF_GATE = "/download/excel-file/gates/{gateId}/measurements";
    String LATEST_MEASUREMENTS_WITH_COORDS = "/reservoirs/{reservoirId}/measurements-coords/latest";
    String LATEST_MEASUREMENTS_DIVIDED_BY_LOCATIONS_FOR_PRESSURE_PIEZOMETERS = "/pressure/reservoirs/{reservoirId}/measurements-by-location/{location}/latest";
    String LATEST_MEASUREMENTS_DIVIDED_BY_LOCATION_FOR_NO_PRESSURE_PIEZOMETERS = "/no-pressure/reservoirs/{reservoirId}/measurements-by-location/{location}/latest";

    @PutMapping(UPDATE_LOCATION_PRESSURE_OF_PIEZOMETER)
    ApiResult<?> updateLocationPressure(@RequestParam("mark") Double locationPressure, @PathVariable UUID piezometerId);

    @GetMapping(LOCATION_PRESSURES_OF_GATE)
    ApiResult<List<PiezometerMark>> getLocationPressuresOfGate(@PathVariable UUID gateId);

    @GetMapping(LATEST_MEASUREMENTS_BY_GATES_OF_RESERVOIR)
    ApiResult<List<PiezometersLatestDataOfGateDTO>> getLatestMeasurementsOfReservoirByGate(@PathVariable("reservoirId") UUID reservoirId);

    @GetMapping(LATEST_MEASUREMENTS_OF_GATE)
    ApiResult<List<PiezometerMeasurementDTO>> getLatestMeasurementsOfGate(@PathVariable("gateId") UUID gateId);

    @GetMapping(MEASUREMENTS_OF_GATE)
    ApiResult<List<PiezometersDataByDateDTO>> getMeasurementsOfGate(@PathVariable UUID gateId,
                                                                    @RequestParam(name = "startDate") String startDate,
                                                                    @RequestParam(name = "endDate") String endDate);

    @GetMapping(DOWNLOAD_EXCEL_FILE_OF_MEASUREMENTS_OF_GATE)
    ResponseEntity<Resource> downloadExcelOfMeasurementsOfGate(@PathVariable UUID gateId,
                                                               @RequestParam(name = "startDate") String startDate,
                                                               @RequestParam(name = "endDate") String endDate);

    @GetMapping(LATEST_MEASUREMENTS_WITH_COORDS)
    ApiResult<?> getCoordinatesOfPiezometers(@PathVariable UUID reservoirId);

    @GetMapping(MEASUREMENTS_OF_PIEZOMETER)
    ApiResult<List<PiezometerMeasurementDTO>> getMeasurementsOfPiezometer(@PathVariable UUID piezometerId,
                                                                          @RequestParam(name = "startDate") String startDate,
                                                                          @RequestParam(name = "endDate") String endDate);

    @GetMapping(LATEST_MEASUREMENTS_DIVIDED_BY_LOCATIONS_FOR_PRESSURE_PIEZOMETERS)
    ApiResult<List<PiezometersLatestDataOfGateDTO>> getLatestMeasurementsByLocationOfPressurePiezometers(@PathVariable UUID reservoirId,
                                                                                                          @PathVariable Integer location);

    @GetMapping(LATEST_MEASUREMENTS_DIVIDED_BY_LOCATION_FOR_NO_PRESSURE_PIEZOMETERS)
    ApiResult<List<PiezometerMeasurementDTO>> getLatestMeasurementsByLocationOfNoPressurePiezometers(@PathVariable UUID reservoirId,
                                                                                                     @PathVariable Integer location);

}
