package com.example.appgidritexmonitoring.service;

import com.example.appgidritexmonitoring.exceptions.RestException;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.print.DocFlavor;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class ImageResizer {


    /**
     * Resizes an image to a absolute width and height (the image may not be
     * proportional).
     *
     * @param inputImagePath  Path of the original image
     * @param outputImagePath Path to save the resized image
     * @param scaledWidth     absolute width in pixels
     * @param scaledHeight    absolute height in pixels
     */
    public static void resizeImage(String inputImagePath, String outputImagePath, int scaledWidth, int scaledHeight) {
        //READING INPUT IMAGE
        BufferedImage inputImage = getBufferedImageFromInputPath(inputImagePath);
        //CREATING OUTPUT IMAGE
        BufferedImage outputImage = new BufferedImage(scaledWidth, scaledHeight, inputImage.getType());

        //SCALED THE INPUT TO THE OUTPUT IMAGE
        Graphics2D graphics = outputImage.createGraphics();
        graphics.drawImage(inputImage, 0, 0, scaledWidth, scaledHeight, null);
        graphics.dispose();

        //EXTRACTS EXTENSION OF OUTPUT FILE
        String formatName = outputImagePath.substring(outputImagePath.lastIndexOf(".") + 1);

        //WRITES TO OUTPUT FILE
        try {
            ImageIO.write(outputImage, formatName, new File(outputImagePath));
        }catch (IOException e) {
            throw RestException.restThrow("Error in Image.read(file)", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Resizes an image by a percentage of original size (proportional).
     */
    public static void resizePercent(String inputImagePath, String outputImagePath, int percent){
        //READ IMAGE FROM PATH
        BufferedImage inputImage = getBufferedImageFromInputPath(inputImagePath);

        int scaledWidth = (inputImage.getWidth() * percent / 100);
        int scaledHeight = (inputImage.getHeight() * percent / 100);

        resizeImage(inputImagePath, outputImagePath, scaledWidth, scaledHeight);
    }

    private static BufferedImage getBufferedImageFromInputPath(String inputImagePath){
        File inputFile = new File(inputImagePath);
        try {
            return ImageIO.read(inputFile);
        } catch (IOException e) {
            throw RestException.restThrow("Error in Image.read(file)", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }




}

