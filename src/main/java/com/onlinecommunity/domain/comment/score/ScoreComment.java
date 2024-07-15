package com.onlinecommunity.domain.comment.score;

import com.onlinecommunity.domain.ScoreStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

@Builder
@Entity(name = "score_comment")
@DynamicUpdate
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ScoreComment {
    // 고유번호
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // 댓글 아이디
    @Column(name = "comment_id")
    private int commentId;

    // 사용자 아이디
    @Column(name = "user_id")
    private int userId;

    @Enumerated(EnumType.STRING)
    private ScoreStatus status;

    public void changeScore(boolean isLike) {
        status = ScoreStatus.changeScoreStatus(status, isLike);
    }
}
