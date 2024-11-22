package com.jeonghyeon.taxiproject.dto.request;

public class CommentUpdateRequestDto {
    private String content; // 내용

    public CommentUpdateRequestDto(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
