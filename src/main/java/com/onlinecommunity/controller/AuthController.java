package com.onlinecommunity.controller;

import com.onlinecommunity.domain.member.Auth;
import com.onlinecommunity.service.MemberService;
import com.onlinecommunity.security.TokenProvider;
import jakarta.validation.constraints.PositiveOrZero;
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
    @PostMapping("/signup-user")
    public ResponseEntity<?> signupUser(@Validated @RequestBody Auth.SignUp request) {
        var result = this.memberService.register(request, false); // 일반 사용자 계정 생성시엔 boolean 변수를 false로 전달한다.
        return ResponseEntity.ok(result);
    }

    // 관리자 회원가입
    @PostMapping("/signup-admin")
    public ResponseEntity<?> signupAdmin(@Validated @RequestBody Auth.SignUp request) {
        var result = this.memberService.register(request, true); // 관리자 계정 생성시엔 boolean 변수를 true로 전달한다.
        return ResponseEntity.ok(result);
    }

    // 로그인
    @PostMapping("/signin")
    public ResponseEntity<?> signin(@Validated @RequestBody Auth.SignIn request) {
        // 로그인용 API
        var member = this.memberService.authenticate(request);
        var token = this.tokenProvider.generateToken(member.getId(), member.getSignupId(), member.getRole().getName());
        log.info("user login -> " + request.getSignupId());

        return ResponseEntity.ok(token);
    }

    // 현재 로그인중인 사용자의 정보 가져오기
    @GetMapping("/signin-information")
    public ResponseEntity<?> getSigninUserInformation() {
        var result = this.memberService.getSigninUserInformation();
        return ResponseEntity.ok(result);
    }

    // 고유번호를 기반으로 사용자의 정보 가져오기
    @GetMapping("/information")
    public ResponseEntity<?> getUserInforamtion(@RequestParam @PositiveOrZero int id) {
        var result = this.memberService.getUserInformation(id);
        return ResponseEntity.ok(result);
    }

    // 사용자의 정보 수정 (현재는 닉네임 변경을 위해서 사용된다.)
    @PutMapping("/information")
    public ResponseEntity<?> updateUserInformation(@Validated @RequestBody Auth.ChangeUserInfo request) {
        var result = this.memberService.updateUserInformation(request);
        return ResponseEntity.ok(result);
    }

    // 암호 수정
    @PutMapping("/password")
    public ResponseEntity<?> updatePassword(@Validated @RequestBody Auth.ChangePassword request) {
        var result = this.memberService.changePassword(request);
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

    // 계정 삭제 (회원탈퇴)
    @DeleteMapping("withdraw")
    public ResponseEntity<?> delete(@Validated @RequestBody Auth.SignIn request) {
        var result = this.memberService.delete(request);
        return ResponseEntity.ok(result);
    }
}
