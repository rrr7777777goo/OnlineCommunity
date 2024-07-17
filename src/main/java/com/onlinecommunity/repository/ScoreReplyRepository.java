package com.onlinecommunity.repository;

import com.onlinecommunity.domain.reply.score.ScoreReply;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScoreReplyRepository extends JpaRepository<ScoreReply, Integer> {
    ScoreReply findAllByReplyIdAndUserId(int replyId, int userId);
}
