package com.onlinecommunity.repository;

import com.onlinecommunity.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Integer> {
    Optional<Member> findById(int id);

    Optional<Member> findBySignupId(String signupId);

    boolean existsBySignupId(String signupId);

    boolean existsByNickname(String nickname);
}
