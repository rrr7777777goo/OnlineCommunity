package com.onlinecommunity.repository;

import com.onlinecommunity.domain.post.score.ScorePost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScorePostRepository extends JpaRepository<ScorePost, Integer> {
    ScorePost findAllByPostIdAndUserId(int postId, int userId);

}
