package com.onlinecommunity.domain.comment;

import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ForRegisterComment {
    @PositiveOrZero
    private int postId; // 게시글

    @Size(min=1, max=1000)
    private String context; // 내용

    public Comment toEntity(int userId) {
        LocalDateTime nowTime = LocalDateTime.now();

        return Comment.builder()
                .postId(this.postId)
                .userId(userId)
                .context(this.getContext())
                .insertDate(nowTime)
                .updateDate(nowTime)
                .likeScore(0)
                .dislikeScore(0)
                .build();
    }
}
