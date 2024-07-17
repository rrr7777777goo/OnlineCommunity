package com.onlinecommunity.domain.comment;

import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ForUpdateComment {
    @PositiveOrZero
    private int id; // 댓글 아이디
    @Size(min=1, max=1000)
    private String context; // 내용
}
