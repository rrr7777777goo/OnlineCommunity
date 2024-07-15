package com.onlinecommunity.domain.reply;

import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ForUpdateReply {
    @PositiveOrZero
    private int id; // 대댓글 아이디
    @Size(min=1, max=1000)
    private String context; // 내용
}
