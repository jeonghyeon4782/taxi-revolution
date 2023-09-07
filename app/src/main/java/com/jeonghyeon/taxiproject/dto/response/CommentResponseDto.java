package com.jeonghyeon.taxiproject.dto.response;

public class CommentResponseDto {
    private Long commentId;
    private String nickname;
    private String createAt;
    private String content;
    private int gender;

    public CommentResponseDto(Long commentId, String nickname, String createAt, String content, int gender) {
        this.commentId = commentId;
        this.nickname = nickname;
        this.createAt = createAt;
        this.content = content;
        this.gender = gender;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public Long getCommentId() {
        return commentId;
    }

    public void setCommentId(Long commentId) {
        this.commentId = commentId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
