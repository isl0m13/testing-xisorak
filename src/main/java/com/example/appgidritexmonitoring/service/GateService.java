package com.example.appgidritexmonitoring.service;

import com.example.appgidritexmonitoring.payload.ApiResult;
import com.example.appgidritexmonitoring.payload.GateDTO;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface GateService {
    ApiResult<List<GateDTO>> getAllOfReservoir(UUID reservoirId);

    ApiResult<GateDTO> getGateById(UUID id);
}
