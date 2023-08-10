package com.jeonghyeon.taxiproject.dto.request;

public class PostUpdateRequestDto {
    private String title;
    private String content;
    private String departureLocation;
    private String destinationLocation;
    private String recruitmentStatus;
    private int allSeat;
    private String departureTime;

    public PostUpdateRequestDto(String title, String content, String departureLocation, String destinationLocation, String recruitmentStatus, int allSeat, String departureTime) {
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
