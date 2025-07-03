package com.example.appgidritexmonitoring.exceptions;


import com.example.appgidritexmonitoring.payload.ErrorData;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Objects;

@EqualsAndHashCode(callSuper = true)
@Data
public class RestException extends RuntimeException {
    private String userMsg;
    private HttpStatus status;
    private List<ErrorData> errors;

    private String resourceName;
    private String fieldName;
    private Object fieldValue;

    public RestException(String userMsg, HttpStatus status) {
        super(userMsg);
        this.userMsg = userMsg;
        this.status = status;
    }

    private RestException(HttpStatus status, List<ErrorData> errors) {
        this.status = status;
        this.errors = errors;
    }

   public static RestException restThrow(String userMsg, HttpStatus status){
        return new RestException(userMsg, status);
   }

   public static RestException restThrow(String userMsg){
        return new RestException(userMsg, HttpStatus.BAD_REQUEST);
   }

   public static RestException restThrow(List<ErrorData> errors, HttpStatus status){
        return new RestException(status, errors);
   }


}
