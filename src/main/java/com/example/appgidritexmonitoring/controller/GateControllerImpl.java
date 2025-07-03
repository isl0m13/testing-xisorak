package com.example.appgidritexmonitoring.controller;

import com.example.appgidritexmonitoring.payload.ApiResult;
import com.example.appgidritexmonitoring.payload.GateDTO;
import com.example.appgidritexmonitoring.service.GateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GateControllerImpl implements GateController{
    private final GateService gateService;

    @Override
    public ApiResult<List<GateDTO>> getAllGatesOfReservoir(UUID reservoirId) {
        return gateService.getAllOfReservoir(reservoirId);
    }

    @Override
    public ApiResult<GateDTO> getGateById(UUID id) {
        return gateService.getGateById(id);
    }


}
