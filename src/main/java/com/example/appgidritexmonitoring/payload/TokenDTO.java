package com.example.appgidritexmonitoring.payload;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TokenDTO {

    private String accessToken;

    private String refreshToken;

    private final String tokenType = "Bearer ";

}
