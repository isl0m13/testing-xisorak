package com.example.appgidritexmonitoring.service;

import com.example.appgidritexmonitoring.payload.ApiResult;
import com.example.appgidritexmonitoring.payload.SafetyCriteriaDTO;

import java.util.List;
import java.util.UUID;

public interface SafetyCriteriaService {

    ApiResult<?> getValuesOfReservoir(UUID reservoirId);

    ApiResult<?> getSafetyCriteriaStatutesOfReservoir(UUID reservoir);

    ApiResult<?> updateStatus(UUID safetyCriteriaId,
                              Integer newStatus);
}
