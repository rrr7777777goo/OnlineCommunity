package com.onlinecommunity.controller;

import com.onlinecommunity.domain.member.Auth;
import com.onlinecommunity.service.MemberService;
import com.onlinecommunity.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final MemberService memberService;
    private final TokenProvider tokenProvider;

    // 관리자 계정 생성
    @PostMapping("/create_admin")
    public ResponseEntity<?> create_admin(@RequestBody Auth.SignUp request) {
        var result = this.memberService.register(request, false); // 관리자 계정 생성시엔 boolean 변수를 false로 전달한다.
        return ResponseEntity.ok("");
    }

    // 일반 사용자 회원가입
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody Auth.SignUp request) {
        var result = this.memberService.register(request, true); // 일반 사용자 계정 생성시엔 boolean 변수를 true로 전달한다.
        return ResponseEntity.ok(result);
    }

    // 로그인
    @PostMapping("/signin")
    public ResponseEntity<?> signin(@RequestBody Auth.SignIn request) {
        // 로그인용 API
        var member = this.memberService.authenticate(request);
        var token = this.tokenProvider.generateToken(member.getId(), member.getSignupid(), member.getRole().getName());
        log.info("user login -> " + request.getSignupid());

        return ResponseEntity.ok(token);
    }

    // 닉네임 수정
    @PutMapping("/update/nickname")
    public ResponseEntity<?> updateNickname(@RequestBody Auth.ChangeNickname request) {
        var result = this.memberService.changeNickname(request);
        return ResponseEntity.ok(result);
    }

    // 비밀번호 변경
    @PutMapping("/update/password")
    public ResponseEntity<?> updatePassword(@RequestBody Auth.ChangePassword request) {
        var result = this.memberService.changePassword(request);
        return ResponseEntity.ok(result);
    }

    // 관리자의 유저 계정 잠금 (유저의 계정만 잠금이 가능하다.)
    @PutMapping("lock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> lockUserAccount(@RequestBody Auth.IdClass request) {
        var result = this.memberService.lockUserAccount(request);
        return ResponseEntity.ok(result);
    }

    // 관리자의 유저 계정 잠금 해제 (유저의 계정만 잠금 해제가 가능하다.)
    @PutMapping("unlock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> unLockUserAccount(@RequestBody Auth.IdClass request) {
        var result = this.memberService.unLockUserAccount(request);
        return ResponseEntity.ok(result);
    }

    // 자신의 계정 삭제 (회원탈퇴)
    @DeleteMapping("delete")
    public ResponseEntity<?> delete(@RequestBody Auth.SignIn request) {
        var result = this.memberService.delete(request);
        return ResponseEntity.ok(result);
    }
}
