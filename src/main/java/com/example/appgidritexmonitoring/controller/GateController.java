package com.example.appgidritexmonitoring.controller;

import com.example.appgidritexmonitoring.payload.ApiResult;
import com.example.appgidritexmonitoring.payload.GateDTO;
import com.example.appgidritexmonitoring.repository.GateRepository;
import com.example.appgidritexmonitoring.repository.SensorRepository;
import com.example.appgidritexmonitoring.util.RestConstants;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RequestMapping(GateController.GATE_CONTROLLER_BASE_PATH)
public interface GateController {
    String GATE_CONTROLLER_BASE_PATH = RestConstants.BASE_PATH + "/gates";
    String ALL_GATES_OF_RESERVOIR = "/reservoirs/{reservoirId}";
    String BY_ID = "/{id}";

    @GetMapping(ALL_GATES_OF_RESERVOIR)
    ApiResult<List<GateDTO>> getAllGatesOfReservoir(@PathVariable UUID reservoirId);

    @GetMapping(BY_ID)
    ApiResult<GateDTO> getGateById(@PathVariable UUID id);



}
