package com.onlinecommunity.domain.comment;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class ForDeleteComment {
    @PositiveOrZero
    private int id; // 댓글 아이디
}
