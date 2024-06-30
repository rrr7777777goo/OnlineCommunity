package com.onlinecommunity.domain.member;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Entity(name = "user")
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Member {
    // 고유번호
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // 아이디 (6 ~ 20자)
    private String signupid;

    // 비밀번호(8 ~ 20자)
    @JsonIgnore
    private String password;

    // 닉네임(2 ~ 12자)
    private String nickname;

    // 역할 (일반 사용자(ROLE_USER) 또는 관리자(ROLE_ADMIN))
    @Enumerated(EnumType.STRING)
    private MemberRole role;

    // 삽입날짜 (계정 생성 날짜를 의미)
    private LocalDateTime insertdate;

    // 계정 상태(LOCK이면 잠금, UNLOCK이면 비잠금 상태를 의미한다. 계정이 잠겨 있으면 그 계정은 사용할 수 없다.)
    @Enumerated(EnumType.STRING)
    private MemberStatus status;

    // 계정 잠금 상태 여부 확인
    public void checkStatus() {
        if(status == MemberStatus.LOCK) {
            throw new RuntimeException("현재 계정은 잠금상태입니다. 관리자에게 문의하십시오");
        }
    }

    // 닉네임, 패스워드를 변경하기 위해서 사용하는 함수
    public void change(String password, String nickname) {
        this.password = password;
        this.nickname = nickname;
    }

    // 계정 잠금
    public void lockAccount() {
        if (this.role == MemberRole.ROLE_ADMIN) {
            throw new RuntimeException("관리자 계정은 잠금 관련 설정을 할 수 없습니다.");
        }
        if (this.status == MemberStatus.LOCK) {
            throw new RuntimeException("현재 계정은 이미 잠금 상태입니다.");
        }
        this.status = MemberStatus.LOCK;
    }

    // 계정 잠금 해제
    public void unLockAccount() {
        if (this.role == MemberRole.ROLE_ADMIN) {
            throw new RuntimeException("관리자 계정은 잠금 관련 설정을 할 수 없습니다.");
        }
        if (this.status == MemberStatus.UNLOCK) {
            throw new RuntimeException("현재 계정은 이미 잠금 해제 상태입니다.");
        }
        this.status = MemberStatus.UNLOCK;
    }
}
