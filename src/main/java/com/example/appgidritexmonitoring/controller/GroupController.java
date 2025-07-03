package com.example.appgidritexmonitoring.controller;

import com.example.appgidritexmonitoring.payload.ApiResult;
import com.example.appgidritexmonitoring.payload.GroupAddDTO;
import com.example.appgidritexmonitoring.payload.GroupDTO;
import com.example.appgidritexmonitoring.util.RestConstants;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping(GroupController.GROUP_CONTROLLER_BASE_PATH)
public interface GroupController {
    String GROUP_CONTROLLER_BASE_PATH = RestConstants.BASE_PATH + "/groups";
    String ADD = "/reservoir/{reservoirId}";
    String EDIT = "/{groupId}";
    String DELETE = "/{groupId}";


    @PostMapping(path = ADD)
    ApiResult<GroupDTO> add(@PathVariable UUID reservoirId);

    @PutMapping(path = EDIT)
    ApiResult<GroupDTO> edit(@PathVariable UUID groupId,
                             @RequestBody GroupAddDTO groupAddDTO);

    @DeleteMapping(path = DELETE)
    ApiResult<?> delete(@PathVariable UUID groupId);




}
