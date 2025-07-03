package com.example.appgidritexmonitoring.service;

import com.example.appgidritexmonitoring.entity.Reservoir;
import com.example.appgidritexmonitoring.entity.WaterLevelGauge;
import com.example.appgidritexmonitoring.entity.WaterLevelGaugeMeasurement;
import com.example.appgidritexmonitoring.exceptions.RestException;
import com.example.appgidritexmonitoring.payload.ApiResult;
import com.example.appgidritexmonitoring.payload.CalculatedRes;
import com.example.appgidritexmonitoring.payload.projection.HydrologicalStationMeasurementProj;
import com.example.appgidritexmonitoring.payload.projection.WaterLevelGaugeMeasurementProj;
import com.example.appgidritexmonitoring.payload.projection.WaterLevelGaugeVolumeProj;
import com.example.appgidritexmonitoring.payload.waterlevelgauge.*;
import com.example.appgidritexmonitoring.payload.projection.WaterLevelGaugeMeasurementDiffProjection;
import com.example.appgidritexmonitoring.repository.HydrologicalStationMeasurementRepository;
import com.example.appgidritexmonitoring.repository.ReservoirRepository;
import com.example.appgidritexmonitoring.repository.WaterLevelGaugeMeasurementRepository;
import com.example.appgidritexmonitoring.repository.WaterLevelGaugeRepository;
import com.example.appgidritexmonitoring.util.CommonUtils;
import com.example.appgidritexmonitoring.util.DeviceResultConverter;
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

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class WaterLevelGaugeServiceImpl implements WaterLevelGaugeService {

    @Value("${app.admin.secret-key}")
    private String secretKey;

    @Value("${app.admin.reservoir}")
    private String reservoirName;

    private final WaterLevelGaugeMeasurementRepository waterLevelGaugeMeasurementRepository;
    private final HydrologicalStationMeasurementRepository hydrologicalStationMeasurementRepository;
    private final WaterLevelGaugeRepository waterLevelGaugeRepository;
    private final ReservoirRepository reservoirRepository;


    @Override
    public ApiResult<List<WaterLevelGaugeLatestDataDTO>> getLatestMeasurementOfReservoir(UUID reservoirId) {
        getReservoirById(reservoirId);
        List<WaterLevelGaugeMeasurement> latestMeasurementsOfReservoir =
                waterLevelGaugeMeasurementRepository.getLatestMeasurementsOfReservoir(reservoirId);

        List<WaterLevelGaugeLatestDataDTO> measurementDTOS = latestMeasurementsOfReservoir
                .stream()
                .map(this::mapLatestDataDTOFromWaterLevelGaugeMeasurement)
                .toList();

        //GET DIFFERENCES
        var latestDiffMeasurementOfReservoir = waterLevelGaugeMeasurementRepository.getLatestDiffMeasurementOfReservoir(reservoirId);

        //GET VOLUMES
        var latestVolumesByWaterLevelGauges = waterLevelGaugeMeasurementRepository.getLatestVolumesByWaterLevelGauges(reservoirId);

        for (WaterLevelGaugeLatestDataDTO waterLevelGaugeLatestDataDTO : measurementDTOS) {
            WaterLevelGaugeDTO waterLevelGauge = waterLevelGaugeLatestDataDTO.getWaterLevelGauge();
            WaterLevelGaugeMeasurementDiffProjection waterLevelDiffProjection = latestDiffMeasurementOfReservoir
                    .stream()
                    .filter(projection -> Objects.equals(waterLevelGauge.getId(), UUID.fromString(projection.getWater_level_gauge_id())))
                    .findFirst().orElseThrow();

            WaterLevelGaugeVolumeProj waterLevelGaugeVolumeProj = latestVolumesByWaterLevelGauges
                    .stream()
                    .filter(projection ->
                            Objects.equals(waterLevelGauge.getId(), UUID.fromString(projection.getWater_level_gauge_id())))
                    .findFirst().orElseThrow();

            waterLevelGaugeLatestDataDTO.setPressureDiff(waterLevelDiffProjection.getPressure_diff());
            waterLevelGaugeLatestDataDTO.setVolumeDate(waterLevelGaugeVolumeProj.getDate());
            waterLevelGaugeLatestDataDTO.setVolumeInSecond(waterLevelGaugeVolumeProj.getVolume_in_second());
        }

        return ApiResult.successResponse(measurementDTOS);
    }


    @Override
    public ApiResult<List<WaterLevelGaugesDataByDateDTO>> getMeasurementsByDatesOfReservoir(UUID reservoirId,
                                                                                            String startDate,
                                                                                            String endDate) {
        getReservoirById(reservoirId);

        List<LocalDateTime> localDateTimes = CommonUtils.parseLocalDateTimesFromStingsStandart(startDate, endDate);
        LocalDateTime startLocalDateTime = localDateTimes.get(0);
        LocalDateTime endLocalDateTime = localDateTimes.get(1);

        List<WaterLevelGaugesDataByDateDTO> result;
        List<WaterLevelGaugeMeasurement> measurementsOfReservoir = waterLevelGaugeMeasurementRepository.getAllMeasurementsByDates(reservoirId, startLocalDateTime, endLocalDateTime);

        result = mapByDateDTOFromMeasurements(measurementsOfReservoir);
        return ApiResult.successResponse(result);

    }

    @Override
    public ResponseEntity<Resource> downloadExcelOfMeasurementsOfReservoir(UUID reservoirId, String startDate, String endDate) {
        getReservoirById(reservoirId);
        List<LocalDateTime> localDateTimes = CommonUtils.parseLocalDateTimesFromStingsStandart(startDate, endDate);
        LocalDateTime startLocalDateTime = localDateTimes.get(0);
        LocalDateTime endLocalDateTime = localDateTimes.get(1);
        var measurementDTOs = getWaterLevelGaugeMeasurementDTOSFromMeasurements(reservoirId, startLocalDateTime, endLocalDateTime);

        Resource file = ExcelGenerator.generateFileForWaterLevelGauges(measurementDTOs);
        return Objects.nonNull(file) ? ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + "result.xlsx" + "\"")
                .body(file) : new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Override
    public ApiResult<?> getDifferenceMeasurementsBetweenDays(UUID reservoirId, String startDate, String endDate) {
        Reservoir reservoir = getReservoirById(reservoirId);
        List<LocalDateTime> localDateTimes = CommonUtils.parseLocalDateTimesFromStingsStandart(startDate, endDate);
        LocalDateTime startLocalDateTime = localDateTimes.get(0);
        LocalDateTime endLocalDateTime = localDateTimes.get(1);

        List<WaterLevelGaugeMeasurementDiffProjection> differenceMeasurementsByDate =
                waterLevelGaugeMeasurementRepository.getDifferenceMeasurementsByDate(reservoirId, startLocalDateTime, endLocalDateTime);

        return ApiResult.successResponse(mapByDateDTOFromMeasurementProj(differenceMeasurementsByDate));
    }

    @Override
    public ApiResult<?> updateLocationPressure(Double locationPressure, UUID waterLevelGaugeId) {
        WaterLevelGauge waterLevelGauge = waterLevelGaugeRepository
                .findById(waterLevelGaugeId)
                .orElseThrow(() -> RestException.restThrow(MessageConstants.WATER_LEVEL_GAUGE_NOT_FOUND, HttpStatus.NOT_FOUND));
        waterLevelGauge.setLocationPressure(locationPressure);
        waterLevelGaugeRepository.save(waterLevelGauge);

        List<WaterLevelGaugeMeasurement> measurements = waterLevelGaugeMeasurementRepository.findAllByWaterLevelGaugeId(waterLevelGaugeId);
        measurements.forEach(this::updateMeasurementByLocationPressure);
        waterLevelGaugeMeasurementRepository.saveAll(measurements);
        return ApiResult.successResponse(MessageConstants.SUCCESSFULLY_CHANGED);
    }

    @Override
    public ApiResult<List<WaterLevelGaugeDTO>> getLocationPressuresOfReservoir(UUID reservoirId) {
        List<WaterLevelGauge> waterLevelGauges = waterLevelGaugeRepository.findAllByReservoirIdOrderByOrdinal(reservoirId);
        List<WaterLevelGaugeDTO> result = waterLevelGauges
                .stream()
                .map(this::mapMarkFromWaterLevelGauge)
                .toList();
        return ApiResult.successResponse(result);
    }

    @Override
    public ApiResult<WaterLevelGaugeMeasurementsDTO> getLatestMeasurementsBySecretKey(String secretKey) {
        if (!secretKey.equals(this.secretKey))
            throw RestException.restThrow(MessageConstants.SECRET_KEY_INVALID, HttpStatus.FORBIDDEN);
        Reservoir reservoir = getReservoirByName();

        List<WaterLevelGaugeMeasurement> latestMeasurementsOfReservoir =
                waterLevelGaugeMeasurementRepository.getLatestMeasurementsOfReservoir(reservoir.getId());

        List<WaterLevelGaugeMeasurementDTO> measurementDTOsOfUpper = latestMeasurementsOfReservoir
                .stream()
                .map(this::mapMeasurementDTOFromWaterLevelGauge)
                .toList();

        HydrologicalStationMeasurementProj hydrologicalStationMeasurementProj = hydrologicalStationMeasurementRepository.getLatestWaterLevelOfSecondStation(reservoir.getId()).orElse(null);

        return ApiResult.successResponse(WaterLevelGaugeMeasurementsDTO.make(measurementDTOsOfUpper, hydrologicalStationMeasurementProj));
    }

    @Override
    public ApiResult<List<WaterLevelGaugeMeasurementDTO>> getMeasurementsOfLastWlgByReservoir(UUID reservoirId,
                                                                                              String startDate,
                                                                                              String endDate) {
        getReservoirById(reservoirId);
        List<LocalDateTime> localDateTimes = CommonUtils.parseLocalDateTimesFromStingsStandart(startDate, endDate);
        LocalDateTime startLocalDateTime = localDateTimes.get(0);
        LocalDateTime endLocalDateTime = localDateTimes.get(1);

        List<WaterLevelGaugeMeasurement> measurements = waterLevelGaugeMeasurementRepository.getMeasurementsOfLastWlgByReservoirId(reservoirId, startLocalDateTime, endLocalDateTime);
        List<WaterLevelGaugeMeasurementDTO> result = measurements.stream().map(this::mapMeasurementDTOFromWaterLevelGauge).toList();

        return ApiResult.successResponse(result);
    }

    private void updateMeasurementByLocationPressure(WaterLevelGaugeMeasurement measurement){
        CalculatedRes calculatedRes = DeviceResultConverter.calculatePressureResult(measurement.getWaterLevelGauge(), measurement.getReadPressure());
        measurement.setComputedPressure(calculatedRes.getFinalRes());
    }

    private List<WaterLevelGaugeMeasurementDTO> getWaterLevelGaugeMeasurementDTOSFromMeasurements(UUID reservoirId, LocalDateTime startDate, LocalDateTime endDate) {
        var measurements = waterLevelGaugeMeasurementRepository.getAllByReservoir(reservoirId, startDate, endDate);
        return measurements
                .stream()
                .map(this::mapMeasurementDTOFromWaterLevelGauge)
                .toList();
    }


    private Reservoir getReservoirById(UUID reservoirId) {
        return reservoirRepository.findById(reservoirId).orElseThrow(() -> RestException.restThrow(MessageConstants.RESERVOIR_NOT_FOUND, HttpStatus.BAD_REQUEST));
    }


    //mapper for list of WaterLevelGaugeMeasurement
    private List<WaterLevelGaugesDataByDateDTO> mapByDateDTOFromMeasurements(List<WaterLevelGaugeMeasurement> measurements) {
        List<WaterLevelGaugesDataByDateDTO> result = new ArrayList<>();

        LocalDateTime localDateTime = null;
        WaterLevelGaugesDataByDateDTO dataByDateDTO;
        List<WaterLevelGaugeMeasurementDTO> indications = new ArrayList<>();

        for (WaterLevelGaugeMeasurement measurement : measurements) {
            if (!measurement.getDate().equals(localDateTime)) {
                indications = new ArrayList<>();
                dataByDateDTO = WaterLevelGaugesDataByDateDTO.builder()
                        .date(measurement.getDate())
                        .indications(indications)
                        .build();
                result.add(dataByDateDTO);
            }

            indications.add(mapMeasurementDTOFromWaterLevelGauge(measurement));
            localDateTime = measurement.getDate();
        }

        return result;
    }

    //mapper for list of WaterLevelGaugeMeasurementDiffProjection
    private List<WaterLevelGaugesDataByDateDTO> mapByDateDTOFromMeasurementProj(List<WaterLevelGaugeMeasurementDiffProjection> measurements) {
        List<WaterLevelGaugesDataByDateDTO> result = new ArrayList<>();

        LocalDateTime localDateTime = null;
        WaterLevelGaugesDataByDateDTO dataByDateDTO;
        List<WaterLevelGaugeMeasurementDTO> indications = new ArrayList<>();

        for (WaterLevelGaugeMeasurementDiffProjection proj : measurements) {
            if (!proj.getDate().equals(localDateTime)) {
                indications = new ArrayList<>();
                dataByDateDTO = WaterLevelGaugesDataByDateDTO.builder()
                        .date(proj.getDate())
                        .indications(indications)
                        .build();
                result.add(dataByDateDTO);
            }

            indications.add(mapMeasurementDTOFromProjection(proj));
            localDateTime = proj.getDate();
        }

        return result;
    }

    private Reservoir getReservoirByName() {
        List<Reservoir> reservoirs = reservoirRepository.findAllByOrderByNameDesc();
        if (reservoirName.equals("xisorak"))
            return reservoirs.get(0);
        return reservoirs.get(1);
    }

    private WaterLevelGaugeLatestDataDTO mapLatestDataDTOFromWaterLevelGaugeMeasurement(WaterLevelGaugeMeasurement measurement) {
        return WaterLevelGaugeLatestDataDTO.builder()
                .waterLevelGauge(mapMarkFromWaterLevelGauge(measurement.getWaterLevelGauge()))
                .pressureDate(measurement.getDate())
                .temperature(measurement.getTempInDegrees())
                .hydroPressure(measurement.getComputedPressure())
                .volumeValue(measurement.getVolumeValue())
                .virtualPressure(measurement.getVirtualPressure())
                .build();
    }

    private WaterLevelGaugeMeasurementDTO mapMeasurementDTOFromProjection(WaterLevelGaugeMeasurementDiffProjection proj){
        return WaterLevelGaugeMeasurementDTO.builder()
                .waterLevelGauge(mapWaterLevelGaugeFromProj(proj))
                .virtualPressure(proj.getVirtual_pressure())
                .pressureDiff(proj.getPressure_diff())
                .build();
    }

    private WaterLevelGaugeMeasurementDTO mapMeasurementDTOFromWaterLevelGauge(WaterLevelGaugeMeasurement measurement) {
        return WaterLevelGaugeMeasurementDTO.builder()
                .waterLevelGauge(mapMarkFromWaterLevelGauge(measurement.getWaterLevelGauge()))
                .date(measurement.getDate())
                .hydroPressure(measurement.getComputedPressure())
                .virtualPressure(measurement.getVirtualPressure())
                .temperature(measurement.getTempInDegrees())
                .volumeValue(measurement.getVolumeValue())
                .build();
    }

    private WaterLevelGaugeDTO mapMarkFromWaterLevelGauge(WaterLevelGauge waterLevelGauge) {
        return WaterLevelGaugeDTO.builder()
                .id(waterLevelGauge.getId())
                .locationPressure(waterLevelGauge.getLocationPressure())
                .ordinal(waterLevelGauge.getOrdinal())
                .build();
    }

    private WaterLevelGaugeDTO mapWaterLevelGaugeFromProj(WaterLevelGaugeMeasurementDiffProjection proj) {
        return WaterLevelGaugeDTO.builder()
                .id(UUID.fromString(proj.getWater_level_gauge_id()))
                .ordinal(proj.getOrdinal())
                .locationPressure(proj.getLocation_pressure())
                .build();
    }



}
