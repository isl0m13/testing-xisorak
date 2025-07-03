package com.example.appgidritexmonitoring.controller;

import com.example.appgidritexmonitoring.payload.ApiResult;
import com.example.appgidritexmonitoring.payload.ReservoirDTO;
import com.example.appgidritexmonitoring.util.RestConstants;
import org.springframework.security.access.method.P;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RequestMapping(ReservoirController.RESERVOIR_CONTROLLER_BASE_PATH)
public interface ReservoirController {
    String RESERVOIR_CONTROLLER_BASE_PATH = RestConstants.BASE_PATH + "/reservoirs";
    String BY_ID = "/{id}";


    @GetMapping()
    ApiResult<List<ReservoirDTO>> getAllReservoirs();

    @GetMapping(BY_ID)
    ApiResult<ReservoirDTO> getReservoirById(@PathVariable("id") UUID id);



}
