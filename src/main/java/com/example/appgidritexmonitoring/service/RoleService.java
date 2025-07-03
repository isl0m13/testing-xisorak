package com.example.appgidritexmonitoring.service;


import com.example.appgidritexmonitoring.payload.ApiResult;
import com.example.appgidritexmonitoring.payload.RoleDTO;

public interface RoleService {

    ApiResult<RoleDTO> get(Integer id);
}
