package com.example.appgidritexmonitoring.service;

import com.example.appgidritexmonitoring.entity.*;
import com.example.appgidritexmonitoring.exceptions.RestException;
import com.example.appgidritexmonitoring.payload.ApiResult;
import com.example.appgidritexmonitoring.payload.GateDTO;
import com.example.appgidritexmonitoring.payload.piezometer.PiezometerMeasurementDTO;
import com.example.appgidritexmonitoring.payload.piezometer.PiezometersLatestDataOfGateDTO;
import com.example.appgidritexmonitoring.payload.plumb.PlumbLatestDataOfGateDTO;
import com.example.appgidritexmonitoring.payload.plumb.PlumbMeasurementDTO;
import com.example.appgidritexmonitoring.repository.GateRepository;
import com.example.appgidritexmonitoring.repository.PlumbMeasurementRepository;
import com.example.appgidritexmonitoring.repository.PlumbRepository;
import com.example.appgidritexmonitoring.repository.ReservoirRepository;
import com.example.appgidritexmonitoring.util.CommonUtils;
import com.example.appgidritexmonitoring.util.MessageConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PlumbServiceImpl implements PlumbService {

    private final PlumbMeasurementRepository plumbMeasurementRepository;
    private final ReservoirRepository reservoirRepository;
    private final GateRepository gateRepository;
    private final PlumbRepository plumbRepository;

    @Override
    public ApiResult<List<PlumbLatestDataOfGateDTO>> getLatestMeasurementsByGateOfReservoir(UUID reservoirId) {
        findReservoirById(reservoirId);
        List<Gate> gatesOfPlums = gateRepository.getAllOfPlumsOrderByOrdinalDesc();
        List<PlumbMeasurement> latestMeasurementsByReservoirId = plumbMeasurementRepository.getLatestMeasurementsByReservoirId(reservoirId);

        List<PlumbLatestDataOfGateDTO> resultDTOS = new ArrayList<>();
        for (Gate gate : gatesOfPlums) {
            List<PlumbMeasurement> plumbMeasurementsOfGate =
                    latestMeasurementsByReservoirId
                            .stream()
                            .filter(plumbMeasurement -> plumbMeasurement.getPlumb().getGate().equals(gate))
                            .toList();
            List<PlumbMeasurementDTO> measurementDTOS = plumbMeasurementsOfGate
                    .stream()
                    .map(plumbMeasurement -> mapPlumbMeasurementDTOFromMeasurement(plumbMeasurement, false))
                    .toList();

            GateDTO gateDTO = mapGateDTOFromGate(gate);
            var gateAndPlumbsMeasurementsDTO = PlumbLatestDataOfGateDTO.builder()
                    .gate(gateDTO)
                    .indications(measurementDTOS)
                    .build();
            resultDTOS.add(gateAndPlumbsMeasurementsDTO);
        }

        return ApiResult.successResponse(resultDTOS);
    }


    @Override
    public ApiResult<List<PlumbMeasurementDTO>> getMeasurementsOfPlumb(UUID plumbId, String startDateStr, String endDateStr) {
        getPlumbById(plumbId);
        List<LocalDateTime> localDateTimes = CommonUtils.parseLocalDateTimesFromStingsStandart(startDateStr, endDateStr);
        LocalDateTime startDate = localDateTimes.get(0);
        LocalDateTime endDate = localDateTimes.get(1);
        List<PlumbMeasurement> measurements = plumbMeasurementRepository.getAllByPlumbIdByDates(plumbId, startDate, endDate);
        List<PlumbMeasurementDTO> result = measurements
                .stream()
                .map(plumbMeasurement -> mapPlumbMeasurementDTOFromMeasurement(plumbMeasurement, true))
                .toList();
        return ApiResult.successResponse(result);
    }

    public Reservoir findReservoirById(UUID reservoirId) {
        return reservoirRepository
                .findById(reservoirId)
                .orElseThrow(() -> RestException.restThrow(MessageConstants.RESERVOIR_NOT_FOUND, HttpStatus.NOT_FOUND));
    }

    private PlumbMeasurementDTO mapPlumbMeasurementDTOFromMeasurement(PlumbMeasurement plumbMeasurement, boolean skipPlumbFields) {
        PlumbMeasurementDTO result = PlumbMeasurementDTO.builder()
                .date(plumbMeasurement.getDate())
                .temp(plumbMeasurement.getTempInDegrees())
                .xValue(plumbMeasurement.getComputedXValue())
                .yValue(plumbMeasurement.getComputedYValue())
                .build();
        if (!skipPlumbFields) {
            Plumb plumb = plumbMeasurement.getPlumb();
            result.setId(plumb.getId());
            result.setName(plumb.getName());
        }
        return result;
    }

    private GateDTO mapGateDTOFromGate(Gate gate) {
        GateDTO gateDTO = GateDTO.builder()
                .id(gate.getId())
                .ordinal(gate.getOrdinal())
                .build();

        if (Objects.nonNull(gate.getName()))
            gateDTO.setName(gate.getName());
        return gateDTO;
    }

    private Plumb getPlumbById(UUID plumbId) {
        return plumbRepository
                .findById(plumbId)
                .orElseThrow(() -> RestException.restThrow(MessageConstants.OBJECT_NOT_FOUNT, HttpStatus.NOT_FOUND));
    }


}
