package com.onlinecommunity.domain.comment.complaint;

import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ForRegisterComplaintComment {
    @PositiveOrZero
    private int commentId; // 댓글 아이디
    @Size(min=10, max=1000)
    private String context; // 내용

    public ComplaintComment toEntity(int userId) {
        return ComplaintComment.builder()
                .commentId(this.commentId)
                .userId(userId)
                .context(this.context)
                .insertDate(LocalDateTime.now())
                .build();
    }
}
