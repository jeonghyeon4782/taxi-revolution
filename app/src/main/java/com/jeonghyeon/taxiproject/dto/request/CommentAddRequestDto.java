package com.jeonghyeon.taxiproject.dto.request;

public class CommentAddRequestDto {
    private String content; // 내용

    public CommentAddRequestDto(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
