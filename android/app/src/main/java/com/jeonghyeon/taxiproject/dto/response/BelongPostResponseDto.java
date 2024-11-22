package com.jeonghyeon.taxiproject.dto.response;

import java.util.List;

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

    public boolean isRecruitmentOpen() {
        return "모집중".equals(recruitmentStatus);
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public Long getBelongId() {
        return belongId;
    }

    public void setBelongId(Long belongId) {
        this.belongId = belongId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDepartureLocation() {
        return departureLocation;
    }

    public void setDepartureLocation(String departureLocation) {
        this.departureLocation = departureLocation;
    }

    public String getDestinationLocation() {
        return destinationLocation;
    }

    public void setDestinationLocation(String destinationLocation) {
        this.destinationLocation = destinationLocation;
    }

    public String getRecruitmentStatus() {
        return recruitmentStatus;
    }

    public void setRecruitmentStatus(String recruitmentStatus) {
        this.recruitmentStatus = recruitmentStatus;
    }

    public int getRemainSeat() {
        return remainSeat;
    }

    public void setRemainSeat(int remainSeat) {
        this.remainSeat = remainSeat;
    }

    public int getAllSeat() {
        return allSeat;
    }

    public void setAllSeat(int allSeat) {
        this.allSeat = allSeat;
    }

    public int getAuthority() {
        return authority;
    }

    public void setAuthority(int authority) {
        this.authority = authority;
    }

    public List<BelongMemberResponseDto> getBelongMembers() {
        return belongMembers;
    }

    public void setBelongMembers(List<BelongMemberResponseDto> belongMembers) {
        this.belongMembers = belongMembers;
    }
}
