package com.example.appgidritexmonitoring.service;

import com.example.appgidritexmonitoring.entity.HydrologicalStation;
import com.example.appgidritexmonitoring.entity.HydrologicalStationMeasurement;
import com.example.appgidritexmonitoring.entity.WaterFlowMeter;
import com.example.appgidritexmonitoring.entity.WaterFlowMeterMeasurement;
import com.example.appgidritexmonitoring.payload.*;
import com.example.appgidritexmonitoring.repository.HydrologicalStationMeasurementRepository;
import com.example.appgidritexmonitoring.repository.HydrologicalStationRepository;
import com.example.appgidritexmonitoring.repository.WaterFlowMeterMeasurementRepository;
import com.example.appgidritexmonitoring.repository.WaterFlowMeterRepository;
import com.example.appgidritexmonitoring.util.CommonUtils;
import com.example.appgidritexmonitoring.util.ExcelGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HydrologicalStationServiceImpl implements HydrologicalStationService {
    private final HydrologicalStationRepository hydrologicalStationRepository;
    private final HydrologicalStationMeasurementRepository hydrologicalStationMeasurementRepository;
    private final WaterFlowMeterMeasurementRepository waterFlowMeterMeasurementRepository;
    private final ReservoirServiceImpl reservoirServiceImpl;


    @Override
    public ApiResult<List<HydrologicalStationMeasurementDTO>> getLatestMeasurementsOfReservoir(UUID reservoirId) {
        reservoirServiceImpl.findReservoirById(reservoirId);
        List<HydrologicalStationMeasurement> measurements =
                hydrologicalStationMeasurementRepository.getLatestMeasurementsOfReservoir(reservoirId);
        List<HydrologicalStationMeasurementDTO> result = measurements.stream()
                .map(this::mapDTOFromHydrologicalStationMeasurement)
                .toList();

        return ApiResult.successResponse(result);
    }


    @Override
    public ApiResult<?> getMeasurementsOfReservoir(UUID reservoirId, String startDateStr, String endDateStr) {
        reservoirServiceImpl.getReservoirById(reservoirId);
        List<LocalDateTime> localDateTimes = CommonUtils.parseLocalDateTimesFromStingsStandart(startDateStr, endDateStr);
        LocalDateTime startDate = localDateTimes.get(0);
        LocalDateTime endDate = localDateTimes.get(1);
        var result = getHydrologicalStationMeasurementDTOs(reservoirId, startDate, endDate);
        return ApiResult.successResponse(result);
    }

    @Override
    public ResponseEntity<Resource> downloadExcelFileOfMeasurementsOfReservoir(UUID reservoirId,
                                                                               String startDateStr,
                                                                               String endDateStr) {
        reservoirServiceImpl.getReservoirById(reservoirId);
        List<LocalDateTime> localDateTimes = CommonUtils.parseLocalDateTimesFromStingsStandart(startDateStr, endDateStr);
        LocalDateTime startDate = localDateTimes.get(0);
        LocalDateTime endDate = localDateTimes.get(1);
        var hydrologicalStationMeasurementDTOs = getHydrologicalStationMeasurementDTOs(reservoirId, startDate, endDate);
        List<String> namesOfHydrologicalStations = hydrologicalStationRepository.getAllNamesByReservoir(reservoirId);

        Resource file = ExcelGenerator.generateFileForHydrologicalStations(namesOfHydrologicalStations, hydrologicalStationMeasurementDTOs);
        return Objects.nonNull(file) ? ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + "result.xlsx" + "\"")
                .body(file) : new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Override
    public ApiResult<?> getLatestHydrologicalAndWaterFlowMeasurementsOfReservoir(UUID reservoirId) {
        List<HydrologicalStationMeasurement> latestHydrologicalMeasurementsOfReservoir = hydrologicalStationMeasurementRepository
                .getLatestMeasurementsOfReservoir(reservoirId);
        WaterFlowMeterMeasurement latestWaterFlowMeterMeasurementOfReservoir = waterFlowMeterMeasurementRepository.getLatestMeasurementOfReservoir(reservoirId);

        List<MeasurementWithCoordsDTO> coordsDTOS = latestHydrologicalMeasurementsOfReservoir
                .stream()
                .map(this::mapMeasurementWithCoordsDTOFromHydrologicalStationMeasurement)
                .toList();

        List<MeasurementWithCoordsDTO> result = new ArrayList<>(coordsDTOS);
        result.add(mapMeasurementWithCoordsFromWaterFlowMeasurement(latestWaterFlowMeterMeasurementOfReservoir));

        return ApiResult.successResponse(result);
    }

    private List<HydrologicalStationMeasurementDTO> getHydrologicalStationMeasurementDTOs(UUID reservoirId,
                                                                                          LocalDateTime startDate,
                                                                                          LocalDateTime endDate) {
        var measurements = hydrologicalStationMeasurementRepository
                .getMeasurementsOfReservoirByDates(reservoirId, startDate, endDate);
        return mapDTOsFromMeasurements(measurements);
    }

    private List<HydrologicalStationMeasurementDTO> mapDTOsFromMeasurements(List<HydrologicalStationMeasurement> measurements) {
        List<HydrologicalStationMeasurementDTO> result = new ArrayList<>();
        LocalDateTime localDateTime = null;
        HydrologicalStationMeasurementDTO currentDTO = null;

        for (HydrologicalStationMeasurement measurement : measurements) {
            LocalDateTime currentDate = measurement.getDate();
            if (!currentDate.equals(localDateTime)) {
                currentDTO = HydrologicalStationMeasurementDTO.builder()
                        .date(currentDate)
                        .indications(new ArrayList<>())
                        .build();
                result.add(currentDTO);
            }

            currentDTO.getIndications().add(mapDTOFromHydrologicalStationMeasurement(measurement));
            localDateTime = currentDate;
        }
        return result;
    }


    private HydrologicalStationMeasurementDTO mapDTOFromHydrologicalStationMeasurement(HydrologicalStationMeasurement measurement) {
        return HydrologicalStationMeasurementDTO.builder()
                .hydrologicalStation(mapDTOFromHydrologicalStation(measurement.getHydrologicalStation()))
                .date(measurement.getDate())
                .waterFlow(measurement.getComputedWaterFlow())
                .virtualPressure(measurement.getVirtualPressure())
                .temperature(measurement.getTempInDegrees())
                .build();
    }

    private HydrologicalStationDTO mapDTOFromHydrologicalStation(HydrologicalStation hydrologicalStation) {
        return HydrologicalStationDTO.builder()
                .id(hydrologicalStation.getId())
                .name(hydrologicalStation.getName())
                .ordinal(hydrologicalStation.getOrdinal())
                .build();
    }

    private MeasurementWithCoordsDTO mapMeasurementWithCoordsDTOFromHydrologicalStationMeasurement(HydrologicalStationMeasurement measurement){
        HydrologicalStation hydrologicalStation = measurement.getHydrologicalStation();
        return MeasurementWithCoordsDTO.builder()
                .name(hydrologicalStation.getName())
                .waterInd(measurement.getComputedWaterFlow())
                .waterVirtualInd(measurement.getVirtualPressure())
                .temperature(measurement.getTempInDegrees())
                .date(measurement.getDate())
                .coords(Coordinates.create(hydrologicalStation.getLat(), hydrologicalStation.getLon()))
                .build();
    }

    private MeasurementWithCoordsDTO mapMeasurementWithCoordsFromWaterFlowMeasurement(WaterFlowMeterMeasurement measurement){
        WaterFlowMeter waterFlowMeter = measurement.getWaterFlowMeter();
        return MeasurementWithCoordsDTO.builder()
                .name("Дренаж насос")
                .waterInd(measurement.getComputedWaterFlow())
                .temperature(measurement.getTempInDegrees())
                .date(measurement.getDate())
                .coords(Coordinates.create(waterFlowMeter.getLat(), waterFlowMeter.getLon()))
                .build();
    }


}
