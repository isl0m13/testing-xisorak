package com.example.appgidritexmonitoring.payload;

import lombok.*;

import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DocumentDTO {

    private UUID documentId;

    private String name;

    private String description;

    private FileResponse attachment;

}
