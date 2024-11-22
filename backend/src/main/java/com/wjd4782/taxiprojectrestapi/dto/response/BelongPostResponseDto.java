package com.wjd4782.taxiprojectrestapi.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class BelongPostResponseDto {
    private Long postId; // 게시글 아이디
    private Long belongId; // 소속 아이디
    private String title; // 제목
    private String departureLocation; // 출발지
    private String destinationLocation; // 도착지
    private String recruitmentStatus; // 모집상태
    private int remainSeat; // 남은 좌석
    private int allSeat; // 총 좌석
    private int authority; // 게시글 권한
    private List<BelongMemberResponseDto> belongMembers; // 게시글에 소속된 유저

    @Builder
    public BelongPostResponseDto(Long postId, Long belongId, String title, String departureLocation, String destinationLocation, int authority, String recruitmentStatus, int remainSeat, int allSeat, List<BelongMemberResponseDto> belongMembers) {
        this.postId = postId;
        this.belongId = belongId;
        this.title = title;
        this.departureLocation = departureLocation;
        this.destinationLocation = destinationLocation;
        this.authority = authority;
        this.recruitmentStatus = recruitmentStatus;
        this.remainSeat = remainSeat;
        this.allSeat = allSeat;
        this.belongMembers = belongMembers;
    }
}
