package com.onlinecommunity.domain.reply.complaint;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class ForDeleteComplaintReply {
    @PositiveOrZero
    private int id; // 대댓글 신고글 아이디
}
