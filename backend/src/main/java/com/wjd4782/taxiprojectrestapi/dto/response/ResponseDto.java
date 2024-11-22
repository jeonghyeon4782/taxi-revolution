package com.wjd4782.taxiprojectrestapi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class ResponseDto<T> {
    private int status;
    private String msg;
    private T data;
}
