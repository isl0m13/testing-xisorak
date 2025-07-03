package com.example.appgidritexmonitoring.controller;

import com.example.appgidritexmonitoring.payload.ApiResult;
import com.example.appgidritexmonitoring.payload.spillway.*;
import com.example.appgidritexmonitoring.service.SpillwayService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class SpillwayControllerImpl implements SpillwayController {
    private final SpillwayService spillwayService;

    @Override
    public ApiResult<List<SpillwayLatestDataDTO>> getLatestMeasurementOfReservoir(UUID reservoirId) {
        return spillwayService.getLatestMeasurementOfReservoir(reservoirId);
    }
 /*
    @Override
    public ApiResult<List<SpillwayLatestDataOfGateDTO>> getLatestMeasurementsBySectionsOfLocation(UUID reservoirId) {
        return spillwayService.getLatestMeasurementsBySectionsOfLocation(reservoirId);
    }*/

    @Override
    public ApiResult<List<SpillwayMeasurementDTO>> getMeasurementsById(UUID spillwayId,
                                                                       String startDate,
                                                                       String endDate) {
        return spillwayService.getMeasurementsById(spillwayId, startDate, endDate);
    }


    @Override
    public ApiResult<List<SpillwaysDataByDate>> getMeasurementsOfReservoirByDate(UUID reservoirId,
                                                                                 String startDate,
                                                                                 String endDate) {
        return spillwayService.getMeasurementsOfReservoirByDate(reservoirId, startDate, endDate);
    }

    @Override
    public ResponseEntity<Resource> downloadExcelFileOfMeasurementsOfReservoir(UUID reservoirId,
                                                                               String startDate,
                                                                               String endDate) {
        return spillwayService.downloadExcelFileOfMeasurementsOfReservoir(reservoirId, startDate, endDate);
    }

    @Override
    public ApiResult<SpillwayMeasurementTotalDTO> getLatestMeasurementsBySecretKey(String secretKey) {
        return spillwayService.getLatestMeasurementsBySecretKey(secretKey);
    }


}
