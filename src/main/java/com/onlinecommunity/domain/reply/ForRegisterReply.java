package com.onlinecommunity.domain.reply;

import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ForRegisterReply {
    @PositiveOrZero
    private int commentId; // 댓글

    @Size(min=1, max=1000)
    private String context; // 내용

    public Reply toEntity(int userId) {
        LocalDateTime nowTime = LocalDateTime.now();

        return Reply.builder()
                .commentId(this.commentId)
                .userId(userId)
                .context(this.getContext())
                .insertDate(nowTime)
                .updateDate(nowTime)
                .likeScore(0)
                .dislikeScore(0)
                .build();
    }
}
