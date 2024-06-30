package com.onlinecommunity.domain.member;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Auth {

    // 로그인용 클래스
    @Data
    public static class SignIn {
        @Pattern(regexp="[a-z0-9][a-z0-9_-]{5,19}", message = "아이디 작성은 첫번째 문자부터는 알파벳 소문자와 숫자가 사용이 가능하며 두번째 문자부터는 _와 -도 사용이 가능합니다. 그리고 길이 제한은 6자리 이상, 20자리 이하입니다. ")
        private String signupid; // 아이디
        @Pattern(regexp="(?=.*[0-9])(?=.*[a-zA-Z])[a-zA-Z0-9!?@#$%^&*().,_-]{8,20}", message = "비밀번호는 영어(대소문자 모두 가능)와 숫자, 특수문자(!?@#$%^&*().,_-)를 이용해서 작성해야 하며 이 때 영어와 숫자를 최소 1개씩 포함되게 작성해야 합니다. 그리고 길이 제한은 8자리 이상, 20자리 이하입니다. ")
        private String password; // 비밀번호
    }

    // 회원가입용 클래스
    @Data
    public static class SignUp {
        @Pattern(regexp="[a-z0-9][a-z0-9_-]{5,19}", message = "아이디 작성은 첫번째 문자부터는 알파벳 소문자와 숫자가 사용이 가능하며 두번째 문자부터는 _와 -도 사용이 가능합니다. 그리고 길이 제한은 6자리 이상, 20자리 이하입니다. ")
        private String signupid; // 아이디
        @Pattern(regexp="(?=.*[0-9])(?=.*[a-zA-Z])[a-zA-Z0-9!?@#$%^&*().,_-]{8,20}", message = "비밀번호는 영어(대소문자 모두 가능)와 숫자, 특수문자(!?@#$%^&*().,_-)를 이용해서 작성해야 하며 이 때 영어와 숫자를 최소 1개씩 포함되게 작성해야 합니다. 그리고 길이 제한은 8자리 이상, 20자리 이하입니다. ")
        private String password; // 비밀번호
        @Pattern(regexp="[A-Za-z0-9가-힣_-]{2,12}", message = "닉네임은 한글,영어(대소문자 모두 가능) ,숫자, 특수문자 _와 -를 조합해서 작성이 가능합니다. 그리고 길이 제한은 2자리 이상, 12자리 이하입니다. ")
        private String nickname; // 닉네임


        // 현재 가지고 있는 값들을 기반으로 Member 객체 반환
        public Member toEntity(boolean isAdminSignup) {
            return Member.builder()
                    .signupid(this.signupid)
                    .password(this.password)
                    .nickname(this.nickname)
                    .role(isAdminSignup ? MemberRole.ROLE_ADMIN : MemberRole.ROLE_USER) // 조건에 맞는 유저 속성을 넣기
                    .insertdate(LocalDateTime.now()) // 현재 시간을 넣기
                    .status(MemberStatus.UNLOCK)
                    .build();
        }
    }

    // 계정의 패스워드 또는 닉네임 변경용 클래스
    @Data
    public static class Change {
        @Pattern(regexp="(?=.*[0-9])(?=.*[a-zA-Z])[a-zA-Z0-9!?@#$%^&*().,_-]{8,20}", message = "비밀번호는 영어(대소문자 모두 가능)와 숫자, 특수문자(!?@#$%^&*().,_-)를 이용해서 작성해야 하며 이 때 영어와 숫자를 최소 1개씩 포함되게 작성해야 합니다. 그리고 길이 제한은 8자리 이상, 20자리 이하입니다. ")
        private String password;
        @Pattern(regexp="[A-Za-z0-9가-힣_-]{2,12}", message = "닉네임은 한글,영어(대소문자 모두 가능) ,숫자, 특수문자 _와 -를 조합해서 작성이 가능합니다. 그리고 길이 제한은 2자리 이상, 12자리 이하입니다. ")
        private String nickname;
    }


    // 관리자가 잠그려는 아이디 정보를 불러올 때 사용하는 클래스
    @Data
    public static class IdClass {
        @Pattern(regexp="[a-z0-9][a-z0-9_-]{5,19}", message = "아이디 작성은 첫번째 문자부터는 알파벳 소문자와 숫자가 사용이 가능하며 두번째 문자부터는 _와 -도 사용이 가능합니다. 그리고 길이 제한은 6자리 이상, 20자리 이하입니다. ")
        private String signupid;
    }

    // 계정의 인덱스 번호(id), 아이디(signupid)를 가져올 때 사용하는 클래스
    public interface IdInterface {
        int getId();
        String getSignupid();
    }
}
