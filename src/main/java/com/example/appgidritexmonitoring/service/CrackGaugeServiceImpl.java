package com.example.appgidritexmonitoring.service;

import com.example.appgidritexmonitoring.entity.CrackGauge;
import com.example.appgidritexmonitoring.entity.CrackGaugeMeasurement;
import com.example.appgidritexmonitoring.entity.Gate;
import com.example.appgidritexmonitoring.exceptions.RestException;
import com.example.appgidritexmonitoring.payload.ApiResult;
import com.example.appgidritexmonitoring.payload.GateDTO;
import com.example.appgidritexmonitoring.payload.crackgauge.CrackGaugeLatestDataOfGateDTO;
import com.example.appgidritexmonitoring.payload.crackgauge.CrackGaugeMeasurementDTO;
import com.example.appgidritexmonitoring.repository.CrackGaugeMeasurementRepository;
import com.example.appgidritexmonitoring.repository.CrackGaugeRepository;
import com.example.appgidritexmonitoring.repository.GateRepository;
import com.example.appgidritexmonitoring.util.CommonUtils;
import com.example.appgidritexmonitoring.util.MessageConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CrackGaugeServiceImpl implements CrackGaugeService {

    private final CrackGaugeMeasurementRepository crackGaugeMeasurementRepository;
    private final CrackGaugeRepository crackGaugeRepository;
    private final GateRepository gateRepository;

    @Override
    public ApiResult<List<CrackGaugeLatestDataOfGateDTO>> getLatestMeasurementsBySectionsOfLocation(Integer location) {
        String locationName = getLocationNameByNum(location);
        if (locationName == null)
            return ApiResult.errorResponse(MessageConstants.OBJECT_NOT_FOUNT, HttpStatus.BAD_REQUEST.value());

        List<CrackGauge> crackGauges = crackGaugeRepository.findAllByLocationOrderByOrdinalAscNameAsc(locationName);
        Map<Gate, List<Integer>> mapOfGateAndOrdinalsCrackGauges = getMapOfGateAndOrdinalsFromCrackGauges(crackGauges);
        List<CrackGaugeMeasurement> latestMeasurements = crackGaugeMeasurementRepository.getLatestMeasurementsOfLocation(locationName);

        Set<Gate> gates = mapOfGateAndOrdinalsCrackGauges.keySet();
        Map<Gate, List<CrackGaugeMeasurement>> measurementsByGate = new HashMap<>();

        for (Gate gate : gates) {
            List<CrackGaugeMeasurement> crackGaugeMeasurements = latestMeasurements
                    .stream()
                    .filter(crackGaugeMeasurement ->
                            crackGaugeMeasurement.getCrackGauge().getGate().equals(gate))
                    .toList();
            measurementsByGate.put(gate, crackGaugeMeasurements);
        }

        List<CrackGaugeLatestDataOfGateDTO> result = new ArrayList<>();

        for (Gate gate : gates) {
            List<CrackGaugeMeasurementDTO> crackGaugeMeasurementDTOS = new ArrayList<>();

            List<CrackGaugeMeasurement> measurementsOfGate = measurementsByGate.get(gate);
            List<Integer> ordinalsOfCrackGauges = mapOfGateAndOrdinalsCrackGauges.get(gate);
            for (Integer ordinal : ordinalsOfCrackGauges) {
                List<CrackGaugeMeasurement> measurementsOfOrdinal = measurementsOfGate
                        .stream()
                        .filter(crackGaugeMeasurement ->
                                crackGaugeMeasurement.getCrackGauge().getOrdinal().equals(ordinal))
                        .toList();
                CrackGaugeMeasurementDTO dto = mapMeasurementDTOFromMeasurements(measurementsOfOrdinal, false);
                crackGaugeMeasurementDTOS.add(dto);
            }

            GateDTO gateDTO = mapGateDTOFromGate(gate);
            result.add(CrackGaugeLatestDataOfGateDTO.make(gateDTO, crackGaugeMeasurementDTOS));
        }

        return ApiResult.successResponse(result);
    }

    @Override
    public ApiResult<List<CrackGaugeMeasurementDTO>> getMeasurementsOfCrackGaugeByDates(String crackGaugeId,
                                                                                        String startDateStr,
                                                                                        String endDateStr) {
        //CHECK crackGaugeID
        List<Integer> integers = getOrdinalAndLocationByCrackGaugeId(crackGaugeId);
        Integer ordinal = integers.get(0);
        Integer locationNum = integers.get(1);
        String locationName = getLocationNameByNum(locationNum);

        List<LocalDateTime> localDateTimes = CommonUtils.parseLocalDateTimesFromStingsStandart(startDateStr, endDateStr);
        LocalDateTime startDate = localDateTimes.get(0);
        LocalDateTime endDate = localDateTimes.get(1);

        List<CrackGaugeMeasurement> measurements = crackGaugeMeasurementRepository.getOfOrdinalAndLocationByDates(ordinal, locationName, startDate, endDate);
        List<CrackGaugeMeasurementDTO> result = new ArrayList<>();

        LocalDateTime dateTime = null;
        List<CrackGaugeMeasurement> crackGaugeMeasurements = new ArrayList<>();
        for (CrackGaugeMeasurement measurement : measurements) {
            LocalDateTime currentDateTime = measurement.getDate();
            if (Objects.nonNull(dateTime) && !currentDateTime.equals(dateTime)) {
                CrackGaugeMeasurementDTO dto = mapMeasurementDTOFromMeasurements(crackGaugeMeasurements, true);
                result.add(dto);
                crackGaugeMeasurements = new ArrayList<>();
            }
            crackGaugeMeasurements.add(measurement);
            dateTime = currentDateTime;
        }

        return ApiResult.successResponse(result);
    }

    private List<Integer> getOrdinalAndLocationByCrackGaugeId(String crackGaugeId) {
        Integer ordinal;
        Integer locationNum;

        try {
            ordinal = Integer.valueOf(crackGaugeId.substring(0, crackGaugeId.indexOf("-")).trim());
            locationNum = Integer.valueOf(crackGaugeId.substring(crackGaugeId.length() - 1).trim());
        } catch (NumberFormatException e) {
            throw RestException.restThrow(MessageConstants.NO_DATA, HttpStatus.BAD_REQUEST);
        }

        return Arrays.asList(ordinal, locationNum);
    }


    private Map<Gate, List<Integer>> getMapOfGateAndOrdinalsFromCrackGauges(List<CrackGauge> crackGauges) {
        Map<Gate, List<Integer>> result = new LinkedHashMap<>();

        if (crackGauges == null || crackGauges.isEmpty()) {
            return result;
        }

        Gate gate = null;
        List<Integer> ordinals = new ArrayList<>();
        Integer previousOrdinal = null;

        for (CrackGauge crackGauge : crackGauges) {
            Gate currentGate = crackGauge.getGate();
            Integer currentOrdinal = crackGauge.getOrdinal();

            if (gate != null && !currentGate.equals(gate)) {
                result.put(gate, ordinals);
                ordinals = new ArrayList<>();
            }

            if (!currentOrdinal.equals(previousOrdinal)) {
                ordinals.add(currentOrdinal);
            }

            gate = currentGate;
            previousOrdinal = currentOrdinal;
        }

        if (gate != null) {
            result.put(gate, ordinals);
        }

        return result;
    }



    private CrackGaugeMeasurementDTO mapMeasurementDTOFromMeasurements(List<CrackGaugeMeasurement> measurements,
                                                                       boolean skipCrackGaugeFields) {
        Double xValue = null;
        Double yValue = null;
        Double zValue = null;
        Double xTemp = null;
        Double yTemp = null;
        Double zTemp = null;
        LocalDateTime localDateTime = null;
        CrackGauge crackGauge = null;

        for (CrackGaugeMeasurement measurement : measurements) {
            localDateTime = measurement.getDate();
            String crackGaugeName = measurement.getCrackGauge().getName();
            Double computedValue = measurement.getComputedValue();
            Double tempInDegrees = measurement.getTempInDegrees();
            crackGauge = measurement.getCrackGauge();

            if (crackGaugeName.endsWith("X")) {
                xValue = computedValue;
                xTemp = tempInDegrees;
            } else if (crackGaugeName.endsWith("Y")) {
                yValue = computedValue;
                yTemp = tempInDegrees;
            } else if (crackGaugeName.endsWith("Z")) {
                zValue = computedValue;
                zTemp = tempInDegrees;
            }
        }

        CrackGaugeMeasurementDTO dto = CrackGaugeMeasurementDTO.builder()
                .date(localDateTime)
                .xValue(xValue)
                .yValue(yValue)
                .zValue(zValue)
                .xTemp(xTemp)
                .yTemp(yTemp)
                .zTemp(zTemp)
                .build();

        if (!skipCrackGaugeFields && crackGauge != null) {
            String name = crackGauge.getName();
            dto.setName(name.substring(0, name.length() - 2));
            dto.setId(crackGauge.getOrdinal() + "-" + getLocationNumFromName(crackGauge.getLocation()));
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

    private String getLocationNameByNum(Integer locationNum) {
        if (locationNum == 1)
            return "cg_885";
        else if (locationNum == 2)
            return "cg_804";
        throw RestException.restThrow(MessageConstants.GATE_NOT_FOUND, HttpStatus.BAD_REQUEST);
    }
        private Integer getLocationNumFromName (String locationName){
            if (locationName.endsWith("885"))
                return 1;
            return 2;
        }

    }
