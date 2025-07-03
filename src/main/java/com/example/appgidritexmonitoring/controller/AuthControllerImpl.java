package com.example.appgidritexmonitoring.controller;

import com.example.appgidritexmonitoring.payload.ApiResult;
import com.example.appgidritexmonitoring.payload.SignDTO;
import com.example.appgidritexmonitoring.payload.TokenDTO;
import com.example.appgidritexmonitoring.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthControllerImpl implements AuthController{

    private final AuthService authService;

    @Override
    public ApiResult<TokenDTO> signIn(SignDTO signDTO) {
        return authService.signIn(signDTO);
    }

    @Override
    public ApiResult<TokenDTO> refreshToken(String accessToken, String refreshToken) {
        return authService.refreshToken(accessToken, refreshToken);
    }

}
