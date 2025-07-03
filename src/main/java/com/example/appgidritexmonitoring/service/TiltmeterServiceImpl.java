package com.example.appgidritexmonitoring.service;

import com.example.appgidritexmonitoring.entity.Gate;
import com.example.appgidritexmonitoring.entity.Reservoir;
import com.example.appgidritexmonitoring.entity.Tiltmeter;
import com.example.appgidritexmonitoring.entity.TiltmeterMeasurement;
import com.example.appgidritexmonitoring.exceptions.RestException;
import com.example.appgidritexmonitoring.payload.ApiResult;
import com.example.appgidritexmonitoring.payload.GateDTO;
import com.example.appgidritexmonitoring.payload.tiltmeter.TiltmeterLatestDataOfGateDTO;
import com.example.appgidritexmonitoring.payload.tiltmeter.TiltmeterMeasurementDTO;
import com.example.appgidritexmonitoring.repository.GateRepository;
import com.example.appgidritexmonitoring.repository.TiltmeterMeasurementRepository;
import com.example.appgidritexmonitoring.repository.TiltmeterRepository;
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
public class TiltmeterServiceImpl implements TiltmeterService {

    private final TiltmeterRepository repository;
    private final TiltmeterMeasurementRepository tiltmeterMeasurementRepository;
    private final GateRepository gateRepository;
    private final ReservoirServiceImpl reservoirService;

    @Override
    public ApiResult<List<TiltmeterLatestDataOfGateDTO>> getLatestMeasurementsOfReservoir(UUID reservoirId) {
        Reservoir reservoir = reservoirService.findReservoirById(reservoirId);
        List<Gate> gatesOfTiltmeters = gateRepository.getAllGatesOfTiltmetersByReservoir(reservoirId);
        List<TiltmeterMeasurement> latestMeasurementsOfReservoir = tiltmeterMeasurementRepository.getLatestMeasurementsOfReservoir(reservoirId);

        List<TiltmeterLatestDataOfGateDTO> result = new ArrayList<>();
        for (Gate gate : gatesOfTiltmeters) {
            List<TiltmeterMeasurement> tiltmeterMeasurementsOfGate = latestMeasurementsOfReservoir
                    .stream()
                    .filter(tiltmeterMeasurement ->
                            tiltmeterMeasurement.getTiltmeter().getGate().equals(gate))
                    .toList();
            List<TiltmeterMeasurementDTO> measurementDTOS = tiltmeterMeasurementsOfGate
                    .stream()
                    .map(tiltmeterMeasurement -> mapDTOFromMeasurement(tiltmeterMeasurement, false))
                    .toList();

            GateDTO gateDTO = mapGateDTOFromGate(gate);

            result.add(TiltmeterLatestDataOfGateDTO.of(gateDTO, measurementDTOS));
        }

        return ApiResult.successResponse(result);
    }

    @Override
    public ApiResult<List<TiltmeterMeasurementDTO>> getMeasurementsOfTiltmeter(UUID tiltmeterId,
                                                                               String startDateStr,
                                                                               String endDateStr) {
        getTiltmeterById(tiltmeterId);
        List<LocalDateTime> localDateTimes = CommonUtils.parseLocalDateTimesFromStingsStandart(startDateStr, endDateStr);
        LocalDateTime startDate = localDateTimes.get(0);
        LocalDateTime endDate = localDateTimes.get(1);
        List<TiltmeterMeasurement> measurementsOfTiltmeterByDates = tiltmeterMeasurementRepository.getMeasurementsOfTiltmeterByDates(tiltmeterId, startDate, endDate);
        List<TiltmeterMeasurementDTO> result = measurementsOfTiltmeterByDates
                .stream()
                .map(tiltmeterMeasurement ->
                        mapDTOFromMeasurement(tiltmeterMeasurement, true))
                .toList();

        return ApiResult.successResponse(result);
    }

    private TiltmeterMeasurementDTO mapDTOFromMeasurement(TiltmeterMeasurement measurement,
                                                          boolean skipTiltmeterFields) {
        TiltmeterMeasurementDTO dto = TiltmeterMeasurementDTO.builder()
                .date(measurement.getDate())
                .sinA(measurement.getSinAValue())
                .sinB(measurement.getSinBValue())
                .temp(measurement.getTempInDegrees())
                .tiltValue(measurement.getComputedTiltValue())
                .build();

        if (!skipTiltmeterFields) {
            Tiltmeter tiltmeter = measurement.getTiltmeter();
            dto.setId(tiltmeter.getId());
            dto.setName(tiltmeter.getName());
            dto.setOrdinal(tiltmeter.getOrdinal());
        }
        return dto;
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

    private Tiltmeter getTiltmeterById(UUID id) {
        return repository
                .findById(id)
                .orElseThrow(() ->
                        RestException.restThrow(MessageConstants.OBJECT_NOT_FOUNT, HttpStatus.BAD_REQUEST));
    }


}
