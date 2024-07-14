package com.onlinecommunity.domain.post.complaint;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class ForDeleteComplaintPost {
    @PositiveOrZero
    private int id; // 게시글 신고글 아이디
}
