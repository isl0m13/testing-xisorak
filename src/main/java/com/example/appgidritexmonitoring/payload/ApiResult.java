package com.example.appgidritexmonitoring.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResult<T> implements Serializable {
    private Boolean success = false;
    private String message;
    private T data;
    private List<ErrorData> errors;

    //RESPONCE WITH BOOLEAN(SUCCESS OR FAIL)
    public ApiResult(Boolean success) {
        this.success = success;
    }

    //SUCCESS RESPONCE WITH DATA
    public ApiResult(Boolean success, T data) {
        this.success = success;
        this.data = data;
    }

    //SUCCESS RESPONSE WITH DATA AND MESSAGE
    public ApiResult(Boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    //SUCCESS RESPONSE WITH MESSAGE
    public ApiResult(String message) {
        this.message = message;
        this.success = true;
    }

    //ERROR RESPONSE WITH MESSAGE AND ERROR CODE
    public ApiResult(String errorMsg, Integer errorCode ) {
        this.success = false;
        this.errors = Collections.singletonList(new ErrorData(errorMsg, errorCode));
    }

    //ERROR RESPONSE WITH DATA LIST
    private ApiResult(List<ErrorData> errors) {
        this.success = false;
        this.errors = errors;
    }

    public static <T> ApiResult<T> successResponse(T data){
        return new ApiResult<>(true, data);
    }

    public static <T> ApiResult<T> successResponse(T data, String message){
        return new ApiResult<>(true, message, data);
    }

    public static <T> ApiResult<T> successResponse(){
        return new ApiResult<>(true);
    }
    public static <T> ApiResult<T> successResponse(String message){
        return new ApiResult<>(message);
    }

    public static <T> ApiResult<T> errorResponse(String errorMsg, Integer code){
        return new ApiResult<>(errorMsg, code);
    }
    public static <T> ApiResult<T> errorResponse(List<ErrorData> errorDataList){
        return new ApiResult<>(errorDataList);
    }



}
