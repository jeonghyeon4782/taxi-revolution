package com.wjd4782.taxiprojectrestapi.Exception;

import com.wjd4782.taxiprojectrestapi.dto.response.ResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(java.lang.Exception.class)
    public ResponseDto<String> handleException(Exception ex) {

        ResponseDto<String> errorResponse = new ResponseDto<String>(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage(), null);

        return errorResponse;
    }
}

