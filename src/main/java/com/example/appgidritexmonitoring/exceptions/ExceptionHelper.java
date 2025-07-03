package com.example.appgidritexmonitoring.exceptions;

import com.example.appgidritexmonitoring.payload.ApiResult;
import com.example.appgidritexmonitoring.payload.ErrorData;
import com.example.appgidritexmonitoring.util.MessageConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;


@RestControllerAdvice
@RequiredArgsConstructor
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ExceptionHelper {

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<?> handelException(MethodArgumentNotValidException ex) {
        ex.printStackTrace();
        List<ErrorData> errorDataList = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
                    String[] codes = error.getCodes();
                    assert codes != null;
                    String code = codes[codes.length - 1];
                    FieldError fieldError = (FieldError) error;
                    errorDataList.add(new ErrorData(fieldError.getDefaultMessage(), 422, fieldError.getField()));
                }
        );
        return new ResponseEntity<>(ApiResult.errorResponse(errorDataList), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = ConstraintViolationException.class)
    public ResponseEntity<?> handleException(ConstraintViolationException ex){
        ex.printStackTrace();;
        return new ResponseEntity<>(ApiResult.errorResponse(ex.getMessage(), 400), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {AccessDeniedException.class})
    public ResponseEntity<?> handleException(AccessDeniedException exception) {
        exception.printStackTrace();
        return new ResponseEntity<>(ApiResult.errorResponse(MessageConstants.YOU_HAVE_NOT_PERMISSION, 403), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(value = {RestException.class})
    public ResponseEntity<ApiResult<ErrorData>> handleException(RestException ex) {
        ex.printStackTrace();
        //AGAR RESOURSE TOPILMAGANI XATOSI BORSA CLIETGA QAYSI TABLEDA NIMA TOPILMAGANI HAQIDA HABAR KETADI
        if (ex.getFieldName() != null)
            return new ResponseEntity<>(ApiResult.errorResponse(ex.getUserMsg(), ex.getStatus().value()), ex.getStatus());

        //AKS HOLDA DOIMIY EXCEPTIONLAR ISHLAYVERADI
        if (ex.getErrors() != null)
            return new ResponseEntity<>(ApiResult.errorResponse(ex.getErrors()), ex.getStatus());
        return new ResponseEntity<>(ApiResult.errorResponse(ex.getUserMsg(), ex.getStatus().value()), ex.getStatus());

    }


}
