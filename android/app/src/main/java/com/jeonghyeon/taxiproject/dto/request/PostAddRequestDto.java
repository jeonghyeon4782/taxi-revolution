package com.jeonghyeon.taxiproject.dto.request;

public class PostAddRequestDto {
    private String title; // 제목
    private String content; // 내용
    private String departureLocation; // 출발지
    private String destinationLocation; // 도착지
    private String recruitmentStatus; // 모집상태
    private int allSeat; // 총 좌석
    private String departureTime; // 출발시간

    public PostAddRequestDto(String title, String content, String departureLocation, String destinationLocation, String recruitmentStatus, int allSeat, String departureTime) {
        this.title = title;
        this.content = content;
        this.departureLocation = departureLocation;
        this.destinationLocation = destinationLocation;
        this.recruitmentStatus = recruitmentStatus;
        this.allSeat = allSeat;
        this.departureTime = departureTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public int getAllSeat() {
        return allSeat;
    }

    public void setAllSeat(int allSeat) {
        this.allSeat = allSeat;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }
}
