package com.example.appgidritexmonitoring.service;

import com.example.appgidritexmonitoring.controller.AttachmentController;
import com.example.appgidritexmonitoring.entity.Attachment;
import com.example.appgidritexmonitoring.entity.Images;
import com.example.appgidritexmonitoring.entity.enums.PhotoFormatEnum;
import com.example.appgidritexmonitoring.exceptions.RestException;
import com.example.appgidritexmonitoring.payload.ApiResult;
import com.example.appgidritexmonitoring.payload.FileResponse;
import com.example.appgidritexmonitoring.repository.AttachmentRepository;
import com.example.appgidritexmonitoring.repository.ImageRepository;
import com.example.appgidritexmonitoring.util.MessageConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AttachmentServiceImpl implements AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final ImageRepository imageRepository;

    private final static int IMAGE_RESIZE_PERCENT_FOR_SMALL = 35;
    private final static int IMAGE_RESIZE_PERCENT_FOR_MEDIUM = 60;

    @Value("${app.admin.upload.folder}")
    private String uploadFilePath;

    @Value("${base.url}")
    private String baseURL;

    @Override
    public ApiResult<FileResponse> upload(MultipartFile file) {
        String fileDownloadUri = baseURL.concat(
                (AttachmentController.ATTACHMENT_CONTROLLER_BASE_PATH + "/download/"));

        Attachment attachment = saveAttachment(file);
        String fileName = storeFile(file, attachment.getId().toString());

        attachment.setDownloadUri(fileDownloadUri.concat(fileName));
        attachment.setFilePath(uploadFilePath + "/" + fileName);

        attachmentRepository.save(attachment);
        return ApiResult.successResponse(
                new FileResponse(
                        attachment.getId(),
                        fileName,
                        file.getContentType(),
                        file.getSize(),
                        attachment.getDownloadUri()
                )
        );
    }

    @Override
    public ApiResult<FileResponse> uploadPhoto(MultipartFile file) {
        //CHECKING IS FILE IMAGE AND LIMIT OF SIZE
        isImage(file);

        String fileDownloadUri = baseURL.concat(
                (AttachmentController.ATTACHMENT_CONTROLLER_BASE_PATH + "/download/"));


        Attachment attachment = saveAttachment(file);
        Images image = saveImage(attachment);

        String fileName = storePhotoFile(file, attachment.getId().toString());

        attachment.setDownloadUri(fileDownloadUri.concat(fileName));
        attachment.setFilePath(uploadFilePath + "/" + fileName);

        savedForImage(image);
        attachmentRepository.save(attachment);

        return ApiResult.successResponse(
                new FileResponse(
                        attachment.getId(),
                        fileName,
                        file.getContentType(),
                        file.getSize(),
                        attachment.getDownloadUri(),
                        fileDownloadUri + image.getMediumPhotoName(),
                        fileDownloadUri + image.getSmallPhotoName()
                )
        );
    }

    @Override
    public ResponseEntity<Resource> downloadFile(String filename, HttpServletRequest request) {
        Resource resource = loadFileAsResource(filename);
        String extension = filename.substring(filename.lastIndexOf(".") + 1);


        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        if (contentType == null)
            contentType = "application/octet-stream";

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);                            //instead inline we used to use attachment;
    }

    private void savedForImage(Images image) {
        try {
            Attachment attachment = image.getAttachment();
            String filePath = attachment.getFilePath();
            String attachmentName = filePath.substring(uploadFilePath.length() + 1);

            String smallName = PhotoFormatEnum.SMALL.getName().toLowerCase() + "_" + attachmentName;
            String mediumName = PhotoFormatEnum.MEDIUM.getName().toLowerCase() + "_" + attachmentName;

            String smallImageSavePath = uploadFilePath + "/" + smallName;
            String mediumImageSavePath = uploadFilePath + "/" + mediumName;

            ImageResizer.resizePercent(filePath, smallImageSavePath, IMAGE_RESIZE_PERCENT_FOR_SMALL);
            ImageResizer.resizePercent(filePath, mediumImageSavePath, IMAGE_RESIZE_PERCENT_FOR_MEDIUM);

            Path smallPath = Paths.get(smallImageSavePath);
            Path mediumPath = Paths.get(mediumImageSavePath);

            image.setSmallPhotoSize(Files.size(smallPath));
            image.setSmallPhotoName(smallName);

            image.setMediumPhotoSize(Files.size(mediumPath));
            image.setMediumPhotoName(mediumName);
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
    }


    private Attachment saveAttachment(MultipartFile file) {
        return attachmentRepository.save(new Attachment(file.getOriginalFilename(), file.getSize(), file.getContentType()));
    }

    private Images saveImage(Attachment attachment) {
        return imageRepository.save(new Images(attachment));
    }

    private Resource loadFileAsResource(String filename) {
        try {
            Path filePath = Path.of(uploadFilePath).resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists())
                return resource;
            else
                throw RestException.restThrow(MessageConstants.ATTACHMENT_NOT_FOUND, HttpStatus.NOT_FOUND);
        } catch (MalformedURLException e) {
            throw RestException.restThrow(MessageConstants.ATTACHMENT_NOT_FOUND, HttpStatus.NOT_FOUND);
        }
    }

    private String storeFile(MultipartFile file, String uuid) {
        String extension = getExtensionOfFile(file);
        uuid = uuid.concat(extension);

        if (uuid.contains(".."))
            throw RestException.restThrow(MessageConstants.FILE_NAME_INVALID, HttpStatus.CONFLICT);

        //COPY FILE TO TARGET LOCATION(REPLACING EXISTING ONE WITH SAME NAME)
        Path targetLocation = Path.of(uploadFilePath).resolve(uuid);
        createDir(Path.of(uploadFilePath));

        //SAVE FILE TO TARGET LOCATION
        try {
            Files.copy(file.getInputStream(), targetLocation);
        } catch (Exception e) {
            throw RestException.restThrow(MessageConstants.FILE_NOT_SAVED, HttpStatus.CONFLICT);
        }
        return uuid;
    }

    private String storePhotoFile(MultipartFile file, String uuid) {
        String originalFilename = file.getOriginalFilename();
        String extension = Objects.requireNonNull(originalFilename).substring(originalFilename.lastIndexOf("."));
        uuid = uuid.concat(extension);

        if (uuid.contains(".."))
            throw RestException.restThrow(MessageConstants.FILE_NAME_INVALID, HttpStatus.CONFLICT);

        //COPY FILE TO TARGET LOCATION(REPLACING EXISTING ONE WITH SAME NAME)
        Path targetLocation = Path.of(uploadFilePath).resolve(uuid);
        createDir(Path.of(uploadFilePath));

        //SAVE FILE TO TARGET LOCATION
        try {
            //READ FILE AS IMAGE
            BufferedImage image = ImageIO.read(file.getInputStream());
            long sizeOfFile = file.getSize();
            double imageSizeInMB = sizeOfFile / (1024.0 * 1024.0);

            //RESIZE IMAGE IF NECESSARY
            double maxSize = 1.0;
            if (imageSizeInMB > maxSize)
                image = resizeImage(image);
            //SAVE IMAGE TO TARGET LOCATION
            ImageIO.write(image, extension.substring(1), targetLocation.toFile());
        } catch (IOException e) {
            throw RestException.restThrow(MessageConstants.FILE_NOT_SAVED, HttpStatus.CONFLICT);
        }

        return uuid;
    }

    private BufferedImage resizeImage(BufferedImage image) {
        int originalWidth = image.getWidth();
        int originalHeight = image.getHeight();
        int newWidth = (int) (originalWidth * 0.4);
        int newHeight = (int) (originalHeight * 0.4);
        //CREATE NEW RESIZED IMAGE WITH CALCULATED DIMENSIONS
        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, image.getType());
        Image scaledInstance = image.getScaledInstance(newWidth, newHeight, Image.SCALE_DEFAULT);
        resizedImage.getGraphics().drawImage(scaledInstance, 0, 0, null);
        return resizedImage;
    }

    private void createDir(Path targetLocation) {
        try {
            if (!Files.exists(targetLocation))
                Files.createDirectories(targetLocation);
        } catch (Exception e) {
            throw RestException.restThrow(MessageConstants.DIRECTORY_NOT_CREATED, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void isImage(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/"))
            throw RestException.restThrow(MessageConstants.INVALID_FILE_EXTENSION, HttpStatus.BAD_REQUEST);

        if (file.getSize() >= 10_000_000)
            throw RestException.restThrow(MessageConstants.OUT_OF_MAX_FILE_SIZE, HttpStatus.BAD_REQUEST);
    }

    private static String getExtensionOfFile(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String extension = Objects.requireNonNull(originalFilename).substring(originalFilename.lastIndexOf("."));
        return extension;
    }


}
