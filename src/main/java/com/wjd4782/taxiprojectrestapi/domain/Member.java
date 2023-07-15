package com.wjd4782.taxiprojectrestapi.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Member implements UserDetails {

    // primary key
    @Id
    @Column(updatable = false, unique = true, nullable = false)
    private String memberId;

    // 비밀번호
    @Column(name = "password", nullable = false)
    private String password;

    // 닉네임
    @Column(name = "nickname", nullable = false, unique = true)
    private String nickname;

    // 성별
    @Column(name = "gender", nullable = false)
    private int gender; // 남자 : 0, 여자 : 1

    // 권한
    @ElementCollection(fetch = FetchType.EAGER)
    @Builder.Default
    private List<String> roles = new ArrayList<>();
    
    // 회원정보 수정
    public void update(String password, String nickname) {
        this.password = password;
        this.nickname = nickname;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public String getUsername() {
        return memberId;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
