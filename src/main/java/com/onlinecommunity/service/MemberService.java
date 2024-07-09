package com.onlinecommunity.service;

import com.onlinecommunity.domain.member.Auth;
import com.onlinecommunity.domain.member.Member;
import com.onlinecommunity.domain.member.MemberRole;
import com.onlinecommunity.repository.MemberRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@AllArgsConstructor
public class MemberService {
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    // 회원가입 (isAdminSignup 매개변수가 true면 관리자 계정 회원가입, false면 일반 사용자 회원가입)
    public Member register(Auth.SignUp member, boolean isAdminSignup) {

        boolean exists = this.memberRepository.existsBySignupid(member.getSignupid());
        if (exists) {
            throw new RuntimeException("이미 존재하는 ID입니다. -> " + member.getSignupid());
        }
        exists = this.memberRepository.existsByNickname(member.getNickname());
        if (exists) {
            throw new RuntimeException("이미 존재하는 닉네임입니다. -> " + member.getNickname());
        }

        member.setPassword(this.passwordEncoder.encode(member.getPassword()));

        var result = this.memberRepository.save(member.toEntity(isAdminSignup));
        return result;
    }

    // 아이디를 기반으로 유저 정보(고유번호, 아이디) 가져오기
    public Auth.IdInterface loadUserBySignupid(String signupid) {
        var member = this.memberRepository.findBySignupid(signupid)
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 ID입니다. -> " + signupid));
        member.checkStatus(); // 계정 잠금여부 확인

        return new Auth.IdInterface() {
            @Override
            public int getId() {
                return member.getId();
            }
            @Override
            public String getSignupid() {
                return member.getSignupid();
            }
        };
    }

    // 입력받은 아이디, 비밀번호를 기반으로 유저정보 가져오기 (로그인)
    public Member authenticate(Auth.SignIn member) {

        var user = this.memberRepository.findBySignupid(member.getSignupid())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 ID입니다. -> " + member.getSignupid()));

        if(!this.passwordEncoder.matches(member.getPassword(), user.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        user.checkStatus(); // 계정 잠금여부 확인
        return user;
    }

    // 현재 로그인한 사람의 아이디 관련 정보를 가져오는 함수
    private Auth.IdInterface getIdInterface() {
        Auth.IdInterface idInterface = (Auth.IdInterface) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return idInterface;
    }

    // 현재 로그인한 사용자 정보 출력
    public Member getSigninUserInformation() {
        Auth.IdInterface currentIdInterface = getIdInterface();

        var user = this.memberRepository.findBySignupid(currentIdInterface.getSignupid())
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 ID입니다. -> " + currentIdInterface.getSignupid()));

        return user;
    }

    // 현재 로그인 한 계정의 권한 정보 가져오기
    private MemberRole getRole() {
        MemberRole memberRole = null;

        int cnt = 0;
        for(GrantedAuthority ga : SecurityContextHolder.getContext().getAuthentication().getAuthorities()) {
            memberRole = MemberRole.getEnum(ga.getAuthority());
            ++cnt;
            if (cnt > 1) {
                throw new RuntimeException("권한 정보가 1개를 초과하였습니다.");
            }
        }
        if (cnt == 0) {
            throw new RuntimeException("현재 계정에는 어떠한 권한 정보도 존재하지 않습니다.");
        }

        return memberRole;
    }

    // 고유번호를 기반으로 사용자 정보 출력하기
    public Member getUserInformation(int id) {
        var user = this.memberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 고유 번호입니다. -> " + id));

        return user;
    }

    // 현재 로그인한 유저의 닉네임 변경하기
    @Transactional
    public Member updateUserInformation(Auth.ChangeUserInfo change) {
        var user = getSigninUserInformation();

        // 현재 바꾸려는 닉네임이 이미 다른 사람이 사용중인 닉네임인지 확인
        boolean exists = this.memberRepository.existsByNickname(change.getNickname());
        if ((user.getNickname().compareTo(change.getNickname()) != 0) && exists) {
            throw new RuntimeException("이미 존재하는 닉네임입니다. -> " + change.getNickname() + " " + user.getNickname());
        }

        user.changeNickname(change.getNickname());
        return user;
    }

    // 현재 로그인한 유저의 패스워드 변경하기
    @Transactional
    public Member changePassword(Auth.ChangePassword change) {
        var user = getSigninUserInformation();

        user.changePassword(this.passwordEncoder.encode(change.getPassword()));
        return user;
    }

    // 계정 잠금 설정
    @Transactional
    public Member lockUserAccount(Auth.IdClass idClass) {
        var user = this.memberRepository.findBySignupid(idClass.getSignupid())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 ID입니다. -> " + idClass.getSignupid()));
        user.lockAccount();
        return user;
    }

    // 계정 잠금 해제 설정
    @Transactional
    public Member unLockUserAccount(Auth.IdClass idClass) {
        var user = this.memberRepository.findBySignupid(idClass.getSignupid())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 ID입니다. -> " + idClass.getSignupid()));
        user.unLockAccount();
        return user;
    }

    // 계정 삭제
    public String delete(Auth.SignIn member) {

        var user = this.memberRepository.findBySignupid(member.getSignupid())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 ID입니다. -> " + member.getSignupid()));

        if (!this.passwordEncoder.matches(member.getPassword(), user.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        this.memberRepository.delete(user);
        return "Delete Complete!";
    }
}
