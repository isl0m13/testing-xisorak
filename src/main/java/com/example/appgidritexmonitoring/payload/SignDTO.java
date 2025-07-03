package com.example.appgidritexmonitoring.payload;

import com.example.appgidritexmonitoring.util.MessageConstants;
import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignDTO {

    @NotBlank(message = MessageConstants.MUST_NOT_BE_BLANK_USERNAME)
    private String userName;

    @NotBlank(message = MessageConstants.MUST_NOT_BE_BLANK_PASSWORD)
    private String password;


}
