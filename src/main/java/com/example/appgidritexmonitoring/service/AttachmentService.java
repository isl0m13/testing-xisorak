package com.example.appgidritexmonitoring.service;

import com.example.appgidritexmonitoring.payload.ApiResult;
import com.example.appgidritexmonitoring.payload.FileResponse;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

public interface AttachmentService {
    ApiResult<FileResponse> uploadPhoto(MultipartFile file);

    ApiResult<FileResponse> upload(MultipartFile file);

    ResponseEntity<Resource> downloadFile(String filename, HttpServletRequest request);

}
