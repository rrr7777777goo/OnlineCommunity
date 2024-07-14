package com.onlinecommunity.domain.post;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

@Builder
@Entity(name = "post")
@DynamicUpdate
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Post {
    // 고유번호
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // 주제 아이디
    @Column(name = "topic_id")
    private int topicId;

    // 사용자 아이디
    @Column(name = "user_id")
    private int userId;

    // 제목
    private String title;

    // 내용
    private String context;

    // 조회수
    @Column(name = "viewcount")
    private Integer viewCount;

    // 삽입날짜 (게시글 생성 날짜를 의미)
    @Column(name = "insertdate")
    private LocalDateTime insertDate;

    // 수정날짜 (게시글 수정 날짜를 의미, 처음 생성시에는 삽입날짜랑 같은 시간이다.)
    @Column(name = "updatedate")
    private LocalDateTime updateDate;

    // 좋아요 점수
    @Column(name = "likescore")
    private int likeScore;

    // 싫어요 점수
    @Column(name = "dislikescore")
    private int dislikeScore;

    public void plusViewCount() {
        ++viewCount;
    }

    public void updatePost(String title, String context) {
        this.title = title;
        this.context = context;
        updateDate = LocalDateTime.now();
    }

    public void plusLikeScore() {
        ++likeScore;
    }

    public void plusDislikeScore() {
        ++dislikeScore;
    }

    public void minusLikeScore() {
        --likeScore;
    }

    public void minusDislikeScore() {
        --dislikeScore;
    }

    public void changeLikeScore(boolean isLike) {
        if(isLike) {
            --dislikeScore;
            ++likeScore;
        } else {
            --likeScore;
            ++dislikeScore;
        }
    }
}
