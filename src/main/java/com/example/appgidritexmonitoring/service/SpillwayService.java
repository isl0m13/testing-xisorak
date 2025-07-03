package com.example.appgidritexmonitoring.service;

import com.example.appgidritexmonitoring.payload.ApiResult;
import com.example.appgidritexmonitoring.payload.spillway.*;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

public interface SpillwayService {
    ApiResult<List<SpillwayLatestDataDTO>> getLatestMeasurementOfReservoir(UUID reservoirId);

    ApiResult<List<SpillwayMeasurementDTO>> getMeasurementsById(UUID spillwayId, String startDate, String endDate);

    ApiResult<List<SpillwaysDataByDate>> getMeasurementsOfReservoirByDate(UUID reservoirId, String startDate, String endDate);

    ResponseEntity<Resource> downloadExcelFileOfMeasurementsOfReservoir(UUID reservoirId, String startDate, String endDate);

    ApiResult<SpillwayMeasurementTotalDTO> getLatestMeasurementsBySecretKey(String secretKey);


    //ApiResult<List<SpillwayLatestDataOfGateDTO>> getLatestMeasurementsBySectionsOfLocation(UUID reservoirId);

}
