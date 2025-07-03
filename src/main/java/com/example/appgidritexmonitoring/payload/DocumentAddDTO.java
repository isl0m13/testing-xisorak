package com.example.appgidritexmonitoring.payload;

import com.example.appgidritexmonitoring.util.MessageConstants;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DocumentAddDTO {

    @NotNull(message = MessageConstants.MUST_NOT_BE_NULL_ATTACHMENT_ID)
    private UUID attachmentId;


}
