package com.onlinecommunity.domain.reply;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class ForDeleteReply {
    @PositiveOrZero
    private int id; // 대댓글 아이디
}
