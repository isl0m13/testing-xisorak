package com.example.appgidritexmonitoring.service;

import com.example.appgidritexmonitoring.entity.User;
import com.example.appgidritexmonitoring.payload.ApiResult;
import com.example.appgidritexmonitoring.payload.SignDTO;
import com.example.appgidritexmonitoring.payload.TokenDTO;
import com.example.appgidritexmonitoring.payload.UserDTODetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.UUID;

public interface AuthService extends UserDetailsService {
    ApiResult<TokenDTO> signIn(SignDTO signDTO);

    ApiResult<TokenDTO> refreshToken(String accessToken, String refreshToken);

    User getUserByIdOrThrow(UUID id);

    ApiResult<UserDTODetails> getUserByToken();

}
