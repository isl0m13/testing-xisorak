package com.example.appgidritexmonitoring.service;

import com.example.appgidritexmonitoring.entity.Reservoir;
import com.example.appgidritexmonitoring.entity.WaterFlowMeterMeasurement;
import com.example.appgidritexmonitoring.exceptions.RestException;
import com.example.appgidritexmonitoring.payload.ApiResult;
import com.example.appgidritexmonitoring.payload.WaterFlowMeterMeasurementDTO;
import com.example.appgidritexmonitoring.repository.ReservoirRepository;
import com.example.appgidritexmonitoring.repository.WaterFlowMeterMeasurementRepository;
import com.example.appgidritexmonitoring.util.CommonUtils;
import com.example.appgidritexmonitoring.util.ExcelGenerator;
import com.example.appgidritexmonitoring.util.MessageConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WaterFlowMeterServiceImpl implements WaterFlowMeterService {

    private final WaterFlowMeterMeasurementRepository waterFlowMeterMeasurementRepository;
    private final ReservoirRepository reservoirRepository;

    @Override
    public ApiResult<WaterFlowMeterMeasurementDTO> getLatestMeasurementsOfReservoir(UUID reservoirId) {
        getReservoirById(reservoirId);
        WaterFlowMeterMeasurement latestMeasurement = waterFlowMeterMeasurementRepository.getLatestMeasurementOfReservoir(reservoirId);
        WaterFlowMeterMeasurementDTO result = null;
        if (Objects.nonNull(latestMeasurement))
            result = mapWaterFlowMeterMeasurementToDTO(latestMeasurement);
        return ApiResult.successResponse(result);
    }

    @Override
    public ApiResult<List<WaterFlowMeterMeasurementDTO>> getMeasurementsOfReservoir(UUID reservoirId, String startDate, String endDate) {
        getReservoirById(reservoirId);
        List<LocalDateTime> localDateTimes = CommonUtils.parseLocalDateTimesFromStingsStandart(startDate, endDate);
        LocalDateTime startLocalDateTime = localDateTimes.get(0);
        LocalDateTime endLocalDateTime = localDateTimes.get(1);
        var result = mapWaterFlowMeterMeasurementDTOSFromMeasurements(reservoirId, startLocalDateTime, endLocalDateTime);
        return ApiResult.successResponse(result);
    }

    @Override
    public ResponseEntity<Resource> downloadExcelOfMeasurementsOfReservoir(UUID reservoirId, String startDate, String endDate) {
        getReservoirById(reservoirId);
        List<LocalDateTime> localDateTimes = CommonUtils.parseLocalDateTimesFromStingsStandart(startDate, endDate);
        LocalDateTime startLocalDateTime = localDateTimes.get(0);
        LocalDateTime endLocalDateTime = localDateTimes.get(1);
        var result = mapWaterFlowMeterMeasurementDTOSFromMeasurements(reservoirId, startLocalDateTime, endLocalDateTime);
        Resource file = ExcelGenerator.generateFileForWaterFlowMeters(result);
        return Objects.nonNull(file) ? ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + "result.xlsx" + "\"")
                .body(file) : new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private List<WaterFlowMeterMeasurementDTO> mapWaterFlowMeterMeasurementDTOSFromMeasurements(UUID reservoirId, LocalDateTime startDate, LocalDateTime endDate) {
        var measurementsOfReservoirs = waterFlowMeterMeasurementRepository.getMeasurementsOfReservoirs(reservoirId, startDate, endDate);
        return measurementsOfReservoirs
                .stream()
                .map(this::mapWaterFlowMeterMeasurementToDTO)
                .toList();
    }


    private WaterFlowMeterMeasurementDTO mapWaterFlowMeterMeasurementToDTO(WaterFlowMeterMeasurement measurement) {
        return WaterFlowMeterMeasurementDTO.builder()
                .waterFlow(measurement.getComputedWaterFlow())
                .date(measurement.getDate())
                .temperature(measurement.getTempInDegrees())
                .build();
    }

    private Reservoir getReservoirById(UUID reservoirId) {
        return reservoirRepository.findById(reservoirId).orElseThrow(() -> RestException.restThrow(MessageConstants.RESERVOIR_NOT_FOUND, HttpStatus.BAD_REQUEST));
    }
}
