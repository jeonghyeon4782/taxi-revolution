package com.wjd4782.taxiprojectrestapi.domain;

import jakarta.persistence.*;
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
@NoArgsConstructor
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
    private List<String> roles = new ArrayList<>();

    // 게시글 1:N
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();

    @Builder
    public Member(String memberId, String password, String nickname, int gender, List<String> roles) {
        this.memberId = memberId;
        this.password = password;
        this.nickname = nickname;
        this.gender = gender;
        this.roles = roles;
    }

    // 비밀번호 수정
    public void updatePassword(String password) {
        this.password = password;
    }

    // 닉네임 수정
    public void updateNickname(String nickname) {
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
