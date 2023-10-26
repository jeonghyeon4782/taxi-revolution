package com.wjd4782.taxiprojectrestapi.domain;

import com.wjd4782.taxiprojectrestapi.dto.request.PostUpdateRequestDto;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

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
    private int remainSeat; // 참여 좌석

    @Column(name = "AllSeat", nullable = false)
    private int allSeat; // 총 좌석

    @Column(name = "departureTime", nullable = false)
    private Timestamp departureTime; // 출발시간

    @Column(name = "createTime")
    private Timestamp createTime; // 생성시간

    @ManyToOne
    @JoinColumn(name = "memberId")
    private Member member; // 글쓴이

    // 소속 1:N
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Belong> belongs = new ArrayList<>();

    // 댓글 1:N
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

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
        int newAllSeat = requestDto.getAllSeat();

        if (newAllSeat < this.remainSeat) {
            throw new IllegalArgumentException("총 좌석 수를 다시 설정해주세요");
        }

        this.title = requestDto.getTitle();
        this.content = requestDto.getContent();
        this.departureLocation = requestDto.getDepartureLocation();
        this.destinationLocation = requestDto.getDestinationLocation();
        this.recruitmentStatus = requestDto.getRecruitmentStatus();
        this.allSeat = requestDto.getAllSeat();
        this.departureTime = departureTime;
    }

    // 인원 증가
    public void plusSeat() {
        this.remainSeat = this.remainSeat + 1;
    }

    // 인원 감소
    public void minusSeat() {
        this.remainSeat = this.remainSeat - 1;
    }
}
