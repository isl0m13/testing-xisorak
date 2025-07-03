package com.example.appgidritexmonitoring.payload;

import com.example.appgidritexmonitoring.util.MessageConstants;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class PagePinAddDTO {

    @NotBlank(message = MessageConstants.MUST_NOT_BE_BLANK_NAME)
    private String name;

    @NotBlank(message = MessageConstants.MUST_NOT_BE_BLANK_URL_OF_PAGE)
    private String url;
}
