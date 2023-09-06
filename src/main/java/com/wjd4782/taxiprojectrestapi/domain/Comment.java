package com.wjd4782.taxiprojectrestapi.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Getter
@NoArgsConstructor
@Entity
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId; // primary key

    @Column(name = "content", nullable = false)
    private String content;

    @ManyToOne
    @JoinColumn(name = "memberId")
    private Member member; // 댓글 작성자

    @ManyToOne
    @JoinColumn(name = "postId")
    private Post post; // 게시글

    @CreationTimestamp
    @Column(name = "createAt")
    private Timestamp createAt;

    @Builder
    public Comment(String content, Member member, Post post) {
        this.content = content;
        this.member = member;
        this.post = post;
    }
}
