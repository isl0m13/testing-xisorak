package com.example.appgidritexmonitoring.payload;

import com.example.appgidritexmonitoring.util.MessageConstants;
import lombok.*;

import javax.validation.constraints.NotBlank;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GroupAddDTO {

    @NotBlank(message = MessageConstants.MUST_NOT_BE_BLANK_NAME)
    private String name;



}
