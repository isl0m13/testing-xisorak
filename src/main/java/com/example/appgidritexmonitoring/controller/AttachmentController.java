package com.example.appgidritexmonitoring.controller;

import com.example.appgidritexmonitoring.payload.ApiResult;
import com.example.appgidritexmonitoring.payload.FileResponse;
import com.example.appgidritexmonitoring.util.RestConstants;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;

@RequestMapping(AttachmentController.ATTACHMENT_CONTROLLER_BASE_PATH)
public interface AttachmentController {
    String ATTACHMENT_CONTROLLER_BASE_PATH = RestConstants.BASE_PATH + "/attachment";
    String UPLOAD_PATH = "/upload";
    String UPLOAD_PATH_FOR_IMAGE = "/upload-image";
    String DOWNLOAD_PATH = "/download/{filename:.+}";
    String DELETE_PATH = "/delete/{filename:.+}";

    @PostMapping(path = UPLOAD_PATH, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ApiResult<FileResponse> upload(@RequestParam("file") MultipartFile file);

    @PostMapping(path = UPLOAD_PATH_FOR_IMAGE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ApiResult<FileResponse> uploadPhoto(@RequestParam("file") MultipartFile file);

    @GetMapping(DOWNLOAD_PATH)
    ResponseEntity<Resource> downloadFile(@PathVariable String filename, HttpServletRequest request);

    @DeleteMapping(DELETE_PATH)
    ApiResult<?> delete(@PathVariable String filename);



}
