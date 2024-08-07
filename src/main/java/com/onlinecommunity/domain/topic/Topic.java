package com.onlinecommunity.domain.topic;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

@Builder
@Entity(name = "topic")
@DynamicUpdate
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Topic {
    // 고유번호
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // 주제 이름
    private String name;

    // 생성날짜 (주제 생성 날짜를 의미)
    @Column(name = "insertdate")
    private LocalDateTime insertDate;

    // 이름 변경용 함수
    public void changeName(String name) {
        this.name = name;
    }
}
