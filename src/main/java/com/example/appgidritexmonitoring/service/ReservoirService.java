package com.example.appgidritexmonitoring.service;

import com.example.appgidritexmonitoring.payload.ApiResult;
import com.example.appgidritexmonitoring.payload.ReservoirDTO;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface ReservoirService {
    ApiResult<List<ReservoirDTO>> getAllReservoirs();

    ApiResult<ReservoirDTO> getReservoirById(UUID id);


}
