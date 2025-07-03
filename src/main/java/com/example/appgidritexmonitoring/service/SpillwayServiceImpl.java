package com.example.appgidritexmonitoring.service;

import com.example.appgidritexmonitoring.entity.Reservoir;
import com.example.appgidritexmonitoring.entity.Spillway;
import com.example.appgidritexmonitoring.entity.SpillwayMeasurement;
import com.example.appgidritexmonitoring.exceptions.RestException;
import com.example.appgidritexmonitoring.payload.ApiResult;
import com.example.appgidritexmonitoring.payload.projection.SpillwayTotalWaterFlowProj;
import com.example.appgidritexmonitoring.payload.spillway.*;
import com.example.appgidritexmonitoring.repository.ReservoirRepository;
import com.example.appgidritexmonitoring.repository.SpillwayMeasurementRepository;
import com.example.appgidritexmonitoring.repository.SpillwayRepository;
import com.example.appgidritexmonitoring.util.CommonUtils;
import com.example.appgidritexmonitoring.util.ExcelGenerator;
import com.example.appgidritexmonitoring.util.MessageConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class SpillwayServiceImpl implements SpillwayService {

    @Value("${app.admin.secret-key}")
    private String secretKey;

    @Value("${app.admin.reservoir}")
    private String reservoirName;

    private final SpillwayMeasurementRepository spillwayMeasurementRepository;
    private final SpillwayRepository spillwayRepository;
    private final ReservoirRepository reservoirRepository;

    @Override
    public ApiResult<List<SpillwayLatestDataDTO>> getLatestMeasurementOfReservoir(UUID reservoirId) {
        Reservoir reservoir = getReservoirById(reservoirId);
        List<SpillwayMeasurement> latestMeasurementsOfReservoir = spillwayMeasurementRepository.getLatestMeasurementsOfReservoir(reservoirId);
        List<SpillwayLatestDataDTO> spillwayLatestDataDTOS = latestMeasurementsOfReservoir.stream()
                .map(this::mapSpillwayLatestDataDTOFromMeasurement)
                .toList();
        return ApiResult.successResponse(spillwayLatestDataDTOS);
    }

    /*@Override
    public ApiResult<List<SpillwayLatestDataOfGateDTO>> getLatestMeasurementsBySectionsOfLocation(UUID reservoirId) {
        return null;
    }*/

    @Override
    public ApiResult<List<SpillwayMeasurementDTO>> getMeasurementsById(UUID spillwayId, String startDate, String endDate) {
        Spillway spillway = spillwayRepository.findById(spillwayId).orElseThrow(() -> RestException.restThrow(MessageConstants.SPILLWAY_NOT_FOUND, HttpStatus.NOT_FOUND));
        List<LocalDateTime> localDateTimes = CommonUtils.parseLocalDateTimesFromStingsStandart(startDate, endDate);
        LocalDateTime startLocalDateTime = localDateTimes.get(0);
        LocalDateTime endLocalDateTime = localDateTimes.get(1);

        List<SpillwayMeasurement> measurementsOfSpillway = spillwayMeasurementRepository.getMeasurementsOfSpillway(spillwayId, startLocalDateTime, endLocalDateTime);
        List<SpillwayMeasurementDTO> spillwayMeasurementDTOS = measurementsOfSpillway.stream()
                .map(this::mapSpillwayMeasurementDTOFromMeasurement)
                .toList();
        return ApiResult.successResponse(spillwayMeasurementDTOS);
    }

    @Override
    public ApiResult<List<SpillwaysDataByDate>> getMeasurementsOfReservoirByDate(UUID reservoirId, String startDate, String endDate) {
        getReservoirById(reservoirId);
        List<LocalDateTime> localDateTimes = CommonUtils.parseLocalDateTimesFromStingsStandart(startDate, endDate);
        LocalDateTime startLocalDateTime = localDateTimes.get(0);
        LocalDateTime endLocalDateTime = localDateTimes.get(1);
        List<SpillwaysDataByDate> result = getSpillwaysDataByDate(reservoirId, startLocalDateTime, endLocalDateTime);
        return ApiResult.successResponse(result);
    }

    @Override
    public ResponseEntity<Resource> downloadExcelFileOfMeasurementsOfReservoir(UUID reservoirId, String startDate, String endDate) {
        getReservoirById(reservoirId);
        List<LocalDateTime> localDateTimes = CommonUtils.parseLocalDateTimesFromStingsStandart(startDate, endDate);
        LocalDateTime startLocalDateTime = localDateTimes.get(0);
        LocalDateTime endLocalDateTime = localDateTimes.get(1);
        List<String> namesOfSpillways = spillwayRepository.getNamesOfSpillwayByReservoir(reservoirId);
        List<SpillwaysDataByDate> spillwaysDataByDate = getSpillwaysDataByDate(reservoirId, startLocalDateTime, endLocalDateTime);

        Resource file = ExcelGenerator.generateFileForSpillways(namesOfSpillways, spillwaysDataByDate);
        return Objects.nonNull(file) ? ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + "result.xlsx" + "\"")
                .body(file) : new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Override
    public ApiResult<SpillwayMeasurementTotalDTO> getLatestMeasurementsBySecretKey(String secretKey) {
        if (!secretKey.equals(this.secretKey))
            throw RestException.restThrow(MessageConstants.SECRET_KEY_INVALID, HttpStatus.FORBIDDEN);

        Reservoir reservoir = getReservoirByName();
        SpillwayTotalWaterFlowProj spillwayTotalWaterFlowProj = spillwayMeasurementRepository
                .getTotalMeasurementOfReservoir(reservoir.getId())
                .orElseThrow(() ->
                        RestException.restThrow(MessageConstants.NO_DATA, HttpStatus.NOT_FOUND));

        SpillwayMeasurementTotalDTO result = SpillwayMeasurementTotalDTO.builder()
                .totalWaterFlow(spillwayTotalWaterFlowProj.getTotal_water_flow())
                .date(spillwayTotalWaterFlowProj.getDate())
                .build();

        return ApiResult.successResponse(result);
    }

    private List<SpillwaysDataByDate> getSpillwaysDataByDate(UUID reservoirId,
                                                             LocalDateTime startLocalDateTime,
                                                             LocalDateTime endLocalDateTime) {
        List<SpillwayMeasurement> measurementsOfReservoir = spillwayMeasurementRepository
                .getMeasurementsOfReservoirByDate(reservoirId, startLocalDateTime, endLocalDateTime);
        return mapSpillwayDataByDateListFromMeasurements(measurementsOfReservoir);
    }

    private List<SpillwaysDataByDate> mapSpillwayDataByDateListFromMeasurements(List<SpillwayMeasurement> measurements) {
        Map<LocalDateTime, List<SpillwayWaterFlowDTO>> mapOfLatestDataDTOSByDate = new LinkedHashMap<>();

        LocalDateTime localDateTime = null;
        for (SpillwayMeasurement measurement : measurements) {
            LocalDateTime currentDate = measurement.getDate();
            SpillwayWaterFlowDTO dto = mapSpillwayWaterFlowDTOFromMeasurement(measurement);
            List<SpillwayWaterFlowDTO> dtos;

            if (!currentDate.equals(localDateTime))
                dtos = new ArrayList<>();
            else
                dtos = mapOfLatestDataDTOSByDate.get(currentDate);

            dtos.add(dto);
            mapOfLatestDataDTOSByDate.put(currentDate, dtos);
            localDateTime = currentDate;
        }


        return mapOfLatestDataDTOSByDate.entrySet()
                .stream()
                .map(localDateTimeListEntry -> SpillwaysDataByDate.builder()
                        .date(localDateTimeListEntry.getKey())
                        .indications(localDateTimeListEntry.getValue())
                        .build())
                .toList();
    }

    private SpillwayWaterFlowDTO mapSpillwayWaterFlowDTOFromMeasurement(SpillwayMeasurement measurement) {
        return SpillwayWaterFlowDTO.builder()
                .spillway(mapSpillwayDTOFromSpillway(measurement.getSpillway()))
                .waterFlow(measurement.getComputedWaterFlow())
                .turbidityIndication(measurement.getTurbidityValue())
                .build();
    }

    private SpillwayLatestDataDTO mapSpillwayLatestDataDTOFromMeasurement(SpillwayMeasurement measurement) {
        SpillwayDTO spillwayDTO = mapSpillwayDTOFromSpillway(measurement.getSpillway());
        var spillwayLatestDataDTO = new SpillwayLatestDataDTO();
        spillwayLatestDataDTO.setSpillway(spillwayDTO);
        spillwayLatestDataDTO.setDate(measurement.getDate());
        spillwayLatestDataDTO.setWaterFlow(measurement.getComputedWaterFlow());
        spillwayLatestDataDTO.setTemperature(measurement.getTempInDegrees());
        spillwayLatestDataDTO.setTurbidityIndication(measurement.getTurbidityValue());
        return spillwayLatestDataDTO;
    }

    private SpillwayMeasurementDTO mapSpillwayMeasurementDTOFromMeasurement(SpillwayMeasurement measurement) {
        return SpillwayMeasurementDTO.builder()
                .date(measurement.getDate())
                .waterFlow(measurement.getComputedWaterFlow())
                .turbidityIndication(measurement.getTurbidityValue())
                .temperature(measurement.getTempInDegrees())
                .build();
    }

    private SpillwayDTO mapSpillwayDTOFromSpillway(Spillway spillway) {
        return SpillwayDTO.builder()
                .id(spillway.getId())
                .name(spillway.getName())
                .ordinal(spillway.getOrdinal())
                .build();
    }

    private Reservoir getReservoirByName() {
        List<Reservoir> reservoirs = reservoirRepository.findAllByOrderByNameDesc();
       /* if (reservoirName.equals("xisorak"))
            return reservoirs.get(0);*/
        return reservoirs.get(0);
    }

    private Reservoir getReservoirById(UUID reservoirId) {
        return reservoirRepository.findById(reservoirId).orElseThrow(() -> RestException.restThrow(MessageConstants.RESERVOIR_NOT_FOUND, HttpStatus.NOT_FOUND));
    }


}
