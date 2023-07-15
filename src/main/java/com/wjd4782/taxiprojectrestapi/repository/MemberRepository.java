package com.wjd4782.taxiprojectrestapi.repository;

import com.wjd4782.taxiprojectrestapi.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    // 아이디로 유저 찾기
    Optional<Member> findByMemberId(String username);

    // 닉네임으로 유저 찾기
    Optional<Member> findByNickname(String nickname);
}
