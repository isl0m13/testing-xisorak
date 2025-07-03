package com.example.appgidritexmonitoring.controller;

import com.example.appgidritexmonitoring.payload.ApiResult;
import com.example.appgidritexmonitoring.payload.PagePinAddDTO;
import com.example.appgidritexmonitoring.payload.PagePinDTO;
import com.example.appgidritexmonitoring.util.RestConstants;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RequestMapping(PagePinController.PAGE_PIN_CONTROLLER_BASE_PATH)
public interface PagePinController {
    String PAGE_PIN_CONTROLLER_BASE_PATH = RestConstants.BASE_PATH + "/pages-pin";
    String DELETE_PATH = "/{id}";
    String CHANGE_ACTIVE_PAGE_PIN = "/{id}";

    @PostMapping
    ApiResult<PagePinDTO> add(@RequestBody @Valid PagePinAddDTO pagePinAddDTO);

    @PutMapping(CHANGE_ACTIVE_PAGE_PIN)
    ApiResult<PagePinDTO> changeActive(@PathVariable UUID id);

    @DeleteMapping(DELETE_PATH)
    ApiResult<?> delete(@PathVariable UUID id);



}
