package com.onlinecommunity.repository;

import com.onlinecommunity.domain.member.Auth;
import com.onlinecommunity.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Integer> {
    Optional<Member> findById(int id);

    Optional<Member> findBySignupid(String signupid);

    // @Query(value="select m.id as id, m.signupid as signupid \n" +
    //         " from com.onlinecommunity.domain.member.Member as m \n" +
    //         " where m.signupid = ?1")
    // Optional<Auth.IdInterface> findidBySignupid(String signupid);

    boolean existsBySignupid(String signupid);

    boolean existsByNickname(String nickname);
}
