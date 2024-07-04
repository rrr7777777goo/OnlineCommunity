package com.onlinecommunity.controller;

import com.onlinecommunity.domain.member.Auth;
import com.onlinecommunity.service.MemberService;
import com.onlinecommunity.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final MemberService memberService;
    private final TokenProvider tokenProvider;

    // 일반 사용자 회원가입
    @PostMapping("/signup_user")
    public ResponseEntity<?> signupUser(@Validated @RequestBody Auth.SignUp request) {
        var result = this.memberService.register(request, false); // 일반 사용자 계정 생성시엔 boolean 변수를 false로 전달한다.
        return ResponseEntity.ok(result);
    }

    // 관리자 회원가입
    @PostMapping("/signup_admin")
    public ResponseEntity<?> signupAdmin(@Validated @RequestBody Auth.SignUp request) {
        var result = this.memberService.register(request, true); // 관리자 계정 생성시엔 boolean 변수를 true로 전달한다.
        return ResponseEntity.ok(result);
    }

    // 로그인
    @PostMapping("/signin")
    public ResponseEntity<?> signin(@Validated @RequestBody Auth.SignIn request) {
        // 로그인용 API
        var member = this.memberService.authenticate(request);
        var token = this.tokenProvider.generateToken(member.getId(), member.getSignupid(), member.getRole().getName());
        log.info("user login -> " + request.getSignupid());

        return ResponseEntity.ok(token);
    }

    // 현재 로그인중인 사용자의 정보 가져오기
    @GetMapping("/signin_information")
    public ResponseEntity<?> getSigninUserInformation() {
        var result = this.memberService.getSigninUserInformation();
        return ResponseEntity.ok(result);
    }

    // 특정 아이디를 가진 사용자의 정보 가져오기
    @GetMapping("/information")
    public ResponseEntity<?> getUserInforamtion(@RequestParam int id) {
        var result = this.memberService.getUserInformation(id);
        return ResponseEntity.ok(result);
    }

    // 닉네임 수정 or 비밀번호 변경 (동시에 변경도 가능)
    @PutMapping("/update")
    public ResponseEntity<?> updateNickname(@Validated @RequestBody Auth.Change request) {
        var result = this.memberService.changePasswordOrNickname(request);
        return ResponseEntity.ok(result);
    }

    // 관리자의 유저 계정 잠금 (유저의 계정만 잠금이 가능하다.)
    @PutMapping("lock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> lockUserAccount(@Validated @RequestBody Auth.IdClass request) {
        var result = this.memberService.lockUserAccount(request);
        return ResponseEntity.ok(result);
    }

    // 관리자의 유저 계정 잠금 해제 (유저의 계정만 잠금 해제가 가능하다.)
    @PutMapping("unlock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> unLockUserAccount(@Validated @RequestBody Auth.IdClass request) {
        var result = this.memberService.unLockUserAccount(request);
        return ResponseEntity.ok(result);
    }

    // 자신의 계정 삭제 (회원탈퇴)
    @DeleteMapping("delete")
    public ResponseEntity<?> delete(@Validated @RequestBody Auth.SignIn request) {
        var result = this.memberService.delete(request);
        return ResponseEntity.ok(result);
    }
}
