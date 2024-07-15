package com.onlinecommunity.repository;

import com.onlinecommunity.domain.comment.score.ScoreComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScoreCommentRepository extends JpaRepository<ScoreComment, Integer> {
    ScoreComment findAllByCommentIdAndUserId(int commentId, int userId);
}
