package com.onlinecommunity.domain.post.score;

import com.onlinecommunity.domain.ScoreStatus;
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
    private ScoreStatus status;

    public void changeScore(boolean isLike) {
        status = ScoreStatus.changeScoreStatus(status, isLike);
    }

}
