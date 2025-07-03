package com.example.appgidritexmonitoring.service;

import com.example.appgidritexmonitoring.entity.DamBodyDevice;
import com.example.appgidritexmonitoring.entity.DamBodyDeviceMeasurement;
import com.example.appgidritexmonitoring.entity.enums.DamBodyDeviceTypeEnum;
import com.example.appgidritexmonitoring.exceptions.RestException;
import com.example.appgidritexmonitoring.payload.ApiResult;
import com.example.appgidritexmonitoring.payload.GateDTO;
import com.example.appgidritexmonitoring.payload.dambodydevice.DamBodyDeviceMeasurementDTO;
import com.example.appgidritexmonitoring.payload.dambodydevice.DamBodyDeviceMeasurementsByGateDTO;
import com.example.appgidritexmonitoring.repository.DamBodyDeviceMeasurementRepository;
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
public class DamBodyDeviceServiceImpl implements DamBodyDeviceService {

    private final DamBodyDeviceMeasurementRepository damBodyDeviceMeasurementRepository;
    private final GateService gateService;

    @Override
    public ApiResult<List<DamBodyDeviceMeasurementsByGateDTO>> getLatestMeasurementsByGateOfReservoir(UUID reservoirId) {
        List<GateDTO> gates = gateService.getAllOfReservoir(reservoirId).getData();
        var latestMeasurementsOfReservoir = damBodyDeviceMeasurementRepository.getLatestMeasurementsOfReservoir(reservoirId);
        List<DamBodyDeviceMeasurementsByGateDTO> result = new ArrayList<>();
        for (GateDTO gate : gates) {
            List<DamBodyDeviceMeasurementDTO> indications = latestMeasurementsOfReservoir
                    .stream()
                    .filter(measurement -> measurement.getDamBodyDevice().getGate().getId().equals(gate.getId()))
                    .map(this::mapDTOFromDamBodyDeviceMeasurement)
                    .toList();
            DamBodyDeviceMeasurementsByGateDTO damBodyDeviceMeasurementsByGateDTO = DamBodyDeviceMeasurementsByGateDTO.builder()
                    .gate(gate)
                    .indications(indications)
                    .build();
            result.add(damBodyDeviceMeasurementsByGateDTO);
        }
        return ApiResult.successResponse(result);
    }

    @Override
    public ApiResult<List<DamBodyDeviceMeasurementDTO>> getLatestMeasurementsOfGate(UUID gateId) {
        GateDTO gate = gateService.getGateById(gateId).getData();
        var latestMeasurements = damBodyDeviceMeasurementRepository.getLatestMeasurementsOfGate(gateId);
        List<DamBodyDeviceMeasurementDTO> result = latestMeasurements
                .stream()
                .map(this::mapDTOFromDamBodyDeviceMeasurement)
                .toList();
        return ApiResult.successResponse(result);
    }

    @Override
    public ApiResult<List<DamBodyDeviceMeasurementDTO>> getMeasurementsOfDeviceType(UUID gateId, String deviceType, String startDate, String endDate) {
        GateDTO gate = gateService.getGateById(gateId).getData();
        DamBodyDeviceTypeEnum deviceTypeEnum = mapStrToDamBodyDeviceTypeEnum(deviceType);
        if (Objects.isNull(deviceTypeEnum))
            throw RestException.restThrow(MessageConstants.INVALID_DEVICE_TYPE_FORMAT, HttpStatus.BAD_REQUEST);
        List<LocalDateTime> localDateTimes = CommonUtils.parseLocalDateTimesFromStingsStandart(startDate, endDate);
        LocalDateTime startDateTime = localDateTimes.get(0);
        LocalDateTime endDateTime = localDateTimes.get(1);
        var measurementsOfDeviceType = damBodyDeviceMeasurementRepository.getMeasurementsOfDeviceType(gate.getId(), deviceTypeEnum.toString(), startDateTime, endDateTime);
        List<DamBodyDeviceMeasurementDTO> result = measurementsOfDeviceType
                .stream()
                .map(this::mapDTOFromDamBodyDeviceMeasurement)
                .toList();
        return ApiResult.successResponse(result);
    }

    private DamBodyDeviceMeasurementDTO mapDTOFromDamBodyDeviceMeasurement(DamBodyDeviceMeasurement measurement) {
        DamBodyDevice damBodyDevice = measurement.getDamBodyDevice();
        return DamBodyDeviceMeasurementDTO.builder()
                .date(measurement.getDate())
                .deviceName(damBodyDevice.getName())
                .indication(measurement.getComputedValue())
                .readValue(measurement.getReadValue())
                .deviceType(damBodyDevice.getType())
                .build();
    }

    private DamBodyDeviceTypeEnum mapStrToDamBodyDeviceTypeEnum(String str) {
        switch (str) {
            case "pds" -> {
                return DamBodyDeviceTypeEnum.PDS;
            }
            case "pts" -> {
                return DamBodyDeviceTypeEnum.PTS;
            }
            case "pngs" -> {
                return DamBodyDeviceTypeEnum.PNGS;
            }
            case "plps" -> {
                return DamBodyDeviceTypeEnum.PLPS;
            }
            default -> {
                return null;
            }
        }
    }

}
