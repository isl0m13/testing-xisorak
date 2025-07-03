package com.example.appgidritexmonitoring.controller;

import com.example.appgidritexmonitoring.payload.ApiResult;
import com.example.appgidritexmonitoring.service.SafetyCriteriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class SafetyCriteriaControllerImpl implements SafetyCriteriaController{

    private final SafetyCriteriaService safetyCriteriaService;

    @Override
    public ApiResult<?> getValuesOfReservoir(UUID reservoirId) {
        return safetyCriteriaService.getValuesOfReservoir(reservoirId);
    }

    @Override
    public ApiResult<?> getSafetyCriteriaStatutesOfReservoir(UUID reservoirId) {
        return safetyCriteriaService.getSafetyCriteriaStatutesOfReservoir(reservoirId);
    }

    @Override
    public ApiResult<?> updateStatus(UUID safetyCriteriaId, Integer newStatus) {
        return safetyCriteriaService.updateStatus(safetyCriteriaId, newStatus);
    }





}
