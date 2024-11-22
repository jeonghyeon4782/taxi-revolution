package com.wjd4782.taxiprojectrestapi.dto.response;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CommentResponseDto {
    private Long commentId;
    private String nickname;
    private String createAt;
    private String content;
    private int gender;
    private int authority;

    @Builder
    public CommentResponseDto(Long commentId, String nickname, String createAt, String content, int gender, int authority) {
        this.commentId = commentId;
        this.nickname = nickname;
        this.createAt = createAt;
        this.content = content;
        this.gender = gender;
        this.authority = authority;
    }
}
