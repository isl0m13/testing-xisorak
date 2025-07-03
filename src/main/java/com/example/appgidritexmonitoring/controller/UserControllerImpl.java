package com.example.appgidritexmonitoring.controller;

import com.example.appgidritexmonitoring.payload.*;
import com.example.appgidritexmonitoring.service.AuthService;
import com.example.appgidritexmonitoring.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UserControllerImpl implements UserController {

    private final UserService userService;
    private final AuthService authService;

    @Override
    public ApiResult<UserDTO> add(UserAddDTO userAddDTO) {
        return userService.add(userAddDTO);
    }

    @Override
    public ApiResult<UserDTO> edit(UserAddDTO userAddDTO, UUID id) {
        return userService.edit(userAddDTO, id);
    }

    @Override
    public ApiResult<UserDTO> delete(UUID id) {
        return userService.delete(id);
    }

    @Override
    public ApiResult<UserDTO> get(UUID id) {
        return userService.get(id);
    }

    @Override
    public ApiResult<UserDTODetails> getUserByToken() {
        return authService.getUserByToken();
    }

    @Override
    public ApiResult<List<UserDTO>> getAll() {
        return userService.getAll();
    }
}
