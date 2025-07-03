package com.example.appgidritexmonitoring.security;

import com.example.appgidritexmonitoring.entity.User;
import com.example.appgidritexmonitoring.service.AuthService;
import com.example.appgidritexmonitoring.util.RestConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthService authService;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, @Lazy AuthService authService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.authService = authService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            setUserPrincipalIfAllOk(request);
        }catch (Exception e){
            System.out.println("Error in JwtAuthenticationFilter setUserPrincipalIfAllOk method: " + e.getMessage());
        }
        filterChain.doFilter(request, response);
    }

    private void setUserPrincipalIfAllOk(HttpServletRequest request){
        String authorization = request.getHeader(RestConstants.AUTH_HEADER);
        if (authorization != null) {
            User user = getUserFromBearerToken(authorization);
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(user, user.getAuthorities(), null);
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
    }

    private User getUserFromBearerToken(String token){
        try {
            token = token.substring("Bearer".length()).trim();
            if (jwtTokenProvider.isValidToken(token, true)){
                String userId = jwtTokenProvider.getUserIdFromToken(token, true);
                return authService.getUserByIdOrThrow(UUID.fromString(userId));
            }
        }catch (Exception e){
            System.out.println("Error message in authentication: " + e.getMessage());
        }
        return null;
    }




}
