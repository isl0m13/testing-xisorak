package com.example.appgidritexmonitoring.service;

import com.example.appgidritexmonitoring.entity.Reservoir;
import com.example.appgidritexmonitoring.exceptions.RestException;
import com.example.appgidritexmonitoring.payload.ApiResult;
import com.example.appgidritexmonitoring.payload.ReservoirDTO;
import com.example.appgidritexmonitoring.repository.ReservoirRepository;
import com.example.appgidritexmonitoring.util.MessageConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservoirServiceImpl implements ReservoirService {
    private final ReservoirRepository reservoirRepository;

    @Override
    public ApiResult<List<ReservoirDTO>> getAllReservoirs() {
        List<Reservoir> reservoirs = reservoirRepository.findAll();
        if (reservoirs.isEmpty())
            throw RestException.restThrow(MessageConstants.RESERVOIRS_NOT_FOUND, HttpStatus.NOT_FOUND);
        List<ReservoirDTO> reservoirDTOS = reservoirs.stream().map(this::mapReservoirDTOFromReservoir).toList();
        return ApiResult.successResponse(reservoirDTOS);
    }

    @Override
    public ApiResult<ReservoirDTO> getReservoirById(UUID reservoirId) {
        Reservoir reservoir = findReservoirById(reservoirId);
        ReservoirDTO reservoirDTO = mapReservoirDTOFromReservoir(reservoir);
        return ApiResult.successResponse(reservoirDTO);
    }

    public Reservoir findReservoirById(UUID reservoirId) {
        return reservoirRepository
                .findById(reservoirId)
                .orElseThrow(() -> RestException.restThrow(MessageConstants.RESERVOIR_NOT_FOUND, HttpStatus.NOT_FOUND));
    }

    private ReservoirDTO mapReservoirDTOFromReservoir(Reservoir reservoir) {
        return ReservoirDTO.builder()
                .id(reservoir.getId())
                .name(reservoir.getName())
                .location(reservoir.getLocation())
                .build();
    }


}
