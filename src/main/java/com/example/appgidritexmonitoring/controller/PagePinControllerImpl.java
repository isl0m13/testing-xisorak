package com.example.appgidritexmonitoring.controller;

import com.example.appgidritexmonitoring.payload.ApiResult;
import com.example.appgidritexmonitoring.payload.PagePinAddDTO;
import com.example.appgidritexmonitoring.payload.PagePinDTO;
import com.example.appgidritexmonitoring.service.PagePinService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class PagePinControllerImpl implements PagePinController{
    private final PagePinService pagePinService;

    @Override
    public ApiResult<PagePinDTO> add(PagePinAddDTO pagePinAddDTO) {
        return pagePinService.add(pagePinAddDTO);
    }

    @Override
    public ApiResult<PagePinDTO> changeActive(UUID id) {
        return pagePinService.changeActive(id);
    }

    @Override
    public ApiResult<?> delete(UUID id) {
        return pagePinService.delete(id);
    }


}
