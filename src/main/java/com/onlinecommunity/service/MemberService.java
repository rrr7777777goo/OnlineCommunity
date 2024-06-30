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

@Slf4j
@Service
@AllArgsConstructor
public class MemberService {
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    // 일반 사용자 회원가입
    public Member register(Auth.SignUp member, boolean isUserSignup) {
        member.isValid(isUserSignup);

        boolean exists = this.memberRepository.existsBySignupid(member.getSignupid());
        if(exists) {
            throw new RuntimeException("이미 존재하는 ID입니다. -> " + member.getSignupid());
        }
        exists = this.memberRepository.existsByNickname(member.getNickname());
        if(exists) {
            throw new RuntimeException("이미 존재하는 닉네임입니다. -> " + member.getNickname());
        }

        member.setPassword(this.passwordEncoder.encode(member.getPassword()));

        var result = this.memberRepository.save(member.toEntity(isUserSignup));
        return result;
    }


    // 아이디를 기반으로 유저 정보 가져오기
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
        member.isValid();

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

    // 현재 로그인 한 계정의 권한 정보 가져오기
    private MemberRole getRole() {
        MemberRole memberRole = null;

        int cnt = 0;
        for(GrantedAuthority ga : SecurityContextHolder.getContext().getAuthentication().getAuthorities()) {
            memberRole = MemberRole.getEnum(ga.getAuthority());
            ++cnt;
            if(cnt > 1) throw new RuntimeException("권한 정보가 1개를 초과하였습니다.");
        }
        if(cnt == 0) throw new RuntimeException("현재 계정에는 어떠한 권한 정보도 존재하지 않습니다.");

        return memberRole;
    }

    // 현재 로그인한 계정의 닉네임 변경하기
    public Member changeNickname(Auth.ChangeNickname changeNickname) {
        Auth.IdInterface currentIdInterface = getIdInterface();

        var user = this.memberRepository.findBySignupid(currentIdInterface.getSignupid())
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 ID입니다. -> " + currentIdInterface.getSignupid()));
        if(user.getNickname().compareTo(changeNickname.getNickname()) == 0) {
            throw new RuntimeException("변경 전 닉네임과 변경 후 닉네임이 모두 '" + changeNickname.getNickname() + "' 로 동일합니다.");
        }

        MemberRole currentMemberRole = getRole();

        changeNickname.isValid(currentMemberRole);
        boolean exists = this.memberRepository.existsByNickname(changeNickname.getNickname());
        if(exists) {
            throw new RuntimeException("이미 존재하는 닉네임입니다. -> " + changeNickname.getNickname());
        }

        user.changeNickname(changeNickname.getNickname());
        this.memberRepository.save(user);
        return user;
    }

    // 현재 로그인한 계정의 비밀번호 변경하기
    public Member changePassword(Auth.ChangePassword changePassword) {
        Auth.IdInterface currentIdInterface = getIdInterface();

        var user = this.memberRepository.findBySignupid(currentIdInterface.getSignupid())
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 ID입니다. -> " + currentIdInterface.getSignupid()));
        if(this.passwordEncoder.matches(changePassword.getPassword(), user.getPassword())) {
            throw new RuntimeException("변경 전 패스워드와 변경 후 패스워드가 동일합니다.");
        }

        changePassword.isValid();

        user.changePassword(this.passwordEncoder.encode(changePassword.getPassword()));
        this.memberRepository.save(user);
        return user;
    }

    // 계정 잠금 설정
    public Member lockUserAccount(Auth.IdClass idClass) {
        idClass.isValid();
        
        var user = this.memberRepository.findBySignupid(idClass.getSignupid())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 ID입니다. -> " + idClass.getSignupid()));
        user.lockAccount();
        this.memberRepository.save(user);
        return user;
    }

    // 계정 잠금 해제 설정
    public Member unLockUserAccount(Auth.IdClass idClass) {
        idClass.isValid();
        
        var user = this.memberRepository.findBySignupid(idClass.getSignupid())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 ID입니다. -> " + idClass.getSignupid()));
        user.unLockAccount();
        this.memberRepository.save(user);
        return user;
    }

    // 계정 삭제
    public String delete(Auth.SignIn member) {
        member.isValid();

        var user = this.memberRepository.findBySignupid(member.getSignupid())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 ID입니다. -> " + member.getSignupid()));

        if(!this.passwordEncoder.matches(member.getPassword(), user.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        this.memberRepository.delete(user);
        return "Delete Complete!";
    }
}
