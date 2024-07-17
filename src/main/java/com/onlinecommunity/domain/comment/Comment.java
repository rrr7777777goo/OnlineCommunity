package com.onlinecommunity.domain.comment;

import com.onlinecommunity.domain.ScoreStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

@Builder
@Entity(name = "comment")
@DynamicUpdate
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
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

    // 내용
    private String context;

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

    public void updateComment(String context) {
        this.context = context;
        updateDate = LocalDateTime.now();
    }

    public void plusLikeScore() {
        likeScore = ScoreStatus.plusScore(likeScore);
    }

    public void plusDislikeScore() {
        dislikeScore = ScoreStatus.plusScore(dislikeScore);
    }

    public void minusLikeScore() {
        likeScore = ScoreStatus.minusScore(likeScore);
    }

    public void minusDislikeScore() {
        dislikeScore = ScoreStatus.minusScore(dislikeScore);
    }

    public void changeLikeScore(boolean isLike) {
        if (isLike) {
            dislikeScore = ScoreStatus.minusScore(dislikeScore);
            likeScore = ScoreStatus.plusScore(likeScore);
        } else {
            likeScore = ScoreStatus.minusScore(likeScore);
            dislikeScore = ScoreStatus.plusScore(dislikeScore);
        }
    }
}
