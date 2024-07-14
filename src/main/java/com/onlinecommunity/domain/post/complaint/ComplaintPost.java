package com.onlinecommunity.domain.post.complaint;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

@Builder
@Entity(name = "complaint_post")
@DynamicUpdate
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ComplaintPost {
    // 고유번호
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // 게시글 아이디
    @Column(name = "post_id")
    private int postId;

    // 사용자 아이디
    @Column(name = "user_id")
    private int userId;

    // 신고 내용
    private String context;

    // 삽입날짜 (신고글 생성 날짜를 의미)
    @Column(name = "insertdate")
    private LocalDateTime insertDate;
}
