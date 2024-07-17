package com.onlinecommunity.domain.comment.complaint;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class ForDeleteComplaintComment {
    @PositiveOrZero
    private int id; // 댓글 신고글 아이디
}
