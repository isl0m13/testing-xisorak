package com.example.appgidritexmonitoring.util;

import com.example.appgidritexmonitoring.controller.*;
import com.example.appgidritexmonitoring.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;

public interface RestConstants {

    ObjectMapper OBJECT_MAPPER = new ObjectMapper();


    String BASE_PATH = "/api/v1";
    String AUTH_HEADER = "Authorization";


    String[] OPEN_PAGES = {
            "/*",
            AuthController.AUTH_CONTROLLER_BASE_PATH + "/**",
            AttachmentController.ATTACHMENT_CONTROLLER_BASE_PATH + "/download/**",
            PiezometerController.PIEZOMETER_CONTROLLER_BASE_PATH + "/download/**",
            WaterLevelGaugeController.WATER_LEVEL_GAUGE_CONTROLLER_BASE_PATH + WaterLevelGaugeController.LATEST_MEASUREMENTS_BY_SECRET_KEY + "/**",
            SpillwayController.SPILLWAY_CONTROLLER_BASE_PATH + SpillwayController.LATEST_MEASUREMENTS_BY_SECRET_KEY + "/**"
    };



}
