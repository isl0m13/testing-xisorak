package com.example.appgidritexmonitoring.controller;

import com.example.appgidritexmonitoring.payload.ApiResult;
import com.example.appgidritexmonitoring.util.RestConstants;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.UUID;

@RequestMapping(SafetyCriteriaController.SAFETY_CRITERIA_CONTROLLER_BASE_URL)
@Validated
public interface SafetyCriteriaController {
    String SAFETY_CRITERIA_CONTROLLER_BASE_URL = RestConstants.BASE_PATH + "/safety-criteria";
    String VALUES_OF_RESERVOIR = "/reservoirs/{reservoirId}/values";
    String UPDATE_STATUS_BY_ID =  "/status/{safetyCriteriaId}";
    String STATUSES_OF_RESERVOIR = "/reservoirs/{reservoirId}/all";


    @GetMapping(VALUES_OF_RESERVOIR)
    ApiResult<?> getValuesOfReservoir(@PathVariable UUID reservoirId);

    @GetMapping(STATUSES_OF_RESERVOIR)
    ApiResult<?> getSafetyCriteriaStatutesOfReservoir(@PathVariable UUID reservoirId);

    @PutMapping(UPDATE_STATUS_BY_ID)
    ApiResult<?> updateStatus(@PathVariable UUID safetyCriteriaId,
                              @RequestParam("status") @Min(1) @Max(3) Integer newStatus);




}
