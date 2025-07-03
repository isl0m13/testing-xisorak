package com.example.appgidritexmonitoring.util;

import com.example.appgidritexmonitoring.entity.User;
import com.example.appgidritexmonitoring.exceptions.RestException;
import com.example.appgidritexmonitoring.payload.PagePinDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class CommonUtils {
    //GET CURRENT USER FROM CONTEXT
    public static User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public static LocalDateTime parseLocalDateTimeFromStringSlash(String strDate) {
        if (Objects.nonNull(strDate) && Character.isDigit(strDate.charAt(0))) {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            return LocalDateTime.parse(strDate, dateTimeFormatter);
        }
        return null;
    }

    public static LocalDateTime parseLocalDateTimeFromStringDash(String strDate) {
        if (Objects.nonNull(strDate) && Character.isDigit(strDate.charAt(0))) {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return LocalDateTime.parse(strDate, dateTimeFormatter);
        }
        return null;
    }

    private static LocalDateTime parseLocalDateTimeFromStringStandart(String strDate) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        return LocalDateTime.parse(strDate, dateTimeFormatter);
    }

    public static List<LocalDateTime> parseLocalDateTimesFromStingsStandart(String startDate, String endDate){
        LocalDateTime startLocalDateTime;
        LocalDateTime endLocalDateTime;

        try {
            startDate = startDate.concat(" 00:00:00");
            endDate = endDate.concat(" 00:00:00");
            startLocalDateTime = CommonUtils.parseLocalDateTimeFromStringStandart(startDate);
            endLocalDateTime = CommonUtils.parseLocalDateTimeFromStringStandart(endDate);
        } catch (Exception e) {
            throw RestException.restThrow(MessageConstants.INVALID_DATE_FORMAT);
        }
        if (endLocalDateTime.isBefore(startLocalDateTime))
            throw RestException.restThrow(MessageConstants.START_DATE_MUST_BE_BEFORE_END_DATE);

        return Arrays.asList(startLocalDateTime, endLocalDateTime.plusDays(1));
    }

    public static double roundToHundred(double num){
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        return Double.parseDouble(decimalFormat.format(num));
    }

    public static String parseLocalDateTimeToString(LocalDateTime dateTime){
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        return dateTime.format(dateTimeFormatter);
    }


}
