package com.jeonghyeon.taxiproject.dto.request;

public class RegisterRequestDto {
    private String memberId;
    private String password;
    private String nickname;
    private int gender;

    public RegisterRequestDto(String memberId, String password, String nickname, int gender) {
        this.memberId = memberId;
        this.password = password;
        this.nickname = nickname;
        this.gender = gender;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }
}
