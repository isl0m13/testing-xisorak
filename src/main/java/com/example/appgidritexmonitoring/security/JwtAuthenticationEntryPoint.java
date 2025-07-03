package com.example.appgidritexmonitoring.security;

import com.example.appgidritexmonitoring.payload.ApiResult;
import com.example.appgidritexmonitoring.payload.ErrorData;
import com.example.appgidritexmonitoring.util.RestConstants;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        ApiResult<ErrorData> errorDataApiResult = ApiResult.errorResponse("Forbidden", 403);
        response.getWriter().write(RestConstants.OBJECT_MAPPER.writeValueAsString(errorDataApiResult));
        response.setStatus(403);
        response.setContentType("application/json");
    }
}
