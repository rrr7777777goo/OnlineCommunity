package com.onlinecommunity.domain.reply.complaint;

import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ForRegisterComplaintReply {
    @PositiveOrZero
    private int replyId; // 대댓글 아이디
    @Size(min=10, max=1000)
    private String context; // 내용

    public ComplaintReply toEntity(int userId) {
        return ComplaintReply.builder()
                .replyId(this.replyId)
                .userId(userId)
                .context(this.context)
                .insertDate(LocalDateTime.now())
                .build();
    }
}
