package com.example.appgidritexmonitoring.service;

import com.example.appgidritexmonitoring.payload.*;

import java.util.List;
import java.util.UUID;

public interface UserService {

    ApiResult<UserDTO> add(UserAddDTO userAddDTO);


    ApiResult<UserDTO> edit(UserAddDTO userAddDTO, UUID id);

    ApiResult<UserDTO> delete(UUID id);

    ApiResult<UserDTO> get(UUID id);

    ApiResult<List<UserDTO>> getAll();


}
