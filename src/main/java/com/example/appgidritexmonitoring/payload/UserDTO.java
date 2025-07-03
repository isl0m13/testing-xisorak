package com.example.appgidritexmonitoring.payload;

import com.example.appgidritexmonitoring.util.MessageConstants;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import javax.validation.constraints.NotBlank;
import java.util.Set;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTO {

    private UUID id;

    private String firstName;

    private String lastName;

    private String username;

    private Integer roleId;

    private Set<ReservoirDTO> reservoirs;




}
