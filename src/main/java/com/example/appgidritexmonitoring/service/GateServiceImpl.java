package com.example.appgidritexmonitoring.service;

import com.example.appgidritexmonitoring.entity.Gate;
import com.example.appgidritexmonitoring.exceptions.RestException;
import com.example.appgidritexmonitoring.payload.ApiResult;
import com.example.appgidritexmonitoring.payload.GateDTO;
import com.example.appgidritexmonitoring.repository.GateRepository;
import com.example.appgidritexmonitoring.repository.ReservoirRepository;
import com.example.appgidritexmonitoring.util.MessageConstants;
import com.example.appgidritexmonitoring.util.RestConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GateServiceImpl implements GateService{
    private final GateRepository gateRepository;
    private final ReservoirRepository reservoirRepository;

    @Override
    public ApiResult<List<GateDTO>> getAllOfReservoir(UUID reservoirId) {
        reservoirRepository.findById(reservoirId).orElseThrow(() -> RestException.restThrow(MessageConstants.RESERVOIR_NOT_FOUND, HttpStatus.NOT_FOUND));
        List<Gate> gates = gateRepository.findAllByReservoirIdOrderByOrdinal(reservoirId);
        if (gates.isEmpty())
            throw RestException.restThrow(MessageConstants.GATES_NOT_FOUND, HttpStatus.NOT_FOUND);
        List<GateDTO> gateDTOS = gates.stream().map(this::mapGateDTOFromGate).toList();
        return ApiResult.successResponse(gateDTOS);
    }

    @Override
    public ApiResult<GateDTO> getGateById(UUID id) {
        Gate gate = gateRepository.findById(id)
                .orElseThrow(() -> RestException.restThrow(MessageConstants.GATE_NOT_FOUND, HttpStatus.NOT_FOUND));
        GateDTO gateDTO = mapGateDTOFromGate(gate);
        return ApiResult.successResponse(gateDTO);
    }

    private GateDTO mapGateDTOFromGate(Gate gate){
        GateDTO gateDTO = GateDTO.builder()
                .id(gate.getId())
                .ordinal(gate.getOrdinal())
                .build();

        if (Objects.nonNull(gate.getName()))
            gateDTO.setName(gate.getName());
        return gateDTO;
    };

}
