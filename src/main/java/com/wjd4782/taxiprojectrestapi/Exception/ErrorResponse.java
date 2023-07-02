package com.wjd4782.taxiprojectrestapi.Exception;

public class ErrorResponse {
    private String message; // 에러 내용
    private int errorCode; // 에러 코드

    public ErrorResponse(String message, int errorCode) {
        this.message = message;
        this.errorCode = errorCode;
    }
}
