package com.onlinecommunity.domain.member;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Auth {
    private static final String firstAdminNickname = "ADMIN_";

    // 로그인용 클래스
    @Data
    public static class SignIn {
        private String signupid; // 아이디
        private String password; // 비밀번호

        // 현재 클래스가 가지고 있는 값의 유효성 검사
        public void isValid() {
            isValidSignupid(signupid);
            isValidPassword(password);
        }
    }

    // 회원가입용 클래스
    @Data
    public static class SignUp {
        private String signupid; // 아이디
        private String password; // 비밀번호
        private String nickname; // 닉네임

        // 현재 클래스가 가지고 있는 값의 유효성 검사
        public void isValid(boolean isUserSignup) {
            isValidSignupid(signupid);
            isValidPassword(password);
            isValidNickname(nickname, isUserSignup);
        }

        // 현재 가지고 있는 값들을 기반으로 Member 객체 반환
        public Member toEntity(boolean isUserSignup) {
            return Member.builder()
                    .signupid(this.signupid)
                    .password(this.password)
                    .nickname(this.nickname)
                    .role(isUserSignup ? MemberRole.ROLE_USER : MemberRole.ROLE_ADMIN) // 조건에 맞는 유저 속성을 넣기
                    .insertdate(LocalDateTime.now()) // 현재 시간을 넣기
                    .status(MemberStatus.UNLOCK)
                    .build();
        }
    }

    // 닉네임 변경용 클래스
    @Data
    public static class ChangeNickname {
        private String nickname;

        public void isValid(MemberRole memberRole) {
            isValidNickname(this.nickname, memberRole == MemberRole.ROLE_USER ? true : false);
        }
    }

    // 패스워드 변경용 클래스
    @Data
    public static class ChangePassword {
        private String password;

        public void isValid() {
            isValidPassword(this.password);
        }
    }

    // 관리자가 잠그려는 아이디 정보를 불러올 때 사용하는 클래스
    @Data
    public static class IdClass {
        private String signupid;

        public void isValid() {
            isValidSignupid(signupid);
        }
    }

    // 계정의 인덱스 번호(id), 아이디(signupid)를 가져올 때 사용하는 클래스
    public interface IdInterface {
        int getId();
        String getSignupid();
    }

    // 아이디 유효성 검사
    static private void isValidSignupid(String signupid) {
        final int minLength_signupid = 6; // 아이디 최소 길이
        final int maxLength_signupid = 20; // 아이디 최대 길이

        // 아이디 유효성 검사
        if(signupid == null || signupid.length() < minLength_signupid || signupid.length() > maxLength_signupid) {
            throw new RuntimeException("아이디의 길이는 " + minLength_signupid + "이상 " + maxLength_signupid + "이하여야 합니다.");
        }
    }

    // 패스워드 유효성 검사
    static private void isValidPassword(String password) {
        final int minLength_password = 8; // 비밀번호 최소 길이
        final int maxLength_password = 20; // 비밀번호 최대 길이

        // 비밀번호 유효성 검사
        if(password == null || password.length() < minLength_password ||password.length() > maxLength_password) {
            throw new RuntimeException("패스워드의 길이는 " + minLength_password + "이상 " + maxLength_password + "이하여야 합니다.");
        }
    }

    // 닉네임 유효성 검사
    static private void isValidNickname(String nickname, boolean isUserSignup) {
        if(isUserSignup) { // 일반 사용자 계정 생성
            // 닉네임 초성 검사 (ADMIN_으로 시작할 수 없다.)
            if(nickname.indexOf(firstAdminNickname) == 0) {
                throw new RuntimeException("닉네임은 '" + firstAdminNickname + "'으로 시작할 수 없습니다.");
            }
        } else { // 관리자 계정 생성
            // 닉네임 초성 검사 (ADMIN_으로 시작해야 한다.)
            if(nickname.indexOf(firstAdminNickname) != 0) {
                throw new RuntimeException("관리자의 닉네임은 '" + firstAdminNickname + "'으로 시작해야 합니다.");
            }
        }

        final int minLength_nickname = 2; // 닉네임 최소 길이
        final int maxLength_nickname = 12; // 닉네임 최대 길이

        // 닉네임 유효성 검사
        if(nickname == null || nickname.length() < minLength_nickname || nickname.length() > maxLength_nickname) {
            throw new RuntimeException("닉네임의 길이는 " + minLength_nickname + "이상 " + maxLength_nickname + "이하여야 합니다.");
        }
    }
}
