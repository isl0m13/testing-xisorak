package com.example.appgidritexmonitoring.config;

import com.example.appgidritexmonitoring.exceptions.ExceptionHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class MyAccessDeniedHandler implements AccessDeniedHandler {
    private final ExceptionHelper exceptionHelper;


    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        System.out.println("---".repeat(8));
        ResponseEntity<?> responseEntity = exceptionHelper.handleException(accessDeniedException);
        System.out.println(responseEntity);
    }


}
