package com.example.appgidritexmonitoring.controller;

import com.example.appgidritexmonitoring.payload.ApiResult;
import com.example.appgidritexmonitoring.payload.RoleDTO;
import com.example.appgidritexmonitoring.util.RestConstants;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(RoleController.ROLE_CONTROLLER_BASE_PATH)
public interface RoleController {
    String ROLE_CONTROLLER_BASE_PATH = RestConstants.BASE_PATH + "/roles";
    String ROLE_EDIT_PATH = "/{id}";
    String ROLE_DELETE_PATH = "/{id}";
    String ROLE_GET_BY_ID_PATH = "/{id}";

    @GetMapping(ROLE_GET_BY_ID_PATH)
    ApiResult<RoleDTO> get(@PathVariable Integer id);


}
