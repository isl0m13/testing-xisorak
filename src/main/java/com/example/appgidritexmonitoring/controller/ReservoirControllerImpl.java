package com.example.appgidritexmonitoring.controller;

import com.example.appgidritexmonitoring.payload.ApiResult;
import com.example.appgidritexmonitoring.payload.ReservoirDTO;
import com.example.appgidritexmonitoring.service.ReservoirService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ReservoirControllerImpl implements ReservoirController {
    private final ReservoirService reservoirService;

    @Override
    public ApiResult<List<ReservoirDTO>> getAllReservoirs() {
        return reservoirService.getAllReservoirs();
    }

    @Override
    public ApiResult<ReservoirDTO> getReservoirById(UUID id) {
        return reservoirService.getReservoirById(id);
    }
}
