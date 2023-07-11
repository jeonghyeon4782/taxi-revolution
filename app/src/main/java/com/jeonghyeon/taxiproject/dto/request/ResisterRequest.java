package com.jeonghyeon.taxiproject.dto.request;

public class ResisterRequest {
    private String username;
    private String password;
    private String nickname;
    private int gender;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public ResisterRequest(String username, String password, String nickname, int gender) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.gender = gender;
    }
}
