package com.wjd4782.taxiprojectrestapi.dto.response;

import com.wjd4782.taxiprojectrestapi.domain.Post;
import lombok.Builder;
import lombok.Data;

import java.time.format.DateTimeFormatter;

@Data
public class PostResponseDto {
    private Long postId; // primary key
    private String title; // 제목
    private String content; // 내용
    private String departureLocation; // 출발지
    private String destinationLocation; // 도착지
    private String recruitmentStatus; // 모집상태
    private int remainSeat; // 남은 좌석
    private int allSeat; // 총 좌석
    private String departureTime; // 출발시간
    private String createTime; // 생성시간
    private String nickname; // 글쓴이 닉네임
    private int gender; // 성별

    // domain >> dto
    @Builder
    public PostResponseDto(Post post) {
        // TimeStamp >> String
        DateTimeFormatter desiredFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDepartureTime = post.getDepartureTime().toLocalDateTime().format(desiredFormatter);
        String formattedCreateTime = post.getCreateTime().toLocalDateTime().format(desiredFormatter);
        this.title = post.getTitle();
        this.postId = post.getPostId();
        this.content = post.getContent();
        this.departureLocation = post.getDepartureLocation();
        this.destinationLocation = post.getDestinationLocation();
        this.recruitmentStatus = post.getRecruitmentStatus();
        this.remainSeat = post.getRemainSeat();
        this.allSeat = post.getAllSeat();
        this.departureTime = formattedDepartureTime;
        this.createTime = formattedCreateTime;
        this.nickname = post.getMember().getNickname();
        this.gender = post.getMember().getGender();
    }
}
