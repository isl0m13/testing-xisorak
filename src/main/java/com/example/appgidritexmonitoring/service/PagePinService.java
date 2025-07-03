package com.example.appgidritexmonitoring.service;


import com.example.appgidritexmonitoring.payload.ApiResult;
import com.example.appgidritexmonitoring.payload.PagePinAddDTO;
import com.example.appgidritexmonitoring.payload.PagePinDTO;

import java.util.UUID;

public interface PagePinService {

    ApiResult<PagePinDTO> add(PagePinAddDTO pagePinAddDTO);

    ApiResult<PagePinDTO> changeActive(UUID id);

    ApiResult<?> delete(UUID id);
}
