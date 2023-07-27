package com.wjd4782.taxiprojectrestapi.domain;

import com.wjd4782.taxiprojectrestapi.dto.request.PostUpdateRequestDto;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Time;
import java.sql.Timestamp;

@Getter
@NoArgsConstructor
@Entity
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId; // primary key

    @Column(name = "title")
    private String title; // 제목

    @Column(name = "content")
    private String content; // 내용

    @Column(name = "departureLocation", nullable = false)
    private String departureLocation; // 출발지

    @Column(name = "destinationLocation", nullable = false)
    private String destinationLocation; // 도착지

    @Column(name = "recruitmentStatus", nullable = false)
    private String recruitmentStatus; // 모집상태

    @Column(name = "remainSeat", nullable = false)
    private int remainSeat; // 남은 좌석

    @Column(name = "AllSeat", nullable = false)
    private int allSeat; // 총 좌석

    @Column(name = "departureTime", nullable = false)
    private Timestamp departureTime; // 출발시간

    @Column(name = "createTime")
    private Timestamp createTime; // 생성시간

    @ManyToOne
    @JoinColumn(name = "memberId")
    private Member member; // 글쓴이

    @Builder
    public Post(String title, String content, String departureLocation, String destinationLocation, String recruitmentStatus, int remainSeat, int allSeat, Timestamp departureTime, Timestamp createTime, Member member) {
        this.title = title;
        this.content = content;
        this.departureLocation = departureLocation;
        this.destinationLocation = destinationLocation;
        this.recruitmentStatus = recruitmentStatus;
        this.remainSeat = remainSeat;
        this.allSeat = allSeat;
        this.departureTime = departureTime;
        this.createTime = createTime;
        this.member = member;
    }

    public void setRecruitmentStatus(String recruitmentStatus) {
        this.recruitmentStatus = recruitmentStatus;
    }

    // 게시글 수정
    public void updatePost(PostUpdateRequestDto requestDto) {
        Timestamp departureTime = Timestamp.valueOf(requestDto.getDepartureTime());
        this.title = requestDto.getTitle();
        this.content = requestDto.getContent();
        this.departureLocation = requestDto.getDepartureLocation();
        this.destinationLocation = requestDto.getDestinationLocation();
        this.recruitmentStatus = requestDto.getRecruitmentStatus();
        this.allSeat = requestDto.getAllSeat();
        this.remainSeat = requestDto.getAllSeat() - 1;
        this.departureTime = departureTime;
    }
}
