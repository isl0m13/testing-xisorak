package com.example.appgidritexmonitoring.service;

import com.example.appgidritexmonitoring.entity.Gate;
import com.example.appgidritexmonitoring.entity.Piezometer;
import com.example.appgidritexmonitoring.entity.PiezometerMeasurement;
import com.example.appgidritexmonitoring.entity.Reservoir;
import com.example.appgidritexmonitoring.exceptions.RestException;
import com.example.appgidritexmonitoring.payload.ApiResult;
import com.example.appgidritexmonitoring.payload.Coordinates;
import com.example.appgidritexmonitoring.payload.piezometer.PiezometerCoordsDTO;
import com.example.appgidritexmonitoring.payload.piezometer.*;
import com.example.appgidritexmonitoring.payload.GateDTO;
import com.example.appgidritexmonitoring.payload.projection.PiezometerCoordsProj;
import com.example.appgidritexmonitoring.payload.projection.PiezometerMeasurementAndCoordsProj;
import com.example.appgidritexmonitoring.repository.GateRepository;
import com.example.appgidritexmonitoring.repository.PiezometerMeasurementRepository;
import com.example.appgidritexmonitoring.repository.PiezometerRepository;
import com.example.appgidritexmonitoring.repository.ReservoirRepository;
import com.example.appgidritexmonitoring.util.CommonUtils;
import com.example.appgidritexmonitoring.util.DeviceResultConverter;
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
import java.util.*;

@Service
@RequiredArgsConstructor
public class PiezometerServiceImpl implements PiezometerService {

    private final PiezometerMeasurementRepository piezometerMeasurementRepository;
    private final PiezometerRepository piezometerRepository;
    private final GateRepository gateRepository;
    private final ReservoirRepository reservoirRepository;


    @Override
    public ApiResult<List<PiezometerMark>> getLocationPressuresOfGate(UUID gateId) {
        List<Piezometer> piezometers = piezometerRepository.findAllByGateIdOrderByOrdinal(gateId);
        List<PiezometerMark> result = piezometers
                .stream()
                .map(this::mapPiezometerMarkFromPiezometer)
                .toList();
        return ApiResult.successResponse(result);
    }

    @Override
    public ApiResult<?> updateLocationPressure(Double locationPressure, UUID piezometerId) {
        //GET PIEZOMETER WHICH WE SHOULD UPDATE AND EDIT LOCATION PRESSURE FIELD
        Piezometer piezometer = getPiezometerById(piezometerId);
        piezometer.setLocationPressure(locationPressure);
        piezometerRepository.save(piezometer);

        //GET MEASUREMENTS OF CURRENT PIEZOMETER WITH NEW LOCATION PRESSURE
        List<PiezometerMeasurement> piezometerMeasurements = piezometerMeasurementRepository.findAllByPiezometerId(piezometerId);
        piezometerMeasurements.forEach(this::editMeasurementByLocationPressure);
        piezometerMeasurementRepository.saveAll(piezometerMeasurements);
        return ApiResult.successResponse(MessageConstants.SUCCESSFULLY_CHANGED);
    }

    @Override
    public ApiResult<List<PiezometerMeasurementDTO>> getMeasurementsOfPiezometer(UUID piezometerId, String startDateStr, String endDateStr) {
        getPiezometerById(piezometerId);
        List<LocalDateTime> localDateTimes = CommonUtils.parseLocalDateTimesFromStingsStandart(startDateStr, endDateStr);
        LocalDateTime startDate = localDateTimes.get(0);
        LocalDateTime endDate = localDateTimes.get(1);
        List<PiezometerMeasurement> piezometerMeasurements = piezometerMeasurementRepository.getAllByPiezometerIdByDates(piezometerId, startDate, endDate);
        List<PiezometerMeasurementDTO> result = piezometerMeasurements
                .stream()
                .map(piezometerMeasurement -> mapPiezometerMeasurementDTOFromMeasurement(piezometerMeasurement, true))
                .toList();
        return ApiResult.successResponse(result);
    }

    @Override
    public ApiResult<List<PiezometersLatestDataOfGateDTO>> getLatestMeasurementsByLocationOfPressurePiezometers(UUID reservoirId, Integer location) {
        Reservoir reservoir = getReservoirById(reservoirId);
        String locationName = getLocationNameByNumOfPressurePiezometers(location, reservoir);
        if (locationName == null)
            throw RestException.restThrow(MessageConstants.OBJECT_NOT_FOUNT, HttpStatus.BAD_REQUEST);

        List<Gate> gatesByLocation = gateRepository.findAllByReservoirIdAndLocationNameForPiezometers(reservoirId, locationName);



        List<PiezometerMeasurement> allLatestOfLocation = piezometerMeasurementRepository.findAllLatestOfLocation(reservoirId, locationName);

        List<PiezometersLatestDataOfGateDTO> resultDTOS = new ArrayList<>();
        for (Gate gate : gatesByLocation) {
            List<PiezometerMeasurement> piezometerMeasurementsOfGate =
                    allLatestOfLocation
                            .stream()
                            .filter(piezometerMeasurement -> piezometerMeasurement.getPiezometer().getGate().equals(gate))
                            .toList();
            List<PiezometerMeasurementDTO> measurementDTOS = piezometerMeasurementsOfGate
                    .stream()
                    .map(piezometerMeasurement -> mapPiezometerMeasurementDTOFromMeasurement(piezometerMeasurement, false))
                    .toList();

            GateDTO gateDTO = mapGateDTOFromGate(gate);
            var gateAndPiezometersMeasurementsDTO = PiezometersLatestDataOfGateDTO.builder()
                    .gate(gateDTO)
                    .piezometerIndications(measurementDTOS)
                    .build();
            resultDTOS.add(gateAndPiezometersMeasurementsDTO);
        }

        return ApiResult.successResponse(resultDTOS);
    }

    @Override
    public ApiResult<List<PiezometerMeasurementDTO>> getLatestMeasurementsByLocationOfNoPressurePiezometers(UUID reservoirId, Integer location) {
        Reservoir reservoir = getReservoirById(reservoirId);
        String locationName = getLocationNameByNumOfNoPressurePiezometers(location, reservoir);
        if (locationName == null)
            throw RestException.restThrow(MessageConstants.OBJECT_NOT_FOUNT, HttpStatus.BAD_REQUEST);

        List<PiezometerMeasurement> allLatestOfLocation = piezometerMeasurementRepository.findAllLatestOfLocationOrderByPiezometerOrdinal(reservoirId, locationName);

        List<PiezometerMeasurementDTO> resultDTOS = new ArrayList<>();

        for (PiezometerMeasurement resultDTO : allLatestOfLocation) {
            resultDTOS.add(mapPiezometerMeasurementDTOFromMeasurement(resultDTO, false));
        }

        return ApiResult.successResponse(resultDTOS);
    }

    @Override
    public ApiResult<List<PiezometersLatestDataOfGateDTO>> getLatestMeasurementsOfReservoirByGate(UUID reservoirId) {
        List<Gate> gates = gateRepository.findAllByReservoirIdWithPiezometers(reservoirId);
        if (gates.isEmpty())
            throw RestException.restThrow(MessageConstants.GATES_NOT_FOUND, HttpStatus.NOT_FOUND);

        List<PiezometerMeasurement> latestOfAllGates =
                piezometerMeasurementRepository.findAllLatestOfAllGates(reservoirId);

        List<PiezometersLatestDataOfGateDTO> resultDTOS = new ArrayList<>();
        for (Gate gate : gates) {
            List<PiezometerMeasurement> piezometerMeasurementsOfGate =
                    latestOfAllGates
                            .stream()
                            .filter(piezometerMeasurement -> piezometerMeasurement.getPiezometer().getGate().equals(gate))
                            .toList();
            List<PiezometerMeasurementDTO> measurementDTOS = piezometerMeasurementsOfGate
                    .stream()
                    .map(piezometerMeasurement -> mapPiezometerMeasurementDTOFromMeasurement(piezometerMeasurement, false))
                    .toList();

            GateDTO gateDTO = mapGateDTOFromGate(gate);
            var gateAndPiezometersMeasurementsDTO = PiezometersLatestDataOfGateDTO.builder()
                    .gate(gateDTO)
                    .piezometerIndications(measurementDTOS)
                    .build();
            resultDTOS.add(gateAndPiezometersMeasurementsDTO);
        }

        return ApiResult.successResponse(resultDTOS);
    }

    @Override
    public ApiResult<List<PiezometerMeasurementDTO>> getLatestMeasurementsOfGate(UUID gateId) {
        getGateById(gateId);
        List<PiezometerMeasurement> latestMeasurementsOfGate = piezometerMeasurementRepository.findAllLatestOfGate(gateId);
        List<PiezometerMeasurementDTO> piezometerMeasurementDTOS = latestMeasurementsOfGate
                .stream()
                .map(piezometerMeasurement -> mapPiezometerMeasurementDTOFromMeasurement(piezometerMeasurement, false))
                .toList();
        return ApiResult.successResponse(piezometerMeasurementDTOS);
    }


    private Gate getGateById(UUID gateId) {
        return gateRepository.findById(gateId).orElseThrow(() ->
                RestException.restThrow(MessageConstants.GATE_NOT_FOUND, HttpStatus.NOT_FOUND));
    }

    @Override
    public ApiResult<List<PiezometersDataByDateDTO>> getMeasurementsOfGate(UUID gateId, String startDateStr, String endDateStr) {
        getGateById(gateId);
        List<LocalDateTime> localDateTimes = CommonUtils.parseLocalDateTimesFromStingsStandart(startDateStr, endDateStr);
        LocalDateTime startDate = localDateTimes.get(0);
        LocalDateTime endDate = localDateTimes.get(1);
        List<PiezometerMeasurement> measurementsOfGate = piezometerMeasurementRepository.findAllOfGateByDate(gateId, startDate, endDate);
        List<PiezometersDataByDateDTO> result = getPiezometersDataByDateDTOSFromMeasurements(measurementsOfGate);
        return ApiResult.successResponse(result);
    }


    @Override
    public ResponseEntity<Resource> downloadExcelOfMeasurementsOfGate(UUID gateId, String startDateStr, String endDateStr) {
        Gate gate = getGateById(gateId);
        List<LocalDateTime> localDateTimes = CommonUtils.parseLocalDateTimesFromStingsStandart(startDateStr, endDateStr);
        LocalDateTime startDate = localDateTimes.get(0);
        LocalDateTime endDate = localDateTimes.get(1);
        List<PiezometerMeasurement> measurementsOfGate = piezometerMeasurementRepository.findAllOfGateByDate(gateId, startDate, endDate);
        List<PiezometersDataByDateDTO> result = getPiezometersDataByDateDTOSFromMeasurements(measurementsOfGate);

        List<Integer> ordinals = piezometerRepository.findOrdinalByGateId(gate.getId());
        Resource file = ExcelGenerator.generateFileForPiezometers(result, ordinals);
        return Objects.nonNull(file) ? ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + "result.xlsx" + "\"")
                .body(file) : new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Override
    public ApiResult<?> getCoordinatesOfPiezometers(UUID reservoirId) {
        Reservoir reservoir = reservoirRepository.findById(reservoirId)
                .orElseThrow(() -> RestException.restThrow(MessageConstants.RESERVOIR_NOT_FOUND, HttpStatus.NOT_FOUND));

        var latestMeasurementWithCoordsOfReservoir = piezometerMeasurementRepository.getLatestMeasurementWithCoordsOfReservoir(reservoirId);
        List<PiezometerMeasurementDTO> results = latestMeasurementWithCoordsOfReservoir.stream().map(this::mapMeasurementDTOFromProjection).toList();
        return ApiResult.successResponse(results);
    }

    private Piezometer getPiezometerById(UUID piezometerId) {
        return piezometerRepository.findById(piezometerId).orElseThrow(() -> RestException.restThrow(MessageConstants.PIEZOMETER_NOT_FOUND));
    }

    private Reservoir getReservoirById(UUID reservoirId) {
        return reservoirRepository.findById(reservoirId).orElseThrow(() -> RestException.restThrow(MessageConstants.RESERVOIR_NOT_FOUND, HttpStatus.NOT_FOUND));
    }


    private List<PiezometersDataByDateDTO> getPiezometersDataByDateDTOSFromMeasurements(List<PiezometerMeasurement> measurementsOfGate) {
        List<PiezometersDataByDateDTO> result = new ArrayList<>();
        LocalDateTime localDateTime = null;
        PiezometersDataByDateDTO dto = null;
        for (PiezometerMeasurement piezometerMeasurement : measurementsOfGate) {
            LocalDateTime currentDateTime = piezometerMeasurement.getDate();
            if (!currentDateTime.equals(localDateTime)) {
                dto = PiezometersDataByDateDTO.builder()
                        .date(currentDateTime)
                        .piezometerIndications(new ArrayList<>())
                        .build();
                result.add(dto);
            }

            dto.getPiezometerIndications().add(mapPiezometerDataDTOFromMeasurement(piezometerMeasurement));
            localDateTime = currentDateTime;
        }
        return result;
    }


    private PiezometerMeasurementDTO mapPiezometerMeasurementDTOFromMeasurement(PiezometerMeasurement measurement, boolean skipPiezometerFields) {
        PiezometerMeasurementDTO piezometerMeasurementDTO = new PiezometerMeasurementDTO();
        if (!skipPiezometerFields) {
            Piezometer piezometer = measurement.getPiezometer();
            piezometerMeasurementDTO.setId(piezometer.getId());
            piezometerMeasurementDTO.setOrdinal(piezometer.getOrdinal());
            piezometerMeasurementDTO.setName(piezometer.getName());
            piezometerMeasurementDTO.setCoordinates(Coordinates.create(piezometer.getLat(), piezometer.getLon()));
        }
        piezometerMeasurementDTO.setDate(measurement.getDate());
        piezometerMeasurementDTO.setHydroPressure(measurement.getComputedPressure());
        piezometerMeasurementDTO.setTemperature(measurement.getTempInDegrees());
        piezometerMeasurementDTO.setVirtualPressure(measurement.getVirtualPressure());
        return piezometerMeasurementDTO;
    }

    private PiezometerCoordsDTO mapPiezometerCoordsDTOFromProj(PiezometerCoordsProj proj) {
        return PiezometerCoordsDTO.builder()
                .name(proj.getName())
                .id(proj.getId())
                .ordinal(proj.getOrdinal())
                .coordinates(Coordinates.create(proj.getLat(), proj.getLon()))
                .build();
    }

    private PiezometerMeasurementDTO mapMeasurementDTOFromProjection(PiezometerMeasurementAndCoordsProj proj) {
        PiezometerMeasurementDTO piezometerMeasurementDTO = new PiezometerMeasurementDTO();
        piezometerMeasurementDTO.setId(UUID.fromString(proj.getPiezometer_id()));
        piezometerMeasurementDTO.setOrdinal(proj.getOrdinal());
        piezometerMeasurementDTO.setName(proj.getName());
        piezometerMeasurementDTO.setDate(proj.getDate());
        piezometerMeasurementDTO.setHydroPressure(proj.getComputed_pressure());
        piezometerMeasurementDTO.setTemperature(proj.getTemp_in_degrees());
        piezometerMeasurementDTO.setVirtualPressure(proj.getVirtual_pressure());
        piezometerMeasurementDTO.setCoordinates(Coordinates.create(proj.getLat(), proj.getLon()));
        return piezometerMeasurementDTO;
    }


    private PiezometerDataDTO mapPiezometerDataDTOFromMeasurement(PiezometerMeasurement measurement) {
        return PiezometerDataDTO.builder()
                .id(measurement.getId())
                .ordinal(measurement.getPiezometer().getOrdinal())
                .name(measurement.getPiezometer().getName())
                .virtualPressure(measurement.getVirtualPressure())
                .hydroPressure(measurement.getComputedPressure())
                .temperature(measurement.getTempInDegrees())
                .build();
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

    private PiezometerMark mapPiezometerMarkFromPiezometer(Piezometer piezometer) {
        return PiezometerMark.builder()
                .piezometerId(piezometer.getId())
                .ordinal(piezometer.getOrdinal())
                .name(piezometer.getName())
                .locationPressure(piezometer.getLocationPressure())
                .build();
    }

    private void editMeasurementByLocationPressure(PiezometerMeasurement piezometerMeasurement) {
        List<Double> calculatedPressureResults = DeviceResultConverter.calculatePressureResult(piezometerMeasurement.getPiezometer(), piezometerMeasurement.getReadPressure());
        Double computedPressure = calculatedPressureResults.get(0);
        piezometerMeasurement.setComputedPressure(computedPressure);
    }

    private String getLocationNameByNumOfNoPressurePiezometers(Integer num, Reservoir reservoir){
        if (reservoir.getName().startsWith("Андижон сув омбори")){
            switch (num){
                case 0: return "np_left";
                case 1: return "np_804";
                case 2: return "np_812";
                case 3: return "np_907";
                case 4: return "np_right";
                default: return null;
            }
        }else {
            switch (num){
                case 0: return "np_right_side";
                case 1: return "np_left_side";
                case 2: return "np_band";
                case 3: return "np_cementation";
                case 4: return "np_drainage_and_hps";
                default: return null;
            }
        }
    }

    private String getLocationNameByNumOfPressurePiezometers(Integer num, Reservoir reservoir) {
        if (reservoir.getName().startsWith("Андижон сув омбори")) {
            return switch (num) {
                case 0 -> "p_right";
                case 1 -> "p_795";
                case 2 -> "p_804";
                case 3 -> "p_left";
                default -> null;
            };
        } else {
            return switch (num) {
                case 0 -> "p_sections";
                case 1 -> "p_short";
                case 2 -> "p_observe";
                case 3 -> "p_roman";
                default -> null;
            };
        }
    }

}
