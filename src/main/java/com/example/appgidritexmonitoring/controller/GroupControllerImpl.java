package com.example.appgidritexmonitoring.controller;

import com.example.appgidritexmonitoring.payload.ApiResult;
import com.example.appgidritexmonitoring.payload.GroupAddDTO;
import com.example.appgidritexmonitoring.payload.GroupDTO;
import com.example.appgidritexmonitoring.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GroupControllerImpl implements GroupController{

    private final GroupService groupService;

    @Override
    public ApiResult<GroupDTO> add(UUID reservoirId) {
        return groupService.add(reservoirId);
    }

    @Override
    public ApiResult<GroupDTO> edit(UUID groupId, GroupAddDTO groupAddDTO) {
        return groupService.edit(groupId, groupAddDTO);
    }

    @Override
    public ApiResult<?> delete(UUID groupId) {
        return groupService.delete(groupId);
    }


}
