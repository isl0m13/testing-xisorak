package com.example.appgidritexmonitoring.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FileResponse {
    private UUID id;
    private String fileName;
    private String fileType;
    private long size;
    private String fileDownloadUri;
    private String thumbnailMedium;
    private String thumbnailSmall;

    public FileResponse(UUID id, String fileName, String fileType, long size, String fileDownloadUri) {
        this.id = id;
        this.fileName = fileName;
        this.fileType = fileType;
        this.size = size;
        this.fileDownloadUri = fileDownloadUri;
    }


}
