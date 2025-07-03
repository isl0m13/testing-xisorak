package com.example.appgidritexmonitoring.service;

import com.example.appgidritexmonitoring.payload.ApiResult;
import com.example.appgidritexmonitoring.payload.GroupAddDTO;
import com.example.appgidritexmonitoring.payload.GroupDTO;

import java.util.UUID;

public interface GroupService {

    ApiResult<GroupDTO> add(UUID reservoirId);


    ApiResult<GroupDTO> edit(UUID groupId, GroupAddDTO groupAddDTO);


    ApiResult<?> delete(UUID groupId);




}
