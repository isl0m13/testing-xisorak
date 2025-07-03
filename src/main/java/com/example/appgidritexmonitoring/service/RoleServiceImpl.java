package com.example.appgidritexmonitoring.service;

import com.example.appgidritexmonitoring.entity.Role;
import com.example.appgidritexmonitoring.exceptions.RestException;
import com.example.appgidritexmonitoring.payload.ApiResult;
import com.example.appgidritexmonitoring.payload.RoleDTO;
import com.example.appgidritexmonitoring.repository.RoleRepository;
import com.example.appgidritexmonitoring.util.MessageConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService{

    private final RoleRepository roleRepository;

    @Override
    public ApiResult<RoleDTO> get(Integer id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RestException(MessageConstants.ROLE_NOT_FOUND, HttpStatus.BAD_REQUEST));
           return ApiResult.successResponse(mapRoleToRoleDTO(role));
    }






    private RoleDTO mapRoleToRoleDTO(Role role){
        return RoleDTO.builder()
                .name(role.getName())
                .description(role.getDescription())
                .build();
    }



}
