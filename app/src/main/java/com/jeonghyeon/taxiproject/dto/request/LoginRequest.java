package com.jeonghyeon.taxiproject.dto.request;

public class LoginRequest {
    private String memberId;
    private String password;

    public LoginRequest(String memberId, String password) {
        this.memberId = memberId;
        this.password = password;
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
}
