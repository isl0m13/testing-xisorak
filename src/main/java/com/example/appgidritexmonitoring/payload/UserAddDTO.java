package com.example.appgidritexmonitoring.payload;

import com.example.appgidritexmonitoring.util.MessageConstants;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder
public class UserAddDTO {

    @NotBlank(message = MessageConstants.MUST_NOT_BE_BLANK_FIRST_NAME)
    private String firstName;
    @NotBlank(message = MessageConstants.MUST_NOT_BE_BLANK_LAST_NAME)
    private String lastName;
    @NotBlank(message = MessageConstants.MUST_NOT_BE_BLANK_PASSWORD)
    private String password;
    @NotBlank(message = MessageConstants.MUST_NOT_BE_BLANK_USERNAME)
    private String userName;
    @NotNull(message = MessageConstants.MUST_NOT_BE_NULL_ROLE_ID)
    private Integer roleId;
    @NotNull(message = MessageConstants.USER_MUST_HAVE_MIN_ONE_RESERVOIR)
    private Set<UUID> reservoirs;


    private UUID attachmentId;


}
