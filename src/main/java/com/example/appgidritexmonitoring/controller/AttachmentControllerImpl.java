package com.example.appgidritexmonitoring.controller;

import com.example.appgidritexmonitoring.payload.ApiResult;
import com.example.appgidritexmonitoring.payload.FileResponse;
import com.example.appgidritexmonitoring.service.AttachmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
public class AttachmentControllerImpl implements AttachmentController{

    private final AttachmentService attachmentService;

    @Override
    public ApiResult<FileResponse> upload(MultipartFile file) {
        return attachmentService.upload(file);
    }

    @Override
    public ApiResult<FileResponse> uploadPhoto(MultipartFile file) {
        return attachmentService.uploadPhoto(file);
    }

    @Override
    public ResponseEntity<Resource> downloadFile(String filename, HttpServletRequest request) {
        return attachmentService.downloadFile(filename, request);
    }

    @Override
    public ApiResult<?> delete(String filename) {
        return null;
    }
}
