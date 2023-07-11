package com.wjd4782.taxiprojectrestapi.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@Table(name = "MEMBERS")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    // primary key
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    // 아이디
    @Column(name = "username", nullable = false)
    private String username;

    // 비밀번호
    @Column(name = "password", nullable = false)
    private String password;

    // 닉네임
    @Column(name = "nickname", nullable = false)
    private String nickname;

    // 성별
    @Column(name = "gender", nullable = false)
    private int gender; // 남자 : 0, 여자 : 1

    // 권한
    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private List<Role> roles = new ArrayList<>();

    @Builder
    public Member(String username, String password, String nickname, int gender, List<Role> roles) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.gender = gender;
        this.roles = Collections.singletonList(Role.ROLE_MEMBER);
    }

    // 권한 추가
    public void addRole(Role role) {
        this.roles.add(role);
    }
}
