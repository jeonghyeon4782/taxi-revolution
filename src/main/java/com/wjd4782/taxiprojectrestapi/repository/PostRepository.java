package com.wjd4782.taxiprojectrestapi.repository;

import com.wjd4782.taxiprojectrestapi.domain.Member;
import com.wjd4782.taxiprojectrestapi.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByMember(Member member);
}
