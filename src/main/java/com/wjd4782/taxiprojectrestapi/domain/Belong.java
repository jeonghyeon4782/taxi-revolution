package com.wjd4782.taxiprojectrestapi.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Getter
@NoArgsConstructor
@Entity
public class Belong {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long belongId; // primary key

    @ManyToOne
    @JoinColumn(name = "memberId")
    private Member member; // 글쓴이

    @ManyToOne
    @JoinColumn(name = "postId")
    private Post post; // 게시글

    @Column(name = "authority")
    private int authority; // 0 : 방장, 1 : 일반

    @Builder
    public Belong(Member member, Post post, int authority) {
        this.member = member;
        this.post = post;
        this.authority = authority;
    }
}
