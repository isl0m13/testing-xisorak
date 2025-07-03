package com.example.appgidritexmonitoring.controller;

import com.example.appgidritexmonitoring.payload.*;
import com.example.appgidritexmonitoring.util.RestConstants;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RequestMapping(UserController.USER_CONTROLLER_BASE_PATH)
public interface UserController {
    String USER_CONTROLLER_BASE_PATH = RestConstants.BASE_PATH + "/users";
    String USER_EDIT_PATH = "/{id}";
    String USER_DELETE_PATH = "/{id}";
    String USER_GET_BY_ID_PATH = "/{id}";
    String USER_ME_PATH = "/me";


    @PostMapping
    ApiResult<UserDTO> add(@Valid @RequestBody UserAddDTO userAddDTO);

    @PutMapping(USER_EDIT_PATH)
    ApiResult<UserDTO> edit(@Valid @RequestBody UserAddDTO userAddDTO, @PathVariable UUID id);

    @DeleteMapping(USER_DELETE_PATH)
    ApiResult<UserDTO> delete(@PathVariable UUID id);

    @GetMapping(USER_GET_BY_ID_PATH)
    ApiResult<UserDTO> get(@PathVariable UUID id);

    @GetMapping(USER_ME_PATH)
    ApiResult<UserDTODetails> getUserByToken();

    @GetMapping
    ApiResult<List<UserDTO>> getAll();


}
