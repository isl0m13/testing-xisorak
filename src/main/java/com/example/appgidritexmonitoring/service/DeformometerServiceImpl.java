package com.example.appgidritexmonitoring.service;

import com.example.appgidritexmonitoring.entity.*;
import com.example.appgidritexmonitoring.exceptions.RestException;
import com.example.appgidritexmonitoring.payload.ApiResult;
import com.example.appgidritexmonitoring.payload.crackgauge.CrackGaugeMeasurementDTO;
import com.example.appgidritexmonitoring.payload.deformometer.DeformometerDTO;
import com.example.appgidritexmonitoring.payload.deformometer.DeformometerDataByDateDTO;
import com.example.appgidritexmonitoring.payload.deformometer.DeformometerMeasurementDTO;
import com.example.appgidritexmonitoring.payload.spillway.SpillwayWaterFlowDTO;
import com.example.appgidritexmonitoring.payload.spillway.SpillwaysDataByDate;
import com.example.appgidritexmonitoring.repository.DeformometerMeasurementRepository;
import com.example.appgidritexmonitoring.repository.DeformometerRepository;
import com.example.appgidritexmonitoring.util.CommonUtils;
import com.example.appgidritexmonitoring.util.MessageConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DeformometerServiceImpl implements DeformometerService {

    private final DeformometerRepository deformometerRepository;
    private final DeformometerMeasurementRepository deformometerMeasurementRepository;
    private final ReservoirServiceImpl reservoirService;

    @Override
    public ApiResult<List<DeformometerMeasurementDTO>> getLatestMeasurementsOfReservoir(UUID reservoirId) {
        List<Deformometer> deformometers = deformometerRepository.findAllByReservoirIdOrderByOrdinalAscNameAsc(reservoirId);
        if (deformometers.isEmpty())
            throw RestException.restThrow(MessageConstants.OBJECT_NOT_FOUNT, HttpStatus.NOT_FOUND);
        List<DeformometerMeasurement> latestMeasurements = deformometerMeasurementRepository.getLatestMeasurementsOfReservoir(reservoirId);
        List<DeformometerMeasurementDTO> result = latestMeasurements
                .stream()
                .map(measurement -> mapDeformometerMeasurementDTOFromMeasurement(measurement, false))
                .toList();

        return ApiResult.successResponse(result);
    }

    @Override
    public ApiResult<List<?>> getMeasurementsOfReservoir(UUID reservoirId, String startDateStr, String endDateStr) {
        Reservoir reservoir = reservoirService.findReservoirById(reservoirId);
        List<LocalDateTime> localDateTimes = CommonUtils.parseLocalDateTimesFromStingsStandart(startDateStr, endDateStr);
        LocalDateTime startDate = localDateTimes.get(0);
        LocalDateTime endDate = localDateTimes.get(1);

        List<DeformometerMeasurement> measurements = deformometerMeasurementRepository.getMeasurementsOfReservoirByDates(reservoirId, startDate, endDate);
        List<DeformometerDataByDateDTO> result = mapDeformometerDataByDateListFromMeasurements(measurements);

        return ApiResult.successResponse(result);
    }

    @Override
    public ApiResult<List<DeformometerMeasurementDTO>> getMeasurementsOfDeformometer(UUID deformometerId, String startDateStr, String endDateStr) {
        List<LocalDateTime> localDateTimes = CommonUtils.parseLocalDateTimesFromStingsStandart(startDateStr, endDateStr);
        LocalDateTime startDate = localDateTimes.get(0);
        LocalDateTime endDate = localDateTimes.get(1);

        List<DeformometerMeasurement> measurements = deformometerMeasurementRepository.getMeasurementsOfDeformometerByDates(deformometerId, startDate, endDate);
        List<DeformometerMeasurementDTO> result = measurements
                .stream()
                .map(measurement -> mapDeformometerMeasurementDTOFromMeasurement(measurement, true))
                .toList();

        return ApiResult.successResponse(result);
    }

    private List<DeformometerDataByDateDTO> mapDeformometerDataByDateListFromMeasurements(List<DeformometerMeasurement> measurements) {
        Map<LocalDateTime, List<DeformometerMeasurementDTO>> mapOfLatestDataDTOSByDate = new LinkedHashMap<>();

        LocalDateTime localDateTime = null;
        for (DeformometerMeasurement measurement : measurements) {
            LocalDateTime currentDate = measurement.getDate();
            DeformometerMeasurementDTO dto = mapDeformometerMeasurementDTOFromMeasurement(measurement, false);
            List<DeformometerMeasurementDTO> dtos;

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
                .map(localDateTimeListEntry -> DeformometerDataByDateDTO.builder()
                        .date(localDateTimeListEntry.getKey())
                        .indications(localDateTimeListEntry.getValue())
                        .build())
                .toList();
    }


    private DeformometerMeasurementDTO mapDeformometerMeasurementDTOFromMeasurement(DeformometerMeasurement measurement,
                                                                                    boolean skipParentFields) {
        DeformometerMeasurementDTO dto = DeformometerMeasurementDTO.builder()
                .date(measurement.getDate())
                .temperature(measurement.getTempInDegrees())
                .displacementValue(measurement.getComputedValue())
                .build();
        if (!skipParentFields) {
            DeformometerDTO deformometerDTO = mapDeformometerDTOFromDeformometer(measurement.getDeformometer());
            dto.setDeformometer(deformometerDTO);
        }
        return dto;
    }

    private DeformometerDTO mapDeformometerDTOFromDeformometer(Deformometer deformometer) {
        return DeformometerDTO.builder()
                .id(deformometer.getId())
                .name(deformometer.getName())
                .ordinal(deformometer.getOrdinal())
                .build();
    }




    }