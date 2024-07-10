package com.onlinecommunity.domain.post.score;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

@Builder
@Entity(name = "score_post")
@DynamicUpdate
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ScorePost {
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

    @Enumerated(EnumType.STRING)
    private ScorePostStatus status;

    public void changeScore(boolean isLike) {
        if(isLike) {
            if(status == ScorePostStatus.SCORE_LIKE) {
                throw new RuntimeException("이미 해당 게시글에 좋아요를 누른 상태입니다.");
            }
            status = ScorePostStatus.SCORE_LIKE;
        } else {
            if(status == ScorePostStatus.SCORE_DISLIKE) {
                throw new RuntimeException("이미 해당 게시글에 싫아요를 누른 상태입니다.");
            }
            status = ScorePostStatus.SCORE_DISLIKE;
        }
    }

}
