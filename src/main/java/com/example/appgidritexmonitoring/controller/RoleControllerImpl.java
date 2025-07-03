package com.example.appgidritexmonitoring.controller;

import com.example.appgidritexmonitoring.payload.ApiResult;
import com.example.appgidritexmonitoring.payload.RoleDTO;
import com.example.appgidritexmonitoring.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RoleControllerImpl implements RoleController{

    private final RoleService roleService;

    @Override
    public ApiResult<RoleDTO> get(Integer id) {
        return roleService.get(id);
    }


}

