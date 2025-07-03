package com.example.appgidritexmonitoring.controller;


import com.example.appgidritexmonitoring.payload.ApiResult;
import com.example.appgidritexmonitoring.payload.SignDTO;
import com.example.appgidritexmonitoring.payload.TokenDTO;
import com.example.appgidritexmonitoring.util.RestConstants;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequestMapping(AuthController.AUTH_CONTROLLER_BASE_PATH)
public interface AuthController {
    String AUTH_CONTROLLER_BASE_PATH = RestConstants.BASE_PATH + "/auth";
    String SIGN_IN_PATH = "/sign-in";
    String REFRESH_TOKEN_PATH = "/refresh-token";

    @PostMapping(SIGN_IN_PATH)
    ApiResult<TokenDTO> signIn(@Valid @RequestBody SignDTO signDTO);

    @GetMapping(REFRESH_TOKEN_PATH)
    ApiResult<TokenDTO> refreshToken(@RequestHeader("Authorization") String accessToken,
                                     @RequestHeader("RefreshToken") String refreshToken);




}
